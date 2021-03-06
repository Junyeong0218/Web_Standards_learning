package filter.controller_dispatcher;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import config.RequestMethod;

public class FollowControllersParamFilter implements Filter {
	
	private final String FOLLOW_USER = "/follow/user";
	private final String FOLLOW_HASHTAG = "/follow/hashtag";
	private final String FOLLOWERS = "/follow/followers";
	private final String FOLLOWINGS = "/follow/followings";
	private final String FOLLOW_RECOMMENDATION = "/follow/recommendation";
	private final String ACTIVITIES = "/follow/activities";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		String method = req.getMethod();
		
		if(uri.equals(FOLLOW_USER) || uri.equals(FOLLOW_HASHTAG) || uri.equals(FOLLOWERS) || uri.equals(FOLLOWINGS) || uri.equals(ACTIVITIES)) {
			chain.doFilter(request, response);
			return;
		} else if(uri.equals(FOLLOW_RECOMMENDATION) && method.equals(RequestMethod.GET)) {
			chain.doFilter(request, response);
			return;
		} else if(uri.equals("/follow")) {
			resp.sendError(404, "bad request");
		}
		
		uri = uri.replaceFirst("/follow", "");
		String[] uris = uri.split("/");
		
		try {
			if(uris[1].equals("user")) {
				int partner_user_id = Integer.parseInt(uris[2]);
				request.setAttribute("partner_user_id", partner_user_id);
				
				if(method.equals(RequestMethod.POST) ||
					method.equals(RequestMethod.DELETE)) {
					request.getRequestDispatcher(FOLLOW_USER).forward(request, response);
				} else {
					resp.sendError(404, "not supported Method");
				}
			} else if(uris[1].equals("hashtag")) {
				if(method.equals(RequestMethod.GET)) {
					int page_indicator = Integer.parseInt(uris[2]);
					request.setAttribute("page_indicator", page_indicator);
					request.getRequestDispatcher(FOLLOW_HASHTAG).forward(request, response);
				} else if(method.equals(RequestMethod.POST) ||
								method.equals(RequestMethod.DELETE)) {
					
					int hash_tag_id = Integer.parseInt(uris[2]);
					request.setAttribute("hash_tag_id", hash_tag_id);
					request.getRequestDispatcher(FOLLOW_HASHTAG).forward(request, response);
				} else {
					resp.sendError(404, "not supported Method");
				}
			} else if(uris[1].equals("followers")) {
				int page_indicator = Integer.parseInt(uris[2]);
				request.setAttribute("page_indicator", page_indicator);
				
				if(method.equals(RequestMethod.GET)) {
					request.getRequestDispatcher(FOLLOWERS).forward(request, response);
				} else {
					resp.sendError(404, "not supported Method");
				}
			} else if(uris[1].equals("followings")) {
				int page_indicator = Integer.parseInt(uris[2]);
				request.setAttribute("page_indicator", page_indicator);
				
				if(method.equals(RequestMethod.GET)) {
					request.getRequestDispatcher(FOLLOWINGS).forward(request, response);
				} else {
					resp.sendError(404, "not supported Method");
				}
			} else {
				resp.sendError(404, "bad request");
			}
		} catch (NumberFormatException e) {
			System.out.println("Integer parseInt parsing error occured!");
			resp.sendError(409, "invalid uri");
		}
	}
}
