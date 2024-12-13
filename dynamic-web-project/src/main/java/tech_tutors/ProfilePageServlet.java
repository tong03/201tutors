package tech_tutors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.google.gson.Gson;


@WebServlet("/ProfilePage")
public class ProfilePageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


    public ProfilePageServlet() {
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	
	    String userIDParam = request.getParameter("userID");
	    try {
	        if (userIDParam == null || userIDParam.isEmpty()) {
	            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	            System.out.println("Missing userID parameter");
	            return;
	        }
	
	        int userID = Integer.parseInt(userIDParam);
	
	        ProfileInfo profile = DBHandler.getProfile(userID);
	
	        if (profile != null) {
	            String json = new Gson().toJson(profile);
	            response.getWriter().write(json);
	        } else {
	            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	            System.out.println("Profile not found");
	        }
	    } catch (NumberFormatException e) {
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        System.out.println("Invalid userID format");
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        System.out.println("Internal server error");
	    }
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.addHeader("Access-Control-Allow-Origin", "*");
	    response.addHeader("Access-Control-Allow-Methods", "POST, GET");
		response.setCharacterEncoding("UTF-8");
		
	    try {
	        ProfileInfo pInfo = new Gson().fromJson(request.getReader(), ProfileInfo.class);

	        if (pInfo == null || pInfo.id <= 0) {
	            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	            System.out.println("Invalid profile data or missing userID.");
	            return;
	        }

	        boolean success = DBHandler.changeProfile(pInfo.id, pInfo);

	        if (success) {
	            response.setStatus(HttpServletResponse.SC_OK);
	            response.getWriter().write("{\"message\": \"Profile updated successfully.\"}");
	            System.out.println("Profile updated successfully.");
	        } else {
	            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	            response.getWriter().write("{\"error\": \"Failed to update profile.\"}");
	            System.out.println("Failed to update profile.");
	        }
	    }  catch (NumberFormatException e) {
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        System.out.println("Invalid userID format");
	    } catch (Exception e) {
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        System.out.println("Internal server error: " + e.getMessage());
	        e.printStackTrace();
	    }
		
	}

}
