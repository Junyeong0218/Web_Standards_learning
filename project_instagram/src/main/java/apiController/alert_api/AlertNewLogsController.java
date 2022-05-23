package apiController.alert_api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entity.JwtProperties;
import entity.NonReadActivities;
import entity.SecurityContext;
import entity.User;

@WebServlet("/alert/new-logs")
public class AlertNewLogsController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User sessionUser = SecurityContext.certificateUser(request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, ""));
		response.setContentType("text/plain; charset=UTF-8");
		System.out.println(NonReadActivities.isChanged(sessionUser.getId()));
		if(NonReadActivities.isChanged(sessionUser.getId()) != null) {
			String array = NonReadActivities.getNonReadMessage(sessionUser.getId());
			System.out.println(array);
			response.getWriter().print(array);
		}
	}
}
