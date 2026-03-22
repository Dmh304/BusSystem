import dal.BusDAO;
import dal.ManifestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalTime;
import java.util.List;
import model.Bus;
import model.ManifestStudent;
import model.TripManifest;
import model.UserAccount;
import util.DemoTimeUtil;
import util.SessionUtil;
import util.TimeRuleUtil;

public class ManagerDashboardServlet extends HttpServlet {

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

        BusDAO busDAO = new BusDAO();
        ManifestDAO manifestDAO = new ManifestDAO();

        Bus bus = busDAO.getBusByManager(user.getUserId());
        TripManifest manifest = manifestDAO.getManifestByManager(user.getUserId(), today, sessionType);
        
        // Auto-generate manifest if it doesn't exist and lock time has passed
        if (manifest == null) {
            boolean lockTimePassed = "MORNING".equalsIgnoreCase(sessionType)
                    ? !demoTime.isBefore(java.time.LocalTime.of(5, 0))
                    : !demoTime.isBefore(java.time.LocalTime.of(14, 0));
            if (lockTimePassed) {
                var assignment = manifestDAO.getAssignmentByManager(user.getUserId(), today, sessionType);
                if (assignment != null) {
                    if (manifestDAO.generateManifestFromRegistrations(
                            assignment.getAssignmentId(), today, sessionType)) {
                        manifest = manifestDAO.getManifestByManager(user.getUserId(), today, sessionType);
                    }
                }
            }
        }
        
        // Sync students if manifest exists but has no students yet
        if (manifest != null) {
            manifestDAO.syncManifestStudents(manifest.getManifestId(), today, sessionType);
        }
        
        List<ManifestStudent> students = manifest == null ? null : manifestDAO.getManifestStudents(manifest.getManifestId());

        request.setAttribute("pageTitle", "Manager Dashboard");
        request.setAttribute("selectedSession", sessionType);
        request.setAttribute("bus", bus);
        request.setAttribute("manifest", manifest);
        request.setAttribute("students", students);
        if (manifest != null) {
            request.setAttribute("busCount", manifestDAO.countBusStudents(manifest.getManifestId()));
            request.setAttribute("boardedCount", manifestDAO.countBoardedStudents(manifest.getManifestId()));
            request.setAttribute("pendingCount", manifestDAO.countPendingStudents(manifest.getManifestId()));
        }
        request.setAttribute("canUpdate", TimeRuleUtil.canManagerUpdateManifest(sessionType, demoTime));
        request.getRequestDispatcher("/manager/dashboard.jsp").forward(request, response);
    }
}
