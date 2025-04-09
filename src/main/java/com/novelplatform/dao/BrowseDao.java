package src.main.java.com.novelplatform.dao;

import src.main.java.com.novelplatform.util.DBUtil;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BrowseDao {
    // 插入浏览记录
    public static boolean insertBrowseRecord(int userId, int bookId) throws SQLException {
        String checkSql = "SELECT * FROM browse WHERE r_id = ? AND b_id = ?";
        String insertSql = "INSERT INTO browse (r_id, b_id, browsetime) VALUES (?, ?, NOW())";
        String updateSql = "UPDATE browse SET browsetime = NOW() WHERE r_id = ? AND b_id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            // 检查是否已存在记录
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, bookId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    // 记录已存在，更新浏览时间
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, userId);
                        updateStmt.setInt(2, bookId);
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    // 记录不存在，插入新记录
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, bookId);
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
        }
    }

    // 获取用户浏览历史（每本书最新记录）
    public static ResultSet getRecentBrowseHistory(int userId) throws SQLException {
        String sql = "SELECT b.b_name, MAX(browsetime) AS last_browse_time " +
                "FROM browse br " +
                "JOIN books b ON br.b_id = b.b_id " +
                "WHERE br.r_id = ? " +
                "GROUP BY b.b_id";
        Connection conn = DBUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        return stmt.executeQuery();
    }
}