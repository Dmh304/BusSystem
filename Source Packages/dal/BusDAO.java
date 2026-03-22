package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Bus;

public class BusDAO extends DBContext {

    public Bus getBusByManager(int managerUserId) {
        String sql = "SELECT TOP 1 b.BusID, b.PlateNumber, b.BusName, b.Capacity, b.[Status], "
                + "r.RouteName, u1.FullName AS ManagerName, u2.FullName AS DriverName "
                + "FROM BusAssignment ba "
                + "INNER JOIN Bus b ON ba.BusID = b.BusID "
                + "INNER JOIN Route r ON ba.RouteID = r.RouteID "
                + "LEFT JOIN UserAccount u1 ON ba.ManagerUserID = u1.UserID "
                + "LEFT JOIN UserAccount u2 ON ba.DriverUserID = u2.UserID "
                + "WHERE ba.ManagerUserID = ? AND ba.[Status] = 'ACTIVE'";
        return getAssignedBus(sql, managerUserId);
    }

    public Bus getBusByDriver(int driverUserId) {
        String sql = "SELECT TOP 1 b.BusID, b.PlateNumber, b.BusName, b.Capacity, b.[Status], "
                + "r.RouteName, u1.FullName AS ManagerName, u2.FullName AS DriverName "
                + "FROM BusAssignment ba "
                + "INNER JOIN Bus b ON ba.BusID = b.BusID "
                + "INNER JOIN Route r ON ba.RouteID = r.RouteID "
                + "LEFT JOIN UserAccount u1 ON ba.ManagerUserID = u1.UserID "
                + "LEFT JOIN UserAccount u2 ON ba.DriverUserID = u2.UserID "
                + "WHERE ba.DriverUserID = ? AND ba.[Status] = 'ACTIVE'";
        return getAssignedBus(sql, driverUserId);
    }

    public List<Bus> getAllBuses() {
        List<Bus> list = new ArrayList<>();
        String sql = "SELECT b.BusID, b.PlateNumber, b.BusName, b.Capacity, b.[Status], "
                + "r.RouteName, u1.FullName AS ManagerName, u2.FullName AS DriverName "
                + "FROM Bus b "
                + "LEFT JOIN BusAssignment ba ON b.BusID = ba.BusID AND ba.[Status] = 'ACTIVE' "
                + "LEFT JOIN Route r ON ba.RouteID = r.RouteID "
                + "LEFT JOIN UserAccount u1 ON ba.ManagerUserID = u1.UserID "
                + "LEFT JOIN UserAccount u2 ON ba.DriverUserID = u2.UserID "
                + "ORDER BY b.BusID";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractBus(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countActiveBuses() {
        String sql = "SELECT COUNT(*) FROM Bus WHERE [Status] = 'ACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateBusStatus(int busId, String status) {
        String sql = "UPDATE Bus SET [Status] = ? WHERE BusID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, busId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Bus getAssignedBus(String sql, int userId) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractBus(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bus extractBus(ResultSet rs) throws Exception {
        Bus bus = new Bus();
        bus.setBusId(rs.getInt("BusID"));
        bus.setPlateNumber(rs.getString("PlateNumber"));
        bus.setBusName(rs.getString("BusName"));
        bus.setCapacity(rs.getInt("Capacity"));
        bus.setStatus(rs.getString("Status"));
        bus.setRouteName(rs.getString("RouteName"));
        bus.setManagerName(rs.getString("ManagerName"));
        bus.setDriverName(rs.getString("DriverName"));
        return bus;
    }
}
