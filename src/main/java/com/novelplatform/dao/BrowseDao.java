package src.main.java.com.novelplatform.dao;

import src.main.java.com.novelplatform.util.DBUtil;
import java.sql.*;

public class BrowseDao {
    // 插入浏览记录
    public static boolean insertBrowseRecord(int userId, int bookId) throws SQLException {
        String sql = "INSERT INTO browse (r_id, b_id, browsetime) VALUES (?, ?, NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            return stmt.executeUpdate() > 0;
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
