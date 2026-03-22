import dal.ManifestDAO;
import dal.StudentDAO;
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
        
        // ✅ AUTO-GENERATE MANIFEST if doesn't exist and lock time has passed
        if (manifest == null) {
            // Check if lock time has passed
            boolean lockTimePassed = "MORNING".equalsIgnoreCase(sessionType)
                    ? !demoTime.isBefore(LocalTime.of(5, 0))  // Morning lock starts at 5:00 AM
                    : !demoTime.isBefore(LocalTime.of(14, 0)); // Afternoon lock starts at 2:00 PM
            
            if (lockTimePassed) {
                // Get manager's bus assignment
                var assignment = manifestDAO.getAssignmentByManager(user.getUserId(), today, sessionType);
                if (assignment != null) {
                    // Generate manifest from registrations
                    if (manifestDAO.generateManifestFromRegistrations(
                            assignment.getAssignmentId(), today, sessionType)) {
                        // Retrieve the newly created manifest
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

        request.setAttribute("pageTitle", "Manager Manifest");
        request.setAttribute("selectedSession", sessionType);
        request.setAttribute("manifest", manifest);
        request.setAttribute("students", students);
        request.setAttribute("canUpdate", TimeRuleUtil.canManagerUpdateManifest(sessionType, demoTime));
        request.getRequestDispatcher("/manager/manifest.jsp").forward(request, response);
    }
}
