package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.UserAccount;

public class UserDAO extends DBContext {

    public UserAccount login(String username, String password) {
        String sql = "SELECT u.UserID, u.Username, u.[Password], u.FullName, u.Email, u.RoleID, "
                + "u.[Status], u.CreatedAt, r.RoleName "
                + "FROM UserAccount u "
                + "INNER JOIN Role r ON u.RoleID = r.RoleID "
                + "WHERE u.Username = ? AND u.[Password] = ? AND u.[Status] = 'ACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UserAccount> getAllUsers(String keyword, String roleName) {
        List<UserAccount> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT u.UserID, u.Username, u.[Password], u.FullName, u.Email, u.RoleID, ");
        sql.append("u.[Status], u.CreatedAt, r.RoleName ");
        sql.append("FROM UserAccount u ");
        sql.append("INNER JOIN Role r ON u.RoleID = r.RoleID ");
        sql.append("WHERE 1 = 1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (u.FullName LIKE ? OR u.Email LIKE ? OR u.Username LIKE ?) ");
        }
        if (roleName != null && !roleName.trim().isEmpty()) {
            sql.append("AND r.RoleName = ? ");
        }
        sql.append("ORDER BY u.UserID");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String value = "%" + keyword.trim() + "%";
                ps.setString(idx++, value);
                ps.setString(idx++, value);
                ps.setString(idx++, value);
            }
            if (roleName != null && !roleName.trim().isEmpty()) {
                ps.setString(idx++, roleName);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractUser(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countAllUsers() {
        String sql = "SELECT COUNT(*) FROM UserAccount WHERE [Status] = 'ACTIVE'";
        return getScalar(sql);
    }

    public int countByRole(String roleName) {
        String sql = "SELECT COUNT(*) "
                + "FROM UserAccount u INNER JOIN Role r ON u.RoleID = r.RoleID "
                + "WHERE u.[Status] = 'ACTIVE' AND r.RoleName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roleName);
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

    public UserAccount getById(int userId) {
        String sql = "SELECT u.UserID, u.Username, u.[Password], u.FullName, u.Email, u.RoleID, "
                + "u.[Status], u.CreatedAt, r.RoleName "
                + "FROM UserAccount u "
                + "INNER JOIN Role r ON u.RoleID = r.RoleID "
                + "WHERE u.UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getRoleIdByName(String roleName) {
        String sql = "SELECT RoleID FROM Role WHERE RoleName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RoleID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean addUser(String username, String password, String fullName, String email, int roleId) {
        String sql = "INSERT INTO UserAccount (Username, [Password], FullName, Email, RoleID, [Status], CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, 'ACTIVE', GETDATE())";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setInt(5, roleId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM UserAccount WHERE Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UserAccount> getManagersWithoutAssignment() {
        List<UserAccount> list = new ArrayList<>();
        String sql = "SELECT u.UserID, u.Username, u.[Password], u.FullName, u.Email, u.RoleID, "
                + "u.[Status], u.CreatedAt, r.RoleName "
                + "FROM UserAccount u "
                + "INNER JOIN Role r ON u.RoleID = r.RoleID "
                + "WHERE r.RoleName = 'MANAGER' AND u.[Status] = 'ACTIVE' "
                + "AND u.UserID NOT IN ("
                + "SELECT ManagerUserID FROM BusAssignment WHERE [Status] = 'ACTIVE' AND ManagerUserID IS NOT NULL)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractUser(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<UserAccount> getDriversWithoutAssignment() {
        List<UserAccount> list = new ArrayList<>();
        String sql = "SELECT u.UserID, u.Username, u.[Password], u.FullName, u.Email, u.RoleID, "
                + "u.[Status], u.CreatedAt, r.RoleName "
                + "FROM UserAccount u "
                + "INNER JOIN Role r ON u.RoleID = r.RoleID "
                + "WHERE r.RoleName = 'DRIVER' AND u.[Status] = 'ACTIVE' "
                + "AND u.UserID NOT IN ("
                + "SELECT DriverUserID FROM BusAssignment WHERE [Status] = 'ACTIVE' AND DriverUserID IS NOT NULL)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractUser(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private int getScalar(String sql) {
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

    private UserAccount extractUser(ResultSet rs) throws Exception {
        UserAccount user = new UserAccount();
        user.setUserId(rs.getInt("UserID"));
        user.setUsername(rs.getString("Username"));
        user.setPassword(rs.getString("Password"));
        user.setFullName(rs.getString("FullName"));
        user.setEmail(rs.getString("Email"));
        user.setRoleId(rs.getInt("RoleID"));
        user.setStatus(rs.getString("Status"));
        user.setCreatedAt(rs.getTimestamp("CreatedAt"));
        user.setRoleName(rs.getString("RoleName"));
        return user;
    }
}
