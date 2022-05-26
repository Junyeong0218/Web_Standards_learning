package apiController.auth_api;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entity.User;
import repository.UserDao;
import service.AuthService;
import service.AuthServiceImpl;

@WebServlet("/auth/principal")
public class PrincipalController extends HttpServlet{
	private static final long serialVersionUID = 1L;

	private AuthService authService;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext servletContext = config.getServletContext();
		authService = new AuthServiceImpl((UserDao) servletContext.getAttribute("userDao"));
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User sessionUser = (User) request.getAttribute("sessionUser");
		User userDetail = authService.getUserById(sessionUser.getId());
		StringBuilder sb = new StringBuilder();
		sb.append(" { \"id\": \"" + userDetail.getId() + "\", " + 
							"\"username\": \"" + userDetail.getUsername() + "\", " + 
							"\"name\": \"" + userDetail.getName() + "\", " + 
							"\"email\": \"" + userDetail.getEmail() + "\", " + 
							"\"phone\": \"" + userDetail.getPhone() + "\", " + 
							"\"has_profile_image\": \"" + userDetail.isHas_profile_image() + "\", " + 
							"\"file_name\": \"" + userDetail.getFile_name() + "\" }");
		
		response.setContentType("text/plain; charset=UTF-8");
		response.getWriter().print(sb.toString());
	}
}
