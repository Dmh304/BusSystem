package dal;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.BusAssignment;
import model.ManifestStudent;
import model.RouteStop;
import model.TripManifest;

public class ManifestDAO extends DBContext {

    public TripManifest getManifestByManager(int managerUserId, Date tripDate, String sessionType) {
        String sql = "SELECT TOP 1 tm.ManifestID, tm.AssignmentID, tm.TripDate, tm.SessionType, "
                + "tm.ManifestStatus, tm.CurrentRouteStopID, tm.DepartureTime, tm.StartedAt, tm.FinishedAt, "
                + "b.PlateNumber, r.RouteName, sp.StopName AS CurrentStopName "
                + "FROM TripManifest tm "
                + "INNER JOIN BusAssignment ba ON tm.AssignmentID = ba.AssignmentID "
                + "INNER JOIN Bus b ON ba.BusID = b.BusID "
                + "INNER JOIN Route r ON ba.RouteID = r.RouteID "
                + "LEFT JOIN RouteStop rs ON tm.CurrentRouteStopID = rs.RouteStopID "
                + "LEFT JOIN StopPoint sp ON rs.StopID = sp.StopID "
                + "WHERE ba.ManagerUserID = ? AND tm.TripDate = ? AND tm.SessionType = ?";
        return getManifest(sql, managerUserId, tripDate, sessionType);
    }

    public TripManifest getManifestByDriver(int driverUserId, Date tripDate, String sessionType) {
        String sql = "SELECT TOP 1 tm.ManifestID, tm.AssignmentID, tm.TripDate, tm.SessionType, "
                + "tm.ManifestStatus, tm.CurrentRouteStopID, tm.DepartureTime, tm.StartedAt, tm.FinishedAt, "
                + "b.PlateNumber, r.RouteName, sp.StopName AS CurrentStopName "
                + "FROM TripManifest tm "
                + "INNER JOIN BusAssignment ba ON tm.AssignmentID = ba.AssignmentID "
                + "INNER JOIN Bus b ON ba.BusID = b.BusID "
                + "INNER JOIN Route r ON ba.RouteID = r.RouteID "
                + "LEFT JOIN RouteStop rs ON tm.CurrentRouteStopID = rs.RouteStopID "
                + "LEFT JOIN StopPoint sp ON rs.StopID = sp.StopID "
                + "WHERE ba.DriverUserID = ? AND tm.TripDate = ? AND tm.SessionType = ?";
        return getManifest(sql, driverUserId, tripDate, sessionType);
    }

    public List<ManifestStudent> getManifestStudents(int manifestId) {
        List<ManifestStudent> list = new ArrayList<>();
        String sql = "SELECT ms.ManifestStudentID, ms.ManifestID, ms.StudentID, ms.AttendanceChoice, "
                + "ms.BoardingStatus, ms.BoardedAt, ms.Note, "
                + "s.StudentCode, s.FullName AS StudentName, sp.StopName AS PickupStopName, "
                + "ISNULL(rs.StopOrder, 0) AS PickupStopOrder "
                + "FROM ManifestStudent ms "
                + "INNER JOIN Student s ON ms.StudentID = s.StudentID "
                + "INNER JOIN StopPoint sp ON s.DefaultPickupStopID = sp.StopID "
                + "INNER JOIN TripManifest tm ON ms.ManifestID = tm.ManifestID "
                + "INNER JOIN BusAssignment ba ON tm.AssignmentID = ba.AssignmentID "
                + "LEFT JOIN RouteStop rs ON rs.StopID = s.DefaultPickupStopID AND rs.RouteID = ba.RouteID "
                + "WHERE ms.ManifestID = ? "
                + "ORDER BY rs.StopOrder, s.FullName";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, manifestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ManifestStudent item = new ManifestStudent();
                    item.setManifestStudentId(rs.getInt("ManifestStudentID"));
                    item.setManifestId(rs.getInt("ManifestID"));
                    item.setStudentId(rs.getInt("StudentID"));
                    item.setAttendanceChoice(rs.getString("AttendanceChoice"));
                    item.setBoardingStatus(rs.getString("BoardingStatus"));
                    item.setBoardedAt(rs.getTimestamp("BoardedAt"));
                    item.setNote(rs.getString("Note"));
                    item.setStudentCode(rs.getString("StudentCode"));
                    item.setStudentName(rs.getString("StudentName"));
                    item.setPickupStopName(rs.getString("PickupStopName"));
                    item.setPickupStopOrder(rs.getInt("PickupStopOrder"));
                    list.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateBoardingStatus(int manifestStudentId, String boardingStatus) {
        String sql = "UPDATE ManifestStudent "
                + "SET BoardingStatus = ?, "
                + "BoardedAt = CASE WHEN ? = 'BOARDED' THEN GETDATE() ELSE NULL END "
                + "WHERE ManifestStudentID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, boardingStatus);
            ps.setString(2, boardingStatus);
            ps.setInt(3, manifestStudentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the StopOrder of the current route stop for a manifest.
     * Returns 0 if the trip hasn't started (no current stop).
     */
    public int getCurrentStopOrder(int manifestId) {
        String sql = "SELECT ISNULL(rs.StopOrder, 0) AS StopOrder "
                + "FROM TripManifest tm "
                + "INNER JOIN RouteStop rs ON tm.CurrentRouteStopID = rs.RouteStopID "
                + "WHERE tm.ManifestID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, manifestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("StopOrder");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // Trip not started or no current stop
    }

    public boolean updateCurrentRouteStop(int manifestId, int routeStopId) {
        String sql = "UPDATE TripManifest "
                + "SET CurrentRouteStopID = ?, ManifestStatus = 'RUNNING', "
                + "StartedAt = CASE WHEN StartedAt IS NULL THEN GETDATE() ELSE StartedAt END "
                + "WHERE ManifestID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, routeStopId);
            ps.setInt(2, manifestId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<RouteStop> getRouteStopsByManifest(int manifestId) {
        List<RouteStop> list = new ArrayList<>();
        String sql = "SELECT rs.RouteStopID, rs.RouteID, rs.StopID, rs.StopOrder, "
                + "rs.EstimatedMorningTime, rs.EstimatedAfternoonTime, sp.StopName, sp.AddressDetail "
                + "FROM TripManifest tm "
                + "INNER JOIN BusAssignment ba ON tm.AssignmentID = ba.AssignmentID "
                + "INNER JOIN RouteStop rs ON ba.RouteID = rs.RouteID "
                + "INNER JOIN StopPoint sp ON rs.StopID = sp.StopID "
                + "WHERE tm.ManifestID = ? "
                + "ORDER BY rs.StopOrder";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, manifestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RouteStop item = new RouteStop();
                    item.setRouteStopId(rs.getInt("RouteStopID"));
                    item.setRouteId(rs.getInt("RouteID"));
                    item.setStopId(rs.getInt("StopID"));
                    item.setStopOrder(rs.getInt("StopOrder"));
                    item.setEstimatedMorningTime(rs.getTime("EstimatedMorningTime"));
                    item.setEstimatedAfternoonTime(rs.getTime("EstimatedAfternoonTime"));
                    item.setStopName(rs.getString("StopName"));
                    item.setAddressDetail(rs.getString("AddressDetail"));
                    list.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countBusStudents(int manifestId) {
        return countByManifestAndCondition(manifestId, "AND AttendanceChoice = 'BUS'");
    }

    public int countBoardedStudents(int manifestId) {
        return countByManifestAndCondition(manifestId, "AND AttendanceChoice = 'BUS' AND BoardingStatus = 'BOARDED'");
    }

    public int countPendingStudents(int manifestId) {
        return countByManifestAndCondition(manifestId, "AND AttendanceChoice = 'BUS' AND BoardingStatus = 'PENDING'");
    }

    /**
     * Get bus assignment for manager on a specific date and session type
     */
    public BusAssignment getAssignmentByManager(int managerUserId, Date tripDate, String sessionType) {
        String sql = "SELECT DISTINCT ba.AssignmentID, ba.BusID, ba.RouteID, ba.ManagerUserID, ba.DriverUserID, ba.[Status] "
                + "FROM BusAssignment ba "
                + "WHERE ba.ManagerUserID = ? AND ba.[Status] = 'ACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, managerUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BusAssignment assignment = new BusAssignment();
                    assignment.setAssignmentId(rs.getInt("AssignmentID"));
                    assignment.setBusId(rs.getInt("BusID"));
                    assignment.setRouteId(rs.getInt("RouteID"));
                    assignment.setManagerUserId(rs.getInt("ManagerUserID"));
                    assignment.setStatus(rs.getString("Status"));
                    return assignment;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generate manifest from daily trip registrations after lock time
     */
    public boolean generateManifestFromRegistrations(int assignmentId, Date tripDate, String sessionType) {
        String sqlInsertManifest = 
            "INSERT INTO TripManifest (AssignmentID, TripDate, SessionType, ManifestStatus) "
            + "VALUES (?, ?, ?, 'OPEN')";
        
        try (PreparedStatement ps = connection.prepareStatement(sqlInsertManifest, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, assignmentId);
            ps.setDate(2, tripDate);
            ps.setString(3, sessionType);
            
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int manifestId = keys.getInt(1);
                        // Create ManifestStudent records from DailyTripRegistration
                        return createManifestStudentsFromRegistrations(manifestId, tripDate, sessionType);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Create ManifestStudent records from DailyTripRegistration
     * Only include students that are on the same route as the assignment
     */
    private boolean createManifestStudentsFromRegistrations(int manifestId, Date tripDate, String sessionType) {
        String sql = 
            "INSERT INTO ManifestStudent (ManifestID, StudentID, AttendanceChoice, BoardingStatus) "
            + "SELECT ?, r.StudentID, r.AttendanceChoice, 'PENDING' "
            + "FROM DailyTripRegistration r "
            + "INNER JOIN Student s ON r.StudentID = s.StudentID "
            + "INNER JOIN TripManifest tm ON ? = tm.ManifestID "
            + "INNER JOIN BusAssignment ba ON tm.AssignmentID = ba.AssignmentID "
            + "WHERE r.TripDate = ? AND r.SessionType = ? "
            + "  AND r.AttendanceChoice = 'BUS' "
            + "  AND s.DefaultRouteID = ba.RouteID "
            + "  AND ba.[Status] = 'ACTIVE'";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, manifestId);
            ps.setInt(2, manifestId);
            ps.setDate(3, tripDate);
            ps.setString(4, sessionType);
            return ps.executeUpdate() >= 0; // >= 0 means success (0 rows inserted is still success)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sync manifest students - if manifest exists but has no students,
     * re-populate from DailyTripRegistration.
     * This handles the case where manifest was created before parents registered.
     */
    public boolean syncManifestStudents(int manifestId, Date tripDate, String sessionType) {
        // Check if manifest already has students
        int studentCount = countByManifestAndCondition(manifestId, "");
        if (studentCount > 0) {
            return true; // Already has students, no need to sync
        }
        // No students yet, try to populate from registrations
        return createManifestStudentsFromRegistrations(manifestId, tripDate, sessionType);
    }

    public ManifestStudent getStudentTripStatus(int studentId, Date tripDate, String sessionType) {
        String sql = "SELECT TOP 1 ms.ManifestStudentID, ms.ManifestID, ms.StudentID, ms.AttendanceChoice, "
                + "ms.BoardingStatus, ms.BoardedAt, ms.Note, tm.SessionType, tm.TripDate, tm.ManifestStatus, "
                + "sp.StopName AS CurrentStopName "
                + "FROM ManifestStudent ms "
                + "INNER JOIN TripManifest tm ON ms.ManifestID = tm.ManifestID "
                + "LEFT JOIN RouteStop rs ON tm.CurrentRouteStopID = rs.RouteStopID "
                + "LEFT JOIN StopPoint sp ON rs.StopID = sp.StopID "
                + "WHERE ms.StudentID = ? AND tm.TripDate = ? AND tm.SessionType = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, tripDate);
            ps.setString(3, sessionType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ManifestStudent item = new ManifestStudent();
                    item.setManifestStudentId(rs.getInt("ManifestStudentID"));
                    item.setManifestId(rs.getInt("ManifestID"));
                    item.setStudentId(rs.getInt("StudentID"));
                    item.setAttendanceChoice(rs.getString("AttendanceChoice"));
                    item.setBoardingStatus(rs.getString("BoardingStatus"));
                    item.setBoardedAt(rs.getTimestamp("BoardedAt"));
                    item.setNote(rs.getString("Note"));
                    item.setSessionType(rs.getString("SessionType"));
                    item.setTripDate(rs.getDate("TripDate"));
                    item.setManifestStatus(rs.getString("ManifestStatus"));
                    item.setCurrentStopName(rs.getString("CurrentStopName"));
                    return item;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int countByManifestAndCondition(int manifestId, String condition) {
        String sql = "SELECT COUNT(*) FROM ManifestStudent WHERE ManifestID = ? " + condition;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, manifestId);
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

    private TripManifest getManifest(String sql, int userId, Date tripDate, String sessionType) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, tripDate);
            ps.setString(3, sessionType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TripManifest manifest = new TripManifest();
                    manifest.setManifestId(rs.getInt("ManifestID"));
                    manifest.setAssignmentId(rs.getInt("AssignmentID"));
                    manifest.setTripDate(rs.getDate("TripDate"));
                    manifest.setSessionType(rs.getString("SessionType"));
                    manifest.setManifestStatus(rs.getString("ManifestStatus"));
                    int currentRouteStopId = rs.getInt("CurrentRouteStopID");
                    if (rs.wasNull()) {
                        manifest.setCurrentRouteStopId(null);
                    } else {
                        manifest.setCurrentRouteStopId(currentRouteStopId);
                    }
                    manifest.setDepartureTime(rs.getTime("DepartureTime"));
                    manifest.setStartedAt(rs.getTimestamp("StartedAt"));
                    manifest.setFinishedAt(rs.getTimestamp("FinishedAt"));
                    manifest.setPlateNumber(rs.getString("PlateNumber"));
                    manifest.setRouteName(rs.getString("RouteName"));
                    manifest.setCurrentStopName(rs.getString("CurrentStopName"));
                    return manifest;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Delete all manifests and their students for a specific date (used for system reset)
     */
    public boolean deleteManifestsByDate(Date tripDate) {
        String sql = "DELETE FROM ManifestStudent WHERE ManifestID IN "
                + "(SELECT ManifestID FROM TripManifest WHERE TripDate = ?); "
                + "DELETE FROM TripManifest WHERE TripDate = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, tripDate);
            ps.setDate(2, tripDate);
            return ps.executeUpdate() >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete all manifests and their students for a specific date and session (used for session-specific reset)
     */
    public boolean deleteManifestsByDateAndSession(Date tripDate, String sessionType) {
        String sql = "DELETE FROM ManifestStudent WHERE ManifestID IN "
                + "(SELECT ManifestID FROM TripManifest WHERE TripDate = ? AND SessionType = ?); "
                + "DELETE FROM TripManifest WHERE TripDate = ? AND SessionType = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, tripDate);
            ps.setString(2, sessionType);
            ps.setDate(3, tripDate);
            ps.setString(4, sessionType);
            return ps.executeUpdate() >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
