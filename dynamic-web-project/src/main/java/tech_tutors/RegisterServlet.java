package tech_tutors;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		PrintWriter writer = response.getWriter();
		RegisterRequest req = gson.fromJson(request.getReader(), RegisterRequest.class);
		response.setStatus(HttpServletResponse.SC_OK);
		if (DBHandler.isEmailTaken(req.email)) {
			writer.write(gson.toJson(-1));
			writer.flush();
		} else {
			int id = DBHandler.registerUser(req.email, req.password, req.fname, req.lname, req.standing, req.major);
			writer.write(gson.toJson(id));
			writer.flush();
		}
	}
}

class RegisterRequest {
	public String email;
	public String password;
	public String fname;
	public String lname;
	public String standing;
	public String major;
}
