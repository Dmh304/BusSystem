package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Incident;

public class IncidentDAO extends DBContext {

    public List<Incident> getIncidentsByDriver(int driverUserId) {
        List<Incident> list = new ArrayList<>();
        String sql = "SELECT i.IncidentID, i.AssignmentID, i.DriverUserID, i.Title, i.Description, "
                + "i.IncidentStatus, i.ReportedAt, i.ResolvedAt, u.FullName AS DriverName, b.PlateNumber "
                + "FROM Incident i "
                + "LEFT JOIN UserAccount u ON i.DriverUserID = u.UserID "
                + "LEFT JOIN BusAssignment ba ON i.AssignmentID = ba.AssignmentID "
                + "LEFT JOIN Bus b ON ba.BusID = b.BusID "
                + "WHERE i.DriverUserID = ? "
                + "ORDER BY i.ReportedAt DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, driverUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractIncident(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Incident> getAllIncidents() {
        List<Incident> list = new ArrayList<>();
        String sql = "SELECT i.IncidentID, i.AssignmentID, i.DriverUserID, i.Title, i.Description, "
                + "i.IncidentStatus, i.ReportedAt, i.ResolvedAt, u.FullName AS DriverName, b.PlateNumber "
                + "FROM Incident i "
                + "LEFT JOIN UserAccount u ON i.DriverUserID = u.UserID "
                + "LEFT JOIN BusAssignment ba ON i.AssignmentID = ba.AssignmentID "
                + "LEFT JOIN Bus b ON ba.BusID = b.BusID "
                + "ORDER BY i.ReportedAt DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractIncident(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countOpenIncidents() {
        String sql = "SELECT COUNT(*) FROM Incident WHERE IncidentStatus IN ('OPEN', 'IN_PROGRESS')";
        return countByQuery(sql, null);
    }

    public int countOpenIncidentsByDriver(int driverUserId) {
        String sql = "SELECT COUNT(*) FROM Incident WHERE IncidentStatus IN ('OPEN', 'IN_PROGRESS') AND DriverUserID = ?";
        return countByQuery(sql, driverUserId);
    }

    public boolean createIncident(int assignmentId, int driverUserId, String title, String description) {
        String sql = "INSERT INTO Incident (AssignmentID, DriverUserID, Title, Description, IncidentStatus) "
                + "VALUES (?, ?, ?, ?, 'OPEN')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ps.setInt(2, driverUserId);
            ps.setString(3, title);
            ps.setString(4, description);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private int countByQuery(String sql, Integer driverUserId) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (driverUserId != null) {
                ps.setInt(1, driverUserId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Incident extractIncident(ResultSet rs) throws Exception {
        Incident incident = new Incident();
        incident.setIncidentId(rs.getInt("IncidentID"));
        incident.setAssignmentId(rs.getInt("AssignmentID"));
        int driverUserId = rs.getInt("DriverUserID");
        if (rs.wasNull()) {
            incident.setDriverUserId(null);
        } else {
            incident.setDriverUserId(driverUserId);
        }
        incident.setTitle(rs.getString("Title"));
        incident.setDescription(rs.getString("Description"));
        incident.setIncidentStatus(rs.getString("IncidentStatus"));
        incident.setReportedAt(rs.getTimestamp("ReportedAt"));
        incident.setResolvedAt(rs.getTimestamp("ResolvedAt"));
        incident.setDriverName(rs.getString("DriverName"));
        incident.setPlateNumber(rs.getString("PlateNumber"));
        return incident;
    }
}
