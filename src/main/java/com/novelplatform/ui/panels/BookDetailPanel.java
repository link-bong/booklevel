package src.main.java.com.novelplatform.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import src.main.java.com.novelplatform.dao.BookDao;
import src.main.java.com.novelplatform.dao.BrowseDao;
import src.main.java.com.novelplatform.dao.CommentDao;
import src.main.java.com.novelplatform.ui.ReaderMainUI;
import src.main.java.com.novelplatform.util.DBUtil;

public class BookDetailPanel extends JPanel {
    private final int userId;
    private final int bookId;
    private ReaderMainUI previousFrame; // 修改为 ReaderMainUI 类型
    private final Runnable refreshCallback;

    public BookDetailPanel(int userId, String bookName, String author, ReaderMainUI previousFrame, Runnable refreshCallback) {
        this.userId = userId;
        this.bookId = getBookId(bookName, author);
        this.previousFrame = previousFrame;
        this.refreshCallback = refreshCallback; // 保存回调
        initializeUI();
    }

    private void initializeUI() {
        // 顶部工具栏
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backBtn = new JButton("← 返回");
        backBtn.addActionListener(e -> goBack());
        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(new JLabel("书籍详情", JLabel.CENTER), BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 信息面板美化
        JPanel infoPanel = createInfoPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 使用带滚动条的面板
        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // 操作按钮
        JPanel btnPanel = new JPanel();
        JButton addToShelfBtn = new JButton(isInShelf()? "已在书架" : "加入书架");
        JButton readBtn = new JButton("开始阅读");
        btnPanel.add(addToShelfBtn);
        btnPanel.add(readBtn);

        // 评论面板
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("书籍详情", infoPanel);
        tabPane.addTab("读者评论", createCommentPanel());

        add(tabPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // 事件处理
        addToShelfBtn.addActionListener(e -> toggleShelfStatus(addToShelfBtn));
        readBtn.addActionListener(e -> openReadingView());
    }

    private void addStyledLabel(JPanel panel, String prefix, String value) {
        JLabel label = new JLabel(prefix + value);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(label);
    }

    private void goBack() {
        previousFrame.getContentPane().removeAll();
        previousFrame.add(previousFrame.getTabbedPane());
        previousFrame.revalidate();
        previousFrame.repaint();
    }

    private int getBookId(String bookName, String author) {
        String sql = "SELECT b.b_id FROM books b " +
                "JOIN writer w ON b.w_id = w.w_id " +
                "WHERE b.b_name =? AND w.w_name =?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookName);
            stmt.setString(2, author);
            ResultSet rs = stmt.executeQuery();
            return rs.next()? rs.getInt(1) : -1;
        } catch (SQLException ex) {
            return -1;
        }
    }

    private boolean isInShelf() {
        String sql = "SELECT * FROM addbooks WHERE b_id =? AND bs_id IN " +
                "(SELECT bs_id FROM bookshelf WHERE r_id =?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, userId);
            return stmt.executeQuery().next();
        } catch (SQLException ex) {
            return false;
        }
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        try (ResultSet rs = BookDao.getBookDetails(bookId)) {
            if (rs.next()) {
                addStyledLabel(panel, "📖 书名：", rs.getString("b_name"));
                addStyledLabel(panel, "✍️ 作者：", rs.getString("w_name"));
                addStyledLabel(panel, "📌 状态：", rs.getString("b_state"));
                addStyledLabel(panel, "🏷️ 分类：", rs.getString("b_category"));
                addStyledLabel(panel, "👀 浏览量：", rs.getInt("view_count") + "");
                addStyledLabel(panel, "❤️ 收藏量：", rs.getInt("collect_count") + "");
            }
        } catch (SQLException ex) {
            panel.add(new JLabel("加载详情失败"));
        }
        return panel;
    }

    private JPanel createCommentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel commentModel = new DefaultTableModel(new String[]{"读者", "评论内容", "评论时间"}, 0);
        JTable commentTable = new JTable(commentModel);
        loadComments(commentModel);
        panel.add(new JScrollPane(commentTable), BorderLayout.CENTER);

        // 评论输入框
        JTextArea commentArea = new JTextArea(3, 20);
        JButton submitBtn = new JButton("发表评论");
        submitBtn.addActionListener(e -> {
            try {
                if (CommentDao.addComment(userId, bookId, commentArea.getText())) {
                    loadComments(commentModel); // 刷新评论列表
                    commentArea.setText("");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        panel.add(new JScrollPane(commentArea), BorderLayout.NORTH);
        panel.add(submitBtn, BorderLayout.SOUTH);
        return panel;
    }

    private void loadComments(DefaultTableModel model) {
        String sql = "SELECT r.r_name, c.cm_content, c.cm_time " +
                "FROM comment c " +
                "JOIN reader r ON c.r_id = r.r_id " +
                "WHERE c.b_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("r_name"), rs.getString("cm_content"), rs.getTimestamp("cm_time")});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void toggleShelfStatus(JButton btn) {
        if (isInShelf()) {
            JOptionPane.showMessageDialog(this, "这本书已在书架中");
            return;
        }

        if (!ensureBookshelfExists()) {
            JOptionPane.showMessageDialog(this, "初始化书架失败，请重试");
            return;
        }

        String sql = "INSERT INTO addbooks (bs_id, b_id) SELECT bs_id, ? FROM bookshelf WHERE r_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, userId);
            if (stmt.executeUpdate() > 0) {
                btn.setText("已在书架");
                JOptionPane.showMessageDialog(this, "添加到书架成功");
                if (refreshCallback != null) {
                    refreshCallback.run(); // 触发回调刷新书架
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库错误: " + ex.getMessage());
        }
    }


    // 确保书架存在的方法
    private boolean ensureBookshelfExists() {
        String checkSql = "SELECT bs_id FROM bookshelf WHERE r_id = ?";
        String insertSql = "INSERT INTO bookshelf (r_id, num) VALUES (?, 0)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                insertStmt.setInt(1, userId);
                return insertStmt.executeUpdate() > 0;
            }
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // BookDetailPanel.java
    private void openReadingView() {
        try {
            // 记录浏览行为
            BrowseDao.insertBrowseRecord(userId, bookId);

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new ReadingPanel(userId, bookId, this));
            parentFrame.revalidate();
            parentFrame.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "记录浏览历史失败");
        }
    }


}