import dal.BusDAO;
import dal.IncidentDAO;
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
import model.Incident;
import model.TripManifest;
import model.UserAccount;
import util.DemoTimeUtil;
import util.SessionUtil;
import util.TimeRuleUtil;

public class DriverIncidentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        IncidentDAO incidentDAO = new IncidentDAO();
        BusDAO busDAO = new BusDAO();
        ManifestDAO manifestDAO = new ManifestDAO();

        LocalTime demoTime = DemoTimeUtil.getDemoTime(request.getSession());
        String sessionType = request.getParameter("sessionType");
        if (sessionType == null || sessionType.trim().isEmpty()) {
            sessionType = TimeRuleUtil.getPreferredSession(demoTime);
        }

        List<Incident> incidents = incidentDAO.getIncidentsByDriver(user.getUserId());
        Bus bus = busDAO.getBusByDriver(user.getUserId());
        TripManifest manifest = manifestDAO.getManifestByDriver(user.getUserId(), new Date(System.currentTimeMillis()), sessionType);

        request.setAttribute("pageTitle", "Driver Incident");
        request.setAttribute("incidents", incidents);
        request.setAttribute("bus", bus);
        request.setAttribute("manifest", manifest);
        request.getRequestDispatcher("/driver/incident.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String sessionType = request.getParameter("sessionType");

        ManifestDAO manifestDAO = new ManifestDAO();
        TripManifest manifest = manifestDAO.getManifestByDriver(user.getUserId(), new Date(System.currentTimeMillis()), sessionType);

        if (manifest == null) {
            SessionUtil.setError(request.getSession(), "No current assignment found for this driver.");
            response.sendRedirect(request.getContextPath() + "/driver/incident");
            return;
        }

        IncidentDAO incidentDAO = new IncidentDAO();
        boolean ok = incidentDAO.createIncident(manifest.getAssignmentId(), user.getUserId(), title, description);

        if (ok) {
            SessionUtil.setSuccess(request.getSession(), "Incident reported successfully.");
        } else {
            SessionUtil.setError(request.getSession(), "Failed to report incident.");
        }
        response.sendRedirect(request.getContextPath() + "/driver/incident?sessionType=" + sessionType);
    }
}
