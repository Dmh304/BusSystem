import dal.BusDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import util.SessionUtil;

public class AdminBusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BusDAO busDAO = new BusDAO();
        request.setAttribute("pageTitle", "Admin Buses");
        request.setAttribute("buses", busDAO.getAllBuses());
        request.getRequestDispatcher("/admin/buses.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int busId = Integer.parseInt(request.getParameter("busId"));
        String status = request.getParameter("status");

        BusDAO busDAO = new BusDAO();
        boolean ok = busDAO.updateBusStatus(busId, status);

        if (ok) {
            SessionUtil.setSuccess(request.getSession(), "Đã cập nhật trạng thái xe.");
        } else {
            SessionUtil.setError(request.getSession(), "Không thể cập nhật trạng thái xe.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/buses");
    }
}
