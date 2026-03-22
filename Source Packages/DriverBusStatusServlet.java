import dal.BusDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.Bus;
import model.UserAccount;
import util.SessionUtil;

public class DriverBusStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        BusDAO busDAO = new BusDAO();
        Bus bus = busDAO.getBusByDriver(user.getUserId());

        request.setAttribute("pageTitle", "Driver Bus Status");
        request.setAttribute("bus", bus);
        request.getRequestDispatcher("/driver/bus-status.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int busId = Integer.parseInt(request.getParameter("busId"));
        String status = request.getParameter("status");

        BusDAO busDAO = new BusDAO();
        boolean ok = busDAO.updateBusStatus(busId, status);

        if (ok) {
            SessionUtil.setSuccess(request.getSession(), "Đã cập nhật tình trạng xe.");
        } else {
            SessionUtil.setError(request.getSession(), "Không thể cập nhật tình trạng xe.");
        }
        response.sendRedirect(request.getContextPath() + "/driver/bus-status");
    }
}
