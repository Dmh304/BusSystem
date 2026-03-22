import dal.BusDAO;
import dal.RouteDAO;
import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Bus;
import model.Route;
import model.UserAccount;
import util.SessionUtil;

public class AdminAddBusAssignmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is ADMIN
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        BusDAO busDAO = new BusDAO();
        RouteDAO routeDAO = new RouteDAO();
        UserDAO userDAO = new UserDAO();

        List<Bus> unassignedBuses = busDAO.getUnassignedBuses();
        List<Route> routes = routeDAO.getAllRoutes();
        List<UserAccount> managersWithoutAssignment = userDAO.getManagersWithoutAssignment();
        List<UserAccount> driversWithoutAssignment = userDAO.getDriversWithoutAssignment();

        request.setAttribute("pageTitle", "Add Bus Assignment");
        request.setAttribute("unassignedBuses", unassignedBuses);
        request.setAttribute("routes", routes);
        request.setAttribute("managers", managersWithoutAssignment);
        request.setAttribute("drivers", driversWithoutAssignment);
        request.getRequestDispatcher("/admin/add-bus-assignment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String action = request.getParameter("action");
        String busIdStr = request.getParameter("busId") != null ? request.getParameter("busId").trim() : "";
        String routeIdStr = request.getParameter("routeId") != null ? request.getParameter("routeId").trim() : "";
        String managerUserIdStr = request.getParameter("managerUserId") != null ? request.getParameter("managerUserId").trim() : "";
        String driverUserIdStr = request.getParameter("driverUserId") != null ? request.getParameter("driverUserId").trim() : "";

        BusDAO busDAO = new BusDAO();
        String message = "";

        if ("assign".equalsIgnoreCase(action)) {
            // Validation
            if (busIdStr.isEmpty() || routeIdStr.isEmpty() || managerUserIdStr.isEmpty() || driverUserIdStr.isEmpty()) {
                message = "error:Vui lòng điền đầy đủ thông tin";
            } else {
                try {
                    int busId = Integer.parseInt(busIdStr);
                    int routeId = Integer.parseInt(routeIdStr);
                    int managerUserId = Integer.parseInt(managerUserIdStr);
                    int driverUserId = Integer.parseInt(driverUserIdStr);

                    if (busDAO.createBusAssignment(busId, routeId, managerUserId, driverUserId)) {
                        message = "success:Gán xe cho quản lý thành công";
                    } else {
                        message = "error:Gán xe thất bại";
                    }
                } catch (NumberFormatException e) {
                    message = "error:Dữ liệu không hợp lệ";
                }
            }
        }

        // Set message for JSP
        if (message.startsWith("success:")) {
            request.getSession().setAttribute("message", message.substring(8));
            request.getSession().setAttribute("messageType", "success");
        } else if (message.startsWith("error:")) {
            request.getSession().setAttribute("message", message.substring(6));
            request.getSession().setAttribute("messageType", "error");
        }

        response.sendRedirect(request.getContextPath() + "/admin/add-bus-assignment");
    }
}
