package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Student;

public class StudentDAO extends DBContext {

    public List<Student> getStudentsByParent(int parentUserId) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.StudentID, s.ParentUserID, s.StudentCode, s.FullName, s.Gender, s.Grade, "
                + "s.DefaultRouteID, s.DefaultPickupStopID, s.DefaultDropoffStopID, s.[Status], "
                + "sp1.StopName AS PickupStopName, sp2.StopName AS DropoffStopName, r.RouteName, u.FullName AS ParentName, "
                + "um.FullName AS ManagerName, ud.FullName AS DriverName "
                + "FROM Student s "
                + "INNER JOIN StopPoint sp1 ON s.DefaultPickupStopID = sp1.StopID "
                + "INNER JOIN StopPoint sp2 ON s.DefaultDropoffStopID = sp2.StopID "
                + "INNER JOIN Route r ON s.DefaultRouteID = r.RouteID "
                + "INNER JOIN UserAccount u ON s.ParentUserID = u.UserID "
                + "LEFT JOIN BusAssignment ba ON r.RouteID = ba.RouteID AND ba.[Status] = 'ACTIVE' "
                + "LEFT JOIN UserAccount um ON ba.ManagerUserID = um.UserID "
                + "LEFT JOIN UserAccount ud ON ba.DriverUserID = ud.UserID "
                + "WHERE s.ParentUserID = ? "
                + "ORDER BY s.StudentID";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, parentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractStudent(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Student> getAllStudents(String keyword, Integer parentUserId) {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT s.StudentID, s.ParentUserID, s.StudentCode, s.FullName, s.Gender, s.Grade, ");
        sql.append("s.DefaultRouteID, s.DefaultPickupStopID, s.DefaultDropoffStopID, s.[Status], ");
        sql.append("sp1.StopName AS PickupStopName, sp2.StopName AS DropoffStopName, ");
        sql.append("r.RouteName, u.FullName AS ParentName, NULL AS ManagerName, NULL AS DriverName ");
        sql.append("FROM Student s ");
        sql.append("INNER JOIN StopPoint sp1 ON s.DefaultPickupStopID = sp1.StopID ");
        sql.append("INNER JOIN StopPoint sp2 ON s.DefaultDropoffStopID = sp2.StopID ");
        sql.append("INNER JOIN Route r ON s.DefaultRouteID = r.RouteID ");
        sql.append("INNER JOIN UserAccount u ON s.ParentUserID = u.UserID ");
        sql.append("WHERE 1 = 1 ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (s.FullName LIKE ? OR s.StudentCode LIKE ? OR u.FullName LIKE ?) ");
        }
        if (parentUserId != null) {
            sql.append("AND s.ParentUserID = ? ");
        }
        sql.append("ORDER BY s.StudentID");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String value = "%" + keyword.trim() + "%";
                ps.setString(idx++, value);
                ps.setString(idx++, value);
                ps.setString(idx++, value);
            }
            if (parentUserId != null) {
                ps.setInt(idx++, parentUserId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractStudent(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countActiveStudents() {
        String sql = "SELECT COUNT(*) FROM Student WHERE [Status] = 'ACTIVE'";
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

    public Student getById(int studentId) {
        String sql = "SELECT s.StudentID, s.ParentUserID, s.StudentCode, s.FullName, s.Gender, s.Grade, "
                + "s.DefaultRouteID, s.DefaultPickupStopID, s.DefaultDropoffStopID, s.[Status], "
                + "sp1.StopName AS PickupStopName, sp2.StopName AS DropoffStopName, r.RouteName, u.FullName AS ParentName, "
                + "NULL AS ManagerName, NULL AS DriverName "
                + "FROM Student s "
                + "INNER JOIN StopPoint sp1 ON s.DefaultPickupStopID = sp1.StopID "
                + "INNER JOIN StopPoint sp2 ON s.DefaultDropoffStopID = sp2.StopID "
                + "INNER JOIN Route r ON s.DefaultRouteID = r.RouteID "
                + "INNER JOIN UserAccount u ON s.ParentUserID = u.UserID "
                + "WHERE s.StudentID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractStudent(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addStudent(String studentCode, String fullName, String gender, String grade,
            int parentUserId, int routeId, int pickupStopId, int dropoffStopId) {
        String sql = "INSERT INTO Student (ParentUserID, StudentCode, FullName, Gender, Grade, "
                + "DefaultRouteID, DefaultPickupStopID, DefaultDropoffStopID, [Status]) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, parentUserId);
            ps.setString(2, studentCode);
            ps.setString(3, fullName);
            ps.setString(4, gender);
            ps.setString(5, grade);
            ps.setInt(6, routeId);
            ps.setInt(7, pickupStopId);
            ps.setInt(8, dropoffStopId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM Student WHERE StudentID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStudent(int studentId, String fullName, String gender, String grade,
            int routeId, int pickupStopId, int dropoffStopId) {
        String sql = "UPDATE Student SET FullName = ?, Gender = ?, Grade = ?, "
                + "DefaultRouteID = ?, DefaultPickupStopID = ?, DefaultDropoffStopID = ? "
                + "WHERE StudentID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, gender);
            ps.setString(3, grade);
            ps.setInt(4, routeId);
            ps.setInt(5, pickupStopId);
            ps.setInt(6, dropoffStopId);
            ps.setInt(7, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Student extractStudent(ResultSet rs) throws Exception {
        Student student = new Student();
        student.setStudentId(rs.getInt("StudentID"));
        student.setParentUserId(rs.getInt("ParentUserID"));
        student.setStudentCode(rs.getString("StudentCode"));
        student.setFullName(rs.getString("FullName"));
        student.setGender(rs.getString("Gender"));
        student.setGrade(rs.getString("Grade"));
        student.setDefaultRouteId(rs.getInt("DefaultRouteID"));
        student.setDefaultPickupStopId(rs.getInt("DefaultPickupStopID"));
        student.setDefaultDropoffStopId(rs.getInt("DefaultDropoffStopID"));
        student.setStatus(rs.getString("Status"));
        student.setPickupStopName(rs.getString("PickupStopName"));
        student.setDropoffStopName(rs.getString("DropoffStopName"));
        student.setRouteName(rs.getString("RouteName"));
        student.setParentName(rs.getString("ParentName"));
        student.setManagerName(rs.getString("ManagerName"));
        student.setDriverName(rs.getString("DriverName"));
        return student;
    }
}
