package controller;
import dal.BusDAO;
import dal.RouteDAO;
import dal.UserDAO;
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
        UserDAO userDAO = new UserDAO();
        RouteDAO routeDAO = new RouteDAO();
        
        request.setAttribute("pageTitle", "Admin Buses");
        request.setAttribute("buses", busDAO.getAllBuses());
        request.setAttribute("routes", routeDAO.getAllRoutes());
        request.setAttribute("managers", userDAO.getAllUsers("", "MANAGER"));
        request.setAttribute("drivers", userDAO.getAllUsers("", "DRIVER"));
        request.getRequestDispatcher("/admin/buses.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        BusDAO busDAO = new BusDAO();

        try {
            if ("add".equals(action)) {
                String plateNumber = request.getParameter("plateNumber");
                String busName = request.getParameter("busName");
                int capacity = Integer.parseInt(request.getParameter("capacity"));
                
                boolean ok = busDAO.addBus(plateNumber, busName, capacity);
                if (ok) SessionUtil.setSuccess(request.getSession(), "Bus added successfully.");
                else SessionUtil.setError(request.getSession(), "Failed to add bus.");
                
            } else if ("edit".equals(action)) {
                int busId = Integer.parseInt(request.getParameter("busId"));
                String plateNumber = request.getParameter("plateNumber");
                String busName = request.getParameter("busName");
                int capacity = Integer.parseInt(request.getParameter("capacity"));
                String status = request.getParameter("status");
                
                boolean ok = busDAO.updateBus(busId, plateNumber, busName, capacity, status);
                if (ok) SessionUtil.setSuccess(request.getSession(), "Bus updated successfully.");
                else SessionUtil.setError(request.getSession(), "Failed to update bus.");
                
            } else if ("delete".equals(action)) {
                int busId = Integer.parseInt(request.getParameter("busId"));
                
                // Deactivate assignments first, then bus
                busDAO.deactivateBusAssignments(busId);
                boolean ok = busDAO.deleteBus(busId);
                if (ok) SessionUtil.setSuccess(request.getSession(), "Bus deleted successfully.");
                else SessionUtil.setError(request.getSession(), "Failed to delete bus.");
                
            } else if ("assign".equals(action)) {
                int busId = Integer.parseInt(request.getParameter("busId"));
                int routeId = Integer.parseInt(request.getParameter("routeId"));
                int managerId = Integer.parseInt(request.getParameter("managerId"));
                int driverId = Integer.parseInt(request.getParameter("driverId"));
                
                busDAO.deactivateBusAssignments(busId);
                boolean ok = busDAO.createBusAssignment(busId, routeId, managerId, driverId);
                if (ok) SessionUtil.setSuccess(request.getSession(), "Team assigned successfully.");
                else SessionUtil.setError(request.getSession(), "Failed to assign team.");
                
            } else {
                // Fallback to old status update code if action is missing
                int busId = Integer.parseInt(request.getParameter("busId"));
                String status = request.getParameter("status");
                boolean ok = busDAO.updateBusStatus(busId, status);
                if (ok) SessionUtil.setSuccess(request.getSession(), "Bus status updated successfully.");
                else SessionUtil.setError(request.getSession(), "Failed to update bus status.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            SessionUtil.setError(request.getSession(), "An error occurred: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/buses");
    }
}

