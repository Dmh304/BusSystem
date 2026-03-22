import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import util.SessionUtil;

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        UserDAO userDAO = new UserDAO();

        try {
            if ("add".equals(action)) {
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String roleName = request.getParameter("roleName");
                
                if (userDAO.isUsernameExists(username)) {
                    SessionUtil.setError(request.getSession(), "Username already exists!");
                } else {
                    int roleId = userDAO.getRoleIdByName(roleName);
                    boolean ok = userDAO.addUser(username, password, fullName, email, roleId);
                    if (ok) SessionUtil.setSuccess(request.getSession(), "User added successfully.");
                    else SessionUtil.setError(request.getSession(), "Failed to add user.");
                }
            } else if ("edit".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String roleName = request.getParameter("roleName");
                String status = request.getParameter("status");
                
                int roleId = userDAO.getRoleIdByName(roleName);
                boolean ok = userDAO.updateUser(userId, fullName, email, roleId, status);
                if (ok) SessionUtil.setSuccess(request.getSession(), "User updated successfully.");
                else SessionUtil.setError(request.getSession(), "Failed to update user.");
            } else if ("delete".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                boolean ok = userDAO.deleteUser(userId);
                if (ok) SessionUtil.setSuccess(request.getSession(), "User permanently deleted.");
                else SessionUtil.setError(request.getSession(), "Cannot delete user. They might have associated records.");
            } else if ("lock".equals(action) || "unlock".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                String newStatus = "lock".equals(action) ? "INACTIVE" : "ACTIVE";
                boolean ok = userDAO.updateUserStatus(userId, newStatus);
                if (ok) SessionUtil.setSuccess(request.getSession(), "User status updated to " + newStatus + ".");
                else SessionUtil.setError(request.getSession(), "Failed to update user status.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            SessionUtil.setError(request.getSession(), "An error occurred: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}
