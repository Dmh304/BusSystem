import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String roleName = request.getParameter("roleName");

        UserDAO userDAO = new UserDAO();
        request.setAttribute("pageTitle", "Admin Users");
        request.setAttribute("users", userDAO.getAllUsers(keyword, roleName));
        request.setAttribute("keyword", keyword);
        request.setAttribute("roleName", roleName);
        request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
    }
}
