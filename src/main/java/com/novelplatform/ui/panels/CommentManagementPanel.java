package src.main.java.com.novelplatform.ui.panels;

// CommentManagementPanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import src.main.java.com.novelplatform.dao.CommentDao;
import src.main.java.com.novelplatform.dao.CommentDao.CommentWithReply;
import src.main.java.com.novelplatform.util.DBUtil;

public class CommentManagementPanel extends JPanel {
    private final int writerId;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"书籍", "读者", "评论内容", "评论时间", "回复内容", "回复时间", "操作"}, 0
    );
    private final JTable commentTable = new JTable(tableModel);

    public CommentManagementPanel(int writerId) {
        this.writerId = writerId;
        initUI();
        loadComments();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 评论列表
        commentTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(commentTable);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadComments() {
        tableModel.setRowCount(0);
        try {
            List<CommentWithReply> comments = CommentDao.getCommentsByWriter(writerId);
            for (CommentWithReply comment : comments) {
                JButton replyBtn = new JButton("回复");
                replyBtn.addActionListener(new ReplyActionListener(comment.getCommentId()));

                JButton deleteBtn = new JButton("删除");
                deleteBtn.addActionListener(new DeleteActionListener(comment.getCommentId()));

                JPanel actionPanel = new JPanel();
                actionPanel.add(replyBtn);
                actionPanel.add(deleteBtn);

                tableModel.addRow(new Object[]{
                        comment.getBookName(),
                        comment.getReaderName(),
                        comment.getCommentContent(),
                        comment.getCommentTime(),
                        comment.getReplyContent(),
                        comment.getReplyTime(),
                        actionPanel
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载评论失败: " + ex.getMessage());
        }
    }

    private class ReplyActionListener implements ActionListener {
        private final int commentId;

        public ReplyActionListener(int commentId) {
            this.commentId = commentId;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String replyContent = JOptionPane.showInputDialog("请输入回复内容");
            if (replyContent != null && !replyContent.isEmpty()) {
                try {
                    if (CommentDao.addWriterReply(commentId, writerId, replyContent)) {
                        JOptionPane.showMessageDialog(CommentManagementPanel.this, "回复成功");
                        loadComments();
                    } else {
                        JOptionPane.showMessageDialog(CommentManagementPanel.this, "回复失败");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(CommentManagementPanel.this, "数据库错误: " + ex.getMessage());
                }
            }
        }
    }

    private class DeleteActionListener implements ActionListener {
        private final int commentId;

        public DeleteActionListener(int commentId) {
            this.commentId = commentId;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int confirm = JOptionPane.showConfirmDialog(CommentManagementPanel.this, "确定要删除该评论吗？", "确认删除", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (CommentDao.deleteComment(commentId, writerId)) {
                        JOptionPane.showMessageDialog(CommentManagementPanel.this, "删除成功");
                        loadComments();
                    } else {
                        JOptionPane.showMessageDialog(CommentManagementPanel.this, "删除失败");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(CommentManagementPanel.this, "数据库错误: " + ex.getMessage());
                }
            }
        }
    }
}
