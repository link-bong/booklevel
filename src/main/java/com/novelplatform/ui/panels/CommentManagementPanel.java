package src.main.java.com.novelplatform.ui.panels;

import src.main.java.com.novelplatform.dao.CommentDao;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import src.main.java.com.novelplatform.util.DBUtil;

public class CommentManagementPanel extends JPanel {
    private final int writerId;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"书籍", "读者", "评论内容", "回复内容"}, 0
    );
    private JTable commentTable;
    public CommentManagementPanel(int writerId) {
        this.writerId = writerId;
        initUI();
        loadComments();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 评论列表
        JTable commentTable = new JTable(tableModel);
        commentTable.setRowHeight(60);

        // 回复按钮
        JButton replyBtn = new JButton("回复选中评论");
        replyBtn.addActionListener(e -> showReplyDialog());

        add(new JScrollPane(commentTable), BorderLayout.CENTER);
        add(replyBtn, BorderLayout.SOUTH);
        // 添加删除按钮
        JButton deleteBtn = new JButton("删除评论");
        deleteBtn.addActionListener(e -> deleteSelectedComment());

        JPanel btnPanel = new JPanel();
        btnPanel.add(replyBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void deleteSelectedComment() {
        int selectedRow = commentTable.getSelectedRow();
        if (selectedRow == -1) return;

        int commentId = (int) tableModel.getValueAt(selectedRow, 0); // 假设第一列存储ID
        try {
            if (CommentDao.deleteComment(commentId, writerId)) {
                loadComments();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage());
        }
    }


    private void loadComments() {
        tableModel.setRowCount(0);
        String sql = "SELECT b.b_name, r.r_name, c.cm_content, wr.wr_content " +
                "FROM comment c " +
                "LEFT JOIN wirter_reply wr ON c.cm_id = wr.cm_id " +
                "JOIN books b ON c.b_id = b.b_id " +
                "JOIN reader r ON c.r_id = r.r_id " +
                "WHERE b.w_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, writerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("b_name"),
                        rs.getString("r_name"),
                        rs.getString("cm_content"),
                        rs.getString("wr_content")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载失败: " + ex.getMessage());
        }
    }

    private void showReplyDialog() {
        // 实现回复对话框逻辑
    }
}
