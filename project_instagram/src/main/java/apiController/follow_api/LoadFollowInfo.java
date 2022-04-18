package apiController.follow_api;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entity.User;
import repository.FollowDao;
import response_dto.FollowSummaryResDto;
import service.FollowService;
import service.FollowServiceImpl;

@WebServlet("/follow/load-follow-info")
public class LoadFollowInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private FollowService followService;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(getServletConfig());
		ServletContext servletContext = config.getServletContext();
		followService = new FollowServiceImpl((FollowDao) servletContext.getAttribute("followDao"));
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User sessionUser = (User) session.getAttribute("user");
		
		FollowSummaryResDto resDto = followService.selectFollowSummary(sessionUser.getId());
		
		response.setContentType("text/plain; charset=UTF-8");
		response.getWriter().print("{ \"following\": \"" + resDto.getFollowing() + "\", " + 
																"\"follower\": \"" + resDto.getFollower() + "\" }");
	}
}