import dal.ManifestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalTime;
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
        List<RouteStop> routeStops = manifest == null ? null : manifestDAO.getRouteStopsByManifest(manifest.getManifestId());

        request.setAttribute("pageTitle", "Manager Tracking");
        request.setAttribute("selectedSession", sessionType);
        request.setAttribute("manifest", manifest);
        request.setAttribute("routeStops", routeStops);
        request.getRequestDispatcher("/manager/tracking.jsp").forward(request, response);
    }
}
