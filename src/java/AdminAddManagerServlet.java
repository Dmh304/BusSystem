import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.UserAccount;
import util.SessionUtil;

public class AdminAddManagerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is ADMIN
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.setAttribute("pageTitle", "Add Manager");
        request.getRequestDispatcher("/admin/add-manager.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String action = request.getParameter("action");
        String username = request.getParameter("username") != null ? request.getParameter("username").trim() : "";
        String password = request.getParameter("password") != null ? request.getParameter("password").trim() : "";
        String fullName = request.getParameter("fullName") != null ? request.getParameter("fullName").trim() : "";
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";

        UserDAO userDAO = new UserDAO();
        String message = "";

        if ("add".equalsIgnoreCase(action)) {
            // Validation
            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
                message = "error:Vui lòng điền đầy đủ thông tin";
            } else if (userDAO.isUsernameExists(username)) {
                message = "error:Username đã tồn tại";
            } else {
                // Add manager
                int managerRoleId = userDAO.getRoleIdByName("MANAGER");
                if (managerRoleId > 0 && userDAO.addUser(username, password, fullName, email, managerRoleId)) {
                    message = "success:Thêm manager thành công";
                } else {
                    message = "error:Thêm manager thất bại";
                }
            }
        }

        // Set message for JSP
        if (message.startsWith("success:")) {
            request.getSession().setAttribute("message", message.substring(8));
            request.getSession().setAttribute("messageType", "success");
        } else if (message.startsWith("error:")) {
            request.getSession().setAttribute("message", message.substring(6));
            request.getSession().setAttribute("messageType", "error");
        }

        response.sendRedirect(request.getContextPath() + "/admin/add-manager");
    }
}
