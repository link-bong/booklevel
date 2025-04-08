package src.main.java.com.novelplatform.ui.panels;

import src.main.java.com.novelplatform.dao.CommentDao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent; // 导入 ActionEvent 类
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import src.main.java.com.novelplatform.util.DBUtil;


public class CommentManagementPanel extends JPanel {
    private final int writerId;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"评论ID", "读者", "评论内容", "评论时间"}, 0
    );
    private final JTable commentTable = new JTable(tableModel);

    public CommentManagementPanel(int writerId) {
        this.writerId = writerId;
        initUI();
        loadComments();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 操作工具栏
        JPanel toolPanel = new JPanel();
        JButton replyBtn = new JButton("回复选中评论");
        JButton deleteBtn = new JButton("删除选中评论");

        replyBtn.addActionListener(this::handleReplyComment);
        deleteBtn.addActionListener(this::handleDeleteComment);

        toolPanel.add(replyBtn);
        toolPanel.add(deleteBtn);

        // 评论列表
        commentTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(commentTable);

        add(toolPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadComments() {
        tableModel.setRowCount(0);
        String sql = "SELECT c.cm_id, r.r_name, c.cm_content, c.cm_time " +
                "FROM comment c " +
                "JOIN reader r ON c.r_id = r.r_id " +
                "JOIN books b ON c.b_id = b.b_id " +
                "WHERE b.w_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, writerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("cm_id"),
                        rs.getString("r_name"),
                        rs.getString("cm_content"),
                        rs.getTimestamp("cm_time")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载评论失败: " + ex.getMessage());
        }
    }

    private void handleReplyComment(ActionEvent e) {
        int selectedRow = commentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择一条评论进行回复。");
            return;
        }
        int cmId = (int) tableModel.getValueAt(selectedRow, 0);
        String replyContent = JOptionPane.showInputDialog(this, "请输入回复内容:");
        if (replyContent != null && !replyContent.isEmpty()) {
            String sql = "INSERT INTO wirter_reply (w_id, cm_id, wr_time, wr_content) VALUES (?, ?, NOW(), ?)";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, writerId);
                stmt.setInt(2, cmId);
                stmt.setString(3, replyContent);
                if (stmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "回复成功");
                    loadComments();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "回复失败: " + ex.getMessage());
            }
        }
    }

    private void handleDeleteComment(ActionEvent e) {
        int selectedRow = commentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择一条评论进行删除。");
            return;
        }
        int cmId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除这条评论吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM comment WHERE cm_id = ?";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, cmId);
                if (stmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "删除成功");
                    loadComments();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage());
            }
        }
    }
}
