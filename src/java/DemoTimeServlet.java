import dal.ManifestDAO;
import dal.RegistrationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
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
            // ✅ Reset time to 4:30 AM
            DemoTimeUtil.resetDemoTime(request.getSession());
            
            // ✅ Reset all data for today (new day)
            Date today = new Date(System.currentTimeMillis());
            RegistrationDAO registrationDAO = new RegistrationDAO();
            ManifestDAO manifestDAO = new ManifestDAO();
            
            // Delete all parent registrations for today
            registrationDAO.deleteRegistrationsByDate(today);
            
            // Delete all manifests and boarding records for today
            manifestDAO.deleteManifestsByDate(today);
            
            SessionUtil.setSuccess(request.getSession(), 
                "✅ Đã reset hệ thống: Thời gian mô phỏng → 4:30 AM, Đặt lại đăng ký học sinh, Xóa lịch trình xe.");
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
