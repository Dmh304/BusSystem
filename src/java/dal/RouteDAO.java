package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Route;
import model.RouteStop;

public class RouteDAO extends DBContext {

    public List<Route> getAllRoutes() {
        List<Route> list = new ArrayList<>();
        String sql = "SELECT RouteID, RouteName, Description, [Status] FROM Route ORDER BY RouteID";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Route route = new Route();
                route.setRouteId(rs.getInt("RouteID"));
                route.setRouteName(rs.getString("RouteName"));
                route.setDescription(rs.getString("Description"));
                route.setStatus(rs.getString("Status"));
                list.add(route);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<RouteStop> getStopsByRouteId(int routeId) {
        List<RouteStop> list = new ArrayList<>();
        String sql = "SELECT rs.RouteStopID, rs.RouteID, rs.StopID, rs.StopOrder, "
                + "rs.EstimatedMorningTime, rs.EstimatedAfternoonTime, "
                + "sp.StopName, sp.AddressDetail "
                + "FROM RouteStop rs "
                + "INNER JOIN StopPoint sp ON rs.StopID = sp.StopID "
                + "WHERE rs.RouteID = ? "
                + "ORDER BY rs.StopOrder";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, routeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RouteStop routeStop = new RouteStop();
                    routeStop.setRouteStopId(rs.getInt("RouteStopID"));
                    routeStop.setRouteId(rs.getInt("RouteID"));
                    routeStop.setStopId(rs.getInt("StopID"));
                    routeStop.setStopOrder(rs.getInt("StopOrder"));
                    routeStop.setEstimatedMorningTime(rs.getTime("EstimatedMorningTime"));
                    routeStop.setEstimatedAfternoonTime(rs.getTime("EstimatedAfternoonTime"));
                    routeStop.setStopName(rs.getString("StopName"));
                    routeStop.setAddressDetail(rs.getString("AddressDetail"));
                    list.add(routeStop);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
