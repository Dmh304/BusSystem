package controller;
import dal.ManifestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import model.RouteStop;
import model.TripManifest;
import model.UserAccount;
import util.DemoTimeUtil;
import util.SessionUtil;
import util.TimeRuleUtil;

public class ManagerTrackingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sessionType = request.getParameter("sessionType");
        int manifestId = Integer.parseInt(request.getParameter("manifestId"));
        int routeStopId = Integer.parseInt(request.getParameter("routeStopId"));

        ManifestDAO manifestDAO = new ManifestDAO();
        boolean ok = manifestDAO.updateCurrentRouteStop(manifestId, routeStopId);

        if (ok) {
            SessionUtil.setSuccess(request.getSession(), "Current bus stop updated successfully.");
        } else {
            SessionUtil.setError(request.getSession(), "Failed to update current stop.");
        }

        response.sendRedirect(request.getContextPath() + "/manager/tracking?sessionType=" + sessionType);
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
        
        List<RouteStop> routeStops = manifest == null ? null : manifestDAO.getRouteStopsByManifest(manifest.getManifestId());

        // AFTERNOON: reverse the route stops order (school → home, i.e. stop 7 → 1)
        if (routeStops != null && "AFTERNOON".equalsIgnoreCase(sessionType)) {
            Collections.reverse(routeStops);
        }

        // Check if bus is at the final stop (destination reached)
        boolean isAtFinalStop = false;
        if (routeStops != null && !routeStops.isEmpty() && manifest != null && manifest.getCurrentRouteStopId() != null) {
            RouteStop lastStop = routeStops.get(routeStops.size() - 1);
            if (lastStop.getRouteStopId() == manifest.getCurrentRouteStopId()) {
                isAtFinalStop = true;
            }
        }

        request.setAttribute("pageTitle", "Manager Tracking");
        request.setAttribute("selectedSession", sessionType);
        request.setAttribute("manifest", manifest);
        request.setAttribute("routeStops", routeStops);
        request.setAttribute("isAtFinalStop", isAtFinalStop);
        request.getRequestDispatcher("/manager/tracking.jsp").forward(request, response);
    }
}

