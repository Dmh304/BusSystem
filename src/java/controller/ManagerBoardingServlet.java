package controller;
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

public class ManagerBoardingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sessionType = request.getParameter("sessionType");
        String boardingStatus = request.getParameter("boardingStatus");
        int manifestStudentId = Integer.parseInt(request.getParameter("manifestStudentId"));

        LocalTime demoTime = DemoTimeUtil.getDemoTime(request.getSession());
        if (!TimeRuleUtil.canManagerUpdateManifest(sessionType, demoTime)) {
            SessionUtil.setError(request.getSession(), "It is not yet time to update boarding for " + sessionType + ".");
            response.sendRedirect(request.getContextPath() + "/manager/boarding?sessionType=" + sessionType);
            return;
        }

        ManifestDAO manifestDAO = new ManifestDAO();
        boolean ok = manifestDAO.updateBoardingStatus(manifestStudentId, boardingStatus);
        if (ok) {
            SessionUtil.setSuccess(request.getSession(), "Boarding status updated successfully.");
        } else {
            SessionUtil.setError(request.getSession(), "Failed to update boarding status.");
        }
        response.sendRedirect(request.getContextPath() + "/manager/boarding?sessionType=" + sessionType);
    }

    private void loadPage(HttpServletRequest request, HttpServletResponse response)
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

        request.setAttribute("pageTitle", "Manager Boarding");
        request.setAttribute("selectedSession", sessionType);
        request.setAttribute("manifest", manifest);
        request.setAttribute("students", students);
        request.setAttribute("canUpdate", TimeRuleUtil.canManagerUpdateManifest(sessionType, demoTime));
        // Pass the current stop order so boarding page can disable buttons for stops not yet reached
        int currentStopOrder = manifest != null ? manifestDAO.getCurrentStopOrder(manifest.getManifestId()) : 0;
        request.setAttribute("currentStopOrder", currentStopOrder);
        request.getRequestDispatcher("/manager/boarding.jsp").forward(request, response);
    }
}

