package controller;
import dal.BusDAO;
import dal.IncidentDAO;
import dal.StudentDAO;
import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDAO userDAO = new UserDAO();
        StudentDAO studentDAO = new StudentDAO();
        BusDAO busDAO = new BusDAO();
        IncidentDAO incidentDAO = new IncidentDAO();

        request.setAttribute("pageTitle", "Admin Dashboard");
        request.setAttribute("userCount", userDAO.countAllUsers());
        request.setAttribute("parentCount", userDAO.countByRole("PARENT"));
        request.setAttribute("managerCount", userDAO.countByRole("MANAGER"));
        request.setAttribute("driverCount", userDAO.countByRole("DRIVER"));
        request.setAttribute("studentCount", studentDAO.countActiveStudents());
        request.setAttribute("busCount", busDAO.countActiveBuses());
        request.setAttribute("openIncidentCount", incidentDAO.countOpenIncidents());
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}

