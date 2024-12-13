package tech_tutors;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/searchServlet/*")
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson = new Gson();
       
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        
        // Getting all courses
        if (pathInfo == null || pathInfo.equals("/")) {
            ArrayList<Course> courses = DBHandler.getAllCourses();
            out.print(gson.toJson(courses));
        }
        // Getting tutors for a specific course
        else if (pathInfo.equals("/tutors")) {
            String courseID = request.getParameter("courseID");
            if (courseID != null) {
                int id = Integer.parseInt(courseID);
                ArrayList<ProfileInfo> tutors = DBHandler.tutorsForCourse(id);
                out.print(gson.toJson(tutors));
               
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson("Invalid course ID"));
            }
        }
        // Starting a chat
        else if (pathInfo.equals("/startChat")) {
            int studentID = Integer.parseInt(request.getParameter("studentID"));
            int tutorID = Integer.parseInt(request.getParameter("tutorID"));
            int result = DBHandler.startChat(tutorID, studentID);
            if (result != -1) {
            	out.print(gson.toJson(result));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson("Invalid parameters"));
            }
        }
    }
}
