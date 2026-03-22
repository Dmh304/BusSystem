package controller;
import dal.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminStudentsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");

        StudentDAO studentDAO = new StudentDAO();
        request.setAttribute("pageTitle", "Admin Students");
        request.setAttribute("students", studentDAO.getAllStudents(keyword));
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/admin/students.jsp").forward(request, response);
    }
}

