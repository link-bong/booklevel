package src.main.java.com.novelplatform.dao;

import src.main.java.com.novelplatform.util.DBUtil;
import java.sql.*;

public class BookDao {
    // 获取书籍详细信息
    public static ResultSet getBookDetails(int bookId) throws SQLException {
        String sql = "SELECT b.*, w.w_name, " +
                "(SELECT COUNT(*) FROM browse WHERE b_id = ?) AS view_count, " +
                "(SELECT COUNT(*) FROM addbooks WHERE b_id = ?) AS collect_count, " +
                "(SELECT COUNT(*) FROM comment WHERE b_id = ?) AS comment_count " +
                "FROM books b JOIN writer w ON b.w_id = w.w_id WHERE b.b_id = ?";

        Connection conn = DBUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, bookId);
        stmt.setInt(2, bookId);
        stmt.setInt(3, bookId);
        stmt.setInt(4, bookId);
        return stmt.executeQuery();
    }

    // 添加书签
    public static boolean addBookmark(int userId, int chapterId) throws SQLException {
        String sql = "INSERT INTO bookmark (cp_id, r_id, bm_time) VALUES (?, ?, NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chapterId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}