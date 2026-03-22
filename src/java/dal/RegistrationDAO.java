package dal;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import model.DailyTripRegistration;

public class RegistrationDAO extends DBContext {

    public DailyTripRegistration getRegistration(int studentId, Date tripDate, String sessionType) {
        String sql = "SELECT RegistrationID, StudentID, TripDate, SessionType, AttendanceChoice, "
                + "SourceType, Note, UpdatedAt "
                + "FROM DailyTripRegistration "
                + "WHERE StudentID = ? AND TripDate = ? AND SessionType = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, tripDate);
            ps.setString(3, sessionType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractRegistration(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<Integer, DailyTripRegistration> getRegistrationsByParent(int parentUserId, Date tripDate, String sessionType) {
        Map<Integer, DailyTripRegistration> map = new HashMap<>();
        String sql = "SELECT r.RegistrationID, r.StudentID, r.TripDate, r.SessionType, r.AttendanceChoice, "
                + "r.SourceType, r.Note, r.UpdatedAt "
                + "FROM DailyTripRegistration r "
                + "INNER JOIN Student s ON r.StudentID = s.StudentID "
                + "WHERE s.ParentUserID = ? AND r.TripDate = ? AND r.SessionType = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, parentUserId);
            ps.setDate(2, tripDate);
            ps.setString(3, sessionType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DailyTripRegistration reg = extractRegistration(rs);
                    map.put(reg.getStudentId(), reg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public boolean saveOrUpdateRegistration(int studentId, Date tripDate, String sessionType, String attendanceChoice, String sourceType, String note) {
        DailyTripRegistration current = getRegistration(studentId, tripDate, sessionType);
        if (current == null) {
            String sql = "INSERT INTO DailyTripRegistration "
                    + "(StudentID, TripDate, SessionType, AttendanceChoice, SourceType, Note) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                ps.setDate(2, tripDate);
                ps.setString(3, sessionType);
                ps.setString(4, attendanceChoice);
                ps.setString(5, sourceType);
                ps.setString(6, note);
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String sql = "UPDATE DailyTripRegistration "
                    + "SET AttendanceChoice = ?, SourceType = ?, Note = ?, UpdatedAt = GETDATE() "
                    + "WHERE RegistrationID = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, attendanceChoice);
                ps.setString(2, sourceType);
                ps.setString(3, note);
                ps.setInt(4, current.getRegistrationId());
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private DailyTripRegistration extractRegistration(ResultSet rs) throws Exception {
        DailyTripRegistration reg = new DailyTripRegistration();
        reg.setRegistrationId(rs.getInt("RegistrationID"));
        reg.setStudentId(rs.getInt("StudentID"));
        reg.setTripDate(rs.getDate("TripDate"));
        reg.setSessionType(rs.getString("SessionType"));
        reg.setAttendanceChoice(rs.getString("AttendanceChoice"));
        reg.setSourceType(rs.getString("SourceType"));
        reg.setNote(rs.getString("Note"));
        reg.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        return reg;
    }

    /**
     * Delete all registrations for a specific date (used for system reset)
     */
    public boolean deleteRegistrationsByDate(Date tripDate) {
        String sql = "DELETE FROM DailyTripRegistration WHERE TripDate = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, tripDate);
            return ps.executeUpdate() >= 0; // >= 0 means success (0 rows deleted is still success)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete all registrations for a specific date and session (used for session-specific reset)
     */
    public boolean deleteRegistrationsByDateAndSession(Date tripDate, String sessionType) {
        String sql = "DELETE FROM DailyTripRegistration WHERE TripDate = ? AND SessionType = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, tripDate);
            ps.setString(2, sessionType);
            return ps.executeUpdate() >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
