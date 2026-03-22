import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;
import util.DemoTimeUtil;
import util.SessionUtil;

public class DemoTimeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String value = request.getParameter("value");
        String returnUrl = request.getParameter("returnUrl");

        if ("reset".equalsIgnoreCase(action)) {
            DemoTimeUtil.resetDemoTime(request.getSession());
            SessionUtil.setSuccess(request.getSession(), "Đã reset thời gian mô phỏng.");
        } else if (value != null && !value.trim().isEmpty()) {
            try {
                DemoTimeUtil.setDemoTime(request.getSession(), LocalTime.parse(value));
                SessionUtil.setSuccess(request.getSession(), "Đã cập nhật thời gian mô phỏng: " + value);
            } catch (Exception e) {
                SessionUtil.setError(request.getSession(), "Thời gian mô phỏng không hợp lệ.");
            }
        }

        if (returnUrl == null || returnUrl.trim().isEmpty()) {
            returnUrl = request.getContextPath() + "/login";
        }
        response.sendRedirect(returnUrl);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String referer = request.getHeader("referer");
        if (referer == null || referer.trim().isEmpty()) {
            referer = request.getContextPath() + "/";
        }
        response.sendRedirect(referer);
    }
}
