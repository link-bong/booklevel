package src.main.java.com.novelplatform.dao;

import src.main.java.com.novelplatform.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDao {

    // 获取作者收到的所有评论及回复（联合查询）
    public static List<CommentWithReply> getCommentsByWriter(int writerId) throws SQLException {
        List<CommentWithReply> comments = new ArrayList<>();
        String sql = "SELECT "
                + "  c.cm_id AS comment_id, "
                + "  b.b_name AS book_name, "
                + "  r.r_name AS reader_name, "
                + "  c.cm_content AS comment_content, "
                + "  c.cm_time AS comment_time, "
                + "  wr.wr_content AS reply_content, "
                + "  wr.wr_time AS reply_time "
                + "FROM comment c "
                + "JOIN books b ON c.b_id = b.b_id "
                + "JOIN reader r ON c.r_id = r.r_id "
                + "LEFT JOIN wirter_reply wr ON c.cm_id = wr.cm_id "
                + "WHERE b.w_id = ? "
                + "ORDER BY c.cm_time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, writerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comments.add(new CommentWithReply(
                        rs.getInt("comment_id"),
                        rs.getString("book_name"),
                        rs.getString("reader_name"),
                        rs.getString("comment_content"),
                        rs.getTimestamp("comment_time"),
                        rs.getString("reply_content"),
                        rs.getTimestamp("reply_time")
                ));
            }
        }
        return comments;
    }
    public static boolean hasBrowsed(int userId, int bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM browse WHERE r_id=? AND b_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // 提交评论
    public static boolean addComment(int userId, int bookId, String content) throws SQLException {
        if (!hasBrowsed(userId, bookId)) {
            throw new SQLException("未阅读过该书籍，无法评论");
        }

        String sql = "INSERT INTO comment (cm_content, cm_time, b_id, r_id) " +
                "VALUES (?, NOW(), ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, content);
            stmt.setInt(2, bookId);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    // 添加作者回复
    public static boolean addWriterReply(int commentId, int writerId, String content) throws SQLException {
        String sql = "INSERT INTO wirter_reply (cm_id, w_id, wr_content, wr_time) "
                + "VALUES (?, ?, ?, NOW())";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, commentId);
            stmt.setInt(2, writerId);
            stmt.setString(3, content);

            return stmt.executeUpdate() > 0;
        }
    }

    // 数据载体类（静态内部类）
    public static class CommentWithReply {
        private final int commentId;
        private final String bookName;
        private final String readerName;
        private final String commentContent;
        private final Timestamp commentTime;
        private final String replyContent;
        private final Timestamp replyTime;

        public CommentWithReply(int commentId, String bookName, String readerName,
                                String commentContent, Timestamp commentTime,
                                String replyContent, Timestamp replyTime) {
            this.commentId = commentId;
            this.bookName = bookName;
            this.readerName = readerName;
            this.commentContent = commentContent;
            this.commentTime = commentTime;
            this.replyContent = replyContent;
            this.replyTime = replyTime;
        }

        // Getter 方法
        public int getCommentId() { return commentId; }
        public String getBookName() { return bookName; }
        public String getReaderName() { return readerName; }
        public String getCommentContent() { return commentContent; }
        public Timestamp getCommentTime() { return commentTime; }
        public String getReplyContent() { return replyContent; }
        public Timestamp getReplyTime() { return replyTime; }

        // 判断是否有回复
        public boolean hasReply() {
            return replyContent != null && !replyContent.isEmpty();
        }
    }
        public static boolean deleteComment(int commentId, int writerId) throws SQLException {
            // 验证评论属于该作者的书
            String sql = "DELETE c FROM comment c " +
                    "JOIN books b ON c.b_id = b.b_id " +
                    "WHERE c.cm_id = ? AND b.w_id = ?";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, commentId);
                stmt.setInt(2, writerId);
                return stmt.executeUpdate() > 0;
            }
        }

}
