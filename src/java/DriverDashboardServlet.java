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
import model.Bus;
import model.TripManifest;
import model.UserAccount;
import util.DemoTimeUtil;
import util.SessionUtil;
import util.TimeRuleUtil;

public class DriverDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        LocalTime demoTime = DemoTimeUtil.getDemoTime(request.getSession());
        String sessionType = request.getParameter("sessionType");
        if (sessionType == null || sessionType.trim().isEmpty()) {
            sessionType = TimeRuleUtil.getPreferredSession(demoTime);
        }

        BusDAO busDAO = new BusDAO();
        IncidentDAO incidentDAO = new IncidentDAO();
        ManifestDAO manifestDAO = new ManifestDAO();

        Bus bus = busDAO.getBusByDriver(user.getUserId());
        TripManifest manifest = manifestDAO.getManifestByDriver(user.getUserId(), new Date(System.currentTimeMillis()), sessionType);

        request.setAttribute("pageTitle", "Driver Dashboard");
        request.setAttribute("bus", bus);
        request.setAttribute("manifest", manifest);
        request.setAttribute("selectedSession", sessionType);
        request.setAttribute("openIncidentCount", incidentDAO.countOpenIncidentsByDriver(user.getUserId()));
        request.getRequestDispatcher("/driver/dashboard.jsp").forward(request, response);
    }
}
