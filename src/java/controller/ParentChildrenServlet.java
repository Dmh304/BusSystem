package controller;

import dal.RouteDAO;
import dal.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.UserAccount;
import util.SessionUtil;

public class ParentChildrenServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));

        StudentDAO studentDAO = new StudentDAO();
        RouteDAO routeDAO = new RouteDAO();

        request.setAttribute("pageTitle", "My Children");
        request.setAttribute("students", studentDAO.getStudentsByParent(user.getUserId()));
        request.setAttribute("routes", routeDAO.getAllRoutes());
        request.setAttribute("stopPoints", routeDAO.getAllStopPoints());
        request.getRequestDispatcher("/parent/children.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        String action = request.getParameter("action");
        StudentDAO studentDAO = new StudentDAO();
        RouteDAO routeDAO = new RouteDAO();

        try {
            if ("add".equals(action)) {
                String studentCode = request.getParameter("studentCode");
                String fullName = request.getParameter("fullName");
                String gender = request.getParameter("gender");
                String grade = request.getParameter("grade");
                int routeId = Integer.parseInt(request.getParameter("routeId"));
                int pickupStopId = Integer.parseInt(request.getParameter("pickupStopId"));
                int dropoffStopId = routeDAO.getSchoolStopId(routeId);

                boolean ok = studentDAO.addStudent(studentCode, fullName, gender, grade,
                        user.getUserId(), routeId, pickupStopId, dropoffStopId);
                if (ok) SessionUtil.setSuccess(request.getSession(), "Child added successfully.");
                else SessionUtil.setError(request.getSession(), "Failed to add child.");
            } else if ("edit".equals(action)) {
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                String fullName = request.getParameter("fullName");
                String gender = request.getParameter("gender");
                String grade = request.getParameter("grade");
                int routeId = Integer.parseInt(request.getParameter("routeId"));
                int pickupStopId = Integer.parseInt(request.getParameter("pickupStopId"));
                int dropoffStopId = routeDAO.getSchoolStopId(routeId);

                boolean ok = studentDAO.updateStudent(studentId, fullName, gender, grade,
                        routeId, pickupStopId, dropoffStopId);
                if (ok) SessionUtil.setSuccess(request.getSession(), "Child updated successfully.");
                else SessionUtil.setError(request.getSession(), "Failed to update child.");
            } else if ("delete".equals(action)) {
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                boolean ok = studentDAO.deleteStudent(studentId);
                if (ok) SessionUtil.setSuccess(request.getSession(), "Child deleted successfully.");
                else SessionUtil.setError(request.getSession(), "Cannot delete child. They might have associated records.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            SessionUtil.setError(request.getSession(), "An error occurred: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/parent/children");
    }
}
