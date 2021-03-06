package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apiController.auth_api.OauthController;
import config.FileUploadPathConfig;
import config.RequestMethod;
import entity.JwtProperties;
import entity.NonReadActivities;
import entity.SecurityContext;
import entity.User;
import exception.JWTRegisterException;
import repository.ArticleDao;
import repository.ArticleDaoImpl;
import repository.FollowDao;
import repository.FollowDaoImpl;
import repository.MessageDao;
import repository.MessageDaoImpl;
import repository.NewActivityDao;
import repository.NewActivityDaoImpl;
import repository.SearchDao;
import repository.SearchDaoImpl;
import repository.StoryDao;
import repository.StoryDaoImpl;
import repository.UserDao;
import repository.UserDaoImpl;
import service.AuthServiceImpl;
import service.NewActivityServiceImpl;

public class SessionFilter implements Filter {
	
	private SecurityContext security;
	private UserDao userDao;
	private NewActivityDao newActivityDao;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("session filter initialize!");
		ServletContext servletContext = filterConfig.getServletContext();
		FileUploadPathConfig.setFile_upload_path(filterConfig.getServletContext().getRealPath("/static/file_upload"));
		FileUploadPathConfig.setLog_file_path(filterConfig.getServletContext().getRealPath("/logs"));
		System.out.println(FileUploadPathConfig.getFileUploadPath());
		try {
			DBLoggingFilter.readLogFile(FileUploadPathConfig.getLogFilePath() + "/");
		} catch (IOException e) {
			System.out.println("log file creation Exception");
		}
		userDao = DBLoggingFilter.makeNewProxy(new UserDaoImpl());
		ArticleDao articleDao = DBLoggingFilter.makeNewProxy(new ArticleDaoImpl());
		FollowDao followDao = DBLoggingFilter.makeNewProxy(new FollowDaoImpl());
		StoryDao storyDao = DBLoggingFilter.makeNewProxy(new StoryDaoImpl());
		SearchDao searchDao = DBLoggingFilter.makeNewProxy(new SearchDaoImpl());
		MessageDao messageDao = DBLoggingFilter.makeNewProxy(new MessageDaoImpl());
		newActivityDao = DBLoggingFilter.makeNewProxy(new NewActivityDaoImpl());
		NonReadActivities.createInstance();
		servletContext.setAttribute("userDao", userDao);
		servletContext.setAttribute("articleDao", articleDao);
		servletContext.setAttribute("followDao", followDao);
		servletContext.setAttribute("storyDao", storyDao);
		servletContext.setAttribute("searchDao", searchDao);
		servletContext.setAttribute("messageDao", messageDao);
		servletContext.setAttribute("newActivityDao", newActivityDao);
		security = SecurityContext.createInstance(userDao);
		System.out.println("session filter initialize finish! ----------------------------");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		String method = req.getMethod();
		if(uri.equals("/")) return;
		if( uri.contains("static") || uri.contains("templates") || uri.contains("favicon") ||
			  (uri.contains("security") && method.equals(RequestMethod.GET)) ||
			  (uri.equals("/index") && method.equals(RequestMethod.GET)) ||
			  (uri.equals("/auth/userinfo") && method.equals(RequestMethod.GET)) ||
			  (uri.equals("/oauth/signin") && method.equals(RequestMethod.GET)) ||
			  (uri.contains("signup") && method.equals(RequestMethod.POST)) ) {
			chain.doFilter(request, response);
		} else if(uri.contains("/oauth/signin")) {
			System.out.println("?????? ?????????");
			System.out.println(uri + " - " + method);
			String code = request.getParameter("code");
			String provider = uri.replace("/oauth/signin/", "");
			System.out.println("code : " + code);
			System.out.println("provider : " + provider);
			request.setAttribute("provider", provider);
			OauthController oauthController = new OauthController(
																						new AuthServiceImpl(userDao), 
																						new NewActivityServiceImpl(newActivityDao));
			User user = oauthController.OauthSignin(provider, code);
			// oauth ????????? ?????? ?????? ??????
			System.out.println("after oauth signin ??????");
			if(user == null) {
				System.out.println("user == null");
				resp.setContentType("text/html; charset=UTF-8");
				resp.getWriter().print("<script>alert(\"" + provider + "???????????? ??????????????????. ?????????????????? ???????????????.\"); location.href=\"/index\";</script>");
			} else {
				System.out.println("user != null");
				req.getSession().setAttribute("user_secret_key", user.getSecret_key());
				try {
					String token = security.issueToken(user, user.getSecret_key());
					System.out.println("issueToken : " + token);
					System.out.println("userSecretKey : " + user.getSecret_key());
					System.out.println("true!");
					resp.sendRedirect("/main");
				} catch (JWTRegisterException e) {
					resp.setContentType("text/html; charset=UTF-8");
					resp.getWriter().print("<script>alert(\"" + provider + "???????????? ??????????????????. ?????????????????? ???????????????.\"); location.href=\"/index\";</script>");
				}
			}
		} else if(uri.contains("signin")) {
			// ?????? ?????? ??? ?????? ??????
			chain.doFilter(request, response);
			User user = (User) request.getAttribute("user");
			if(user == null) {
				resp.sendError(400, "wrong username or password");
			} else {
				req.getSession().setAttribute("user_secret_key", user.getSecret_key());
				resp.setContentType("text/plain; charset=UTF-8");
				try {
					String token = security.issueToken(user, user.getSecret_key());
					System.out.println("issueToken : " + token);
					System.out.println("userSecretKey : " + user.getSecret_key());
					resp.getWriter().print(true);
				} catch (JWTRegisterException e) {
					resp.getWriter().print(false);
				}
			}
		} else if((uri.equals("/direct/message") && method.equals(RequestMethod.GET)) ||
						  uri.equals("/main") && method.equals(RequestMethod.GET) ||
						  uri.startsWith("/profile") && method.equals(RequestMethod.GET) ||
						  uri.contains("/search") && method.equals(RequestMethod.GET) ||
						  uri.contains("/logout") && method.equals(RequestMethod.GET) ||
						  uri.contains("/userinfo") && method.equals(RequestMethod.GET)) {
			// location.href ????????? ????????? ??????????????? sessionId ??? UUID -> token ?????? ??? ???????????? ?????? dofilter ????????? session.invalidate() -> sendError -> alert -> location.replace(/index) 
			String user_secret_key = (String) req.getSession().getAttribute("user_secret_key");
			if(security.isLoginedSession(user_secret_key)) {
				String token = security.getToken(user_secret_key);
				User sessionUser = security.certificateUser(token, user_secret_key);
				request.setAttribute("sessionUser", sessionUser);
				chain.doFilter(request, response);
			} else {
				resp.sendRedirect("/index");
			}
		} else {
			// ???????????? ?????? ????????? ?????? ??? response
			if(!uri.contains("security") && !uri.contains("principal") && !uri.contains("alert")) {
				System.out.println("------------------------------------");
				System.out.println("request URI : " + uri + " - " + method);
			}
			String token = req.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, "");
			String user_secret_key = (String) req.getSession().getAttribute("user_secret_key");
			System.out.println("verifyToken : " + token);
			User sessionUser = security.certificateUser(token, user_secret_key);
			if(sessionUser != null) {
				request.setAttribute("sessionUser", sessionUser);
				chain.doFilter(request, response);
			} else {
				resp.setContentType("text/plain; charset=UTF-8");
				resp.sendError(420, "requested User has no Authorization!");
			}
		}
	}
}
