package controller;
import dal.RouteDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminRouteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RouteDAO routeDAO = new RouteDAO();

        int routeId = 1;
        try {
            if (request.getParameter("routeId") != null) {
                routeId = Integer.parseInt(request.getParameter("routeId"));
            }
        } catch (Exception e) {
            routeId = 1;
        }

        request.setAttribute("pageTitle", "Admin Routes");
        request.setAttribute("routes", routeDAO.getAllRoutes());
        request.setAttribute("selectedRouteId", routeId);
        request.setAttribute("routeStops", routeDAO.getStopsByRouteId(routeId));
        request.getRequestDispatcher("/admin/routes.jsp").forward(request, response);
    }
}

