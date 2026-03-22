import dal.ManifestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalTime;
import java.util.List;
import model.ManifestStudent;
import model.TripManifest;
import model.UserAccount;
import util.DemoTimeUtil;
import util.SessionUtil;
import util.TimeRuleUtil;

public class ManagerManifestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        Date today = new Date(System.currentTimeMillis());
        LocalTime demoTime = DemoTimeUtil.getDemoTime(request.getSession());
        String sessionType = request.getParameter("sessionType");
        if (sessionType == null || sessionType.trim().isEmpty()) {
            sessionType = TimeRuleUtil.getPreferredSession(demoTime);
        }

        ManifestDAO manifestDAO = new ManifestDAO();
        TripManifest manifest = manifestDAO.getManifestByManager(user.getUserId(), today, sessionType);
        List<ManifestStudent> students = manifest == null ? null : manifestDAO.getManifestStudents(manifest.getManifestId());

        request.setAttribute("pageTitle", "Manager Manifest");
        request.setAttribute("selectedSession", sessionType);
        request.setAttribute("manifest", manifest);
        request.setAttribute("students", students);
        request.setAttribute("canUpdate", TimeRuleUtil.canManagerUpdateManifest(sessionType, demoTime));
        request.getRequestDispatcher("/manager/manifest.jsp").forward(request, response);
    }
}
