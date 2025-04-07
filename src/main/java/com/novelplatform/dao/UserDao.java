package src.main.java.com.novelplatform.dao;

import src.main.java.com.novelplatform.util.DBUtil;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    // 检查用户名是否存在
    public static boolean usernameExists(String username, String table) throws SQLException {
        String sql = "SELECT * FROM " + table + " WHERE " + table.substring(0,1) + "_name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // 注册用户
    public static boolean registerUser(String username, String password, String table) throws SQLException {
        String sql = "INSERT INTO " + table + " (" + table.substring(0,1) + "_name, " + table.substring(0,1) + "_password) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeUpdate() > 0;
        }
    }

    // 用户登录验证
    public static boolean loginUser(String username, String password, String userType)
            throws SQLException {

        String table = userType.toLowerCase();
        String nameCol = table.substring(0,1) + "_name";
        String passCol = table.substring(0,1) + "_password";

        String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?",
                table, nameCol, passCol);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeQuery().next();
        }
    }

        public static int getUserId(String username, String userType) throws SQLException {
            String table = userType.toLowerCase();
            String idColumn = table.charAt(0) + "_id";
            String nameColumn = table.charAt(0) + "_name";

            String sql = String.format("SELECT %s FROM %s WHERE %s = ?",
                    idColumn, table, nameColumn);

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("用户不存在: " + username);
                }
            }
        }

    // 修改密码
    public static boolean updatePassword(int userId, String newPassword, String userType) throws SQLException {
        String table = userType.toLowerCase();
        String sql = String.format("UPDATE %s SET %s_password = ? WHERE %s_id = ?",
                table, table.charAt(0), table.charAt(0));

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    public static boolean updatePassword(String userType, int userId,
                                         String oldPassword, String newPassword)
            throws SQLException {
        String table = userType.toLowerCase();
        String idColumn = table.charAt(0) + "_id";
        String passColumn = table.charAt(0) + "_password";

        // 验证旧密码
        String checkSql = "SELECT " + passColumn + " FROM " + table +
                " WHERE " + idColumn + " = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next() || !rs.getString(1).equals(oldPassword)) {
                return false;
            }
        }

        // 更新密码
        String updateSql = "UPDATE " + table + " SET " + passColumn + " = ?" +
                " WHERE " + idColumn + " = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}
