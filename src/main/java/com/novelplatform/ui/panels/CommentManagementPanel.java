package src.main.java.com.novelplatform.ui.panels;

import src.main.java.com.novelplatform.dao.CommentDao;
import src.main.java.com.novelplatform.dao.CommentDao.CommentWithReply;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CommentManagementPanel extends JPanel {
    private final int writerId;
    // 修改列名，将评论ID替换为书名，并增加回复内容列
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"书名", "读者", "评论内容", "评论时间", "回复内容"}, 0
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
        try {
            // 使用 CommentDao 中的 getCommentsByWriter 方法获取评论数据
            java.util.List<CommentWithReply> comments = CommentDao.getCommentsByWriter(writerId);
            for (CommentWithReply comment : comments) {
                tableModel.addRow(new Object[]{
                        comment.getBookName(),
                        comment.getReaderName(),
                        comment.getCommentContent(),
                        comment.getCommentTime(),
                        comment.getReplyContent() != null ? comment.getReplyContent() : ""
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
        // 获取评论ID，由于列顺序改变，需要根据新的列顺序获取
        int cmId = getCommentIdFromRow(selectedRow);
        String replyContent = JOptionPane.showInputDialog(this, "请输入回复内容:");
        if (replyContent != null && !replyContent.isEmpty()) {
            try {
                if (CommentDao.addWriterReply(cmId, writerId, replyContent)) {
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
        // 获取评论ID，由于列顺序改变，需要根据新的列顺序获取
        int cmId = getCommentIdFromRow(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除这条评论吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (CommentDao.deleteComment(cmId, writerId)) {
                    JOptionPane.showMessageDialog(this, "删除成功");
                    loadComments();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage());
            }
        }
    }

    // 根据选中行获取评论ID
    private int getCommentIdFromRow(int row) {
        try {
            java.util.List<CommentWithReply> comments = CommentDao.getCommentsByWriter(writerId);
            return comments.get(row).getCommentId();
        } catch (SQLException | IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(this, "获取评论ID失败: " + ex.getMessage());
            return -1;
        }
    }
}