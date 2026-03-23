import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import util.DemoTimeUtil;
import util.SessionUtil;
import model.UserAccount;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set cache control headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        UserAccount currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (currentUser != null) {
            response.sendRedirect(request.getContextPath() + SessionUtil.redirectDashboard(currentUser));
            return;
        }
        request.setAttribute("pageTitle", "Đăng nhập");
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set cache control headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();
        UserAccount user = userDAO.login(username, password);

        if (user == null) {
            request.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu.");
            request.setAttribute("pageTitle", "Đăng nhập");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        SessionUtil.setCurrentUser(session, user);
        DemoTimeUtil.resetDemoTime(session);
        SessionUtil.setSuccess(session, "Đăng nhập thành công.");
        response.sendRedirect(request.getContextPath() + SessionUtil.redirectDashboard(user));
    }
}
