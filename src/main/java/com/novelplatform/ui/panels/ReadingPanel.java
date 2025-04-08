package src.main.java.com.novelplatform.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import src.main.java.com.novelplatform.dao.BookmarkDao;
import src.main.java.com.novelplatform.dao.BrowseDao;
import src.main.java.com.novelplatform.util.DBUtil;

public class ReadingPanel extends JPanel {
    private final int userId;
    private final int bookId;
    private JPanel previousPanel;

    public ReadingPanel(int userId, int bookId, JPanel previousPanel) {
        this.userId = userId;
        this.bookId = bookId;
        this.previousPanel = previousPanel;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 顶部工具栏
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backBtn = new JButton("← 返回");
        backBtn.addActionListener(e -> goBack());
        JButton bookmarkBtn = new JButton("书签");
        bookmarkBtn.addActionListener(e -> showBookmarks());
        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(bookmarkBtn, BorderLayout.EAST);
        topPanel.add(new JLabel("章节列表", JLabel.CENTER), BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 章节列表
        DefaultTableModel model = new DefaultTableModel(new String[]{"章节"}, 0);
        JTable chapterTable = new JTable(model);
        loadChapters(model);

        // 双击添加/移除书签
        chapterTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = chapterTable.getSelectedRow();
                    int chapterId = getChapterId(row);
                    if (chapterId != -1) {
                        if (isChapterBookmarked(chapterId)) {
                            BookmarkDao.removeBookmark(userId, chapterId);
                            JOptionPane.showMessageDialog(null, "已移除书签");
                        } else {
                            BookmarkDao.addBookmark(userId, chapterId);
                            JOptionPane.showMessageDialog(null, "已添加书签");
                        }
                        loadChapters(model); // 刷新章节状态
                    }
                }
            }
        });

        add(new JScrollPane(chapterTable), BorderLayout.CENTER);
    }

    // 添加检查书签是否存在的方法
    private boolean isChapterBookmarked(int chapterId) {
        String sql = "SELECT COUNT(*) FROM bookmark WHERE r_id = ? AND cp_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, chapterId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // 修改 loadChapters 方法显示书签状态
    private void loadChapters(DefaultTableModel model) {
        String sql = "SELECT c.cp_id, c.cp_name, " +
                "CASE WHEN bm.cp_id IS NULL THEN '' ELSE '' END AS status " +
                "FROM chapter c " +
                "LEFT JOIN bookmark bm ON c.cp_id = bm.cp_id AND bm.r_id = ? " +
                "WHERE c.b_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("cp_name") + " " + rs.getString("status") + ""
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void goBack() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.add(previousPanel);
        frame.revalidate();
        frame.repaint();
    }

    private void showBookmarks() {
        // 实现显示书签功能
        DefaultTableModel bookmarkModel = new DefaultTableModel(new String[]{"章节"}, 0);
        loadBookmarks(bookmarkModel);
        JTable bookmarkTable = new JTable(bookmarkModel);
        bookmarkTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = bookmarkTable.getSelectedRow();
                    int chapterId = getBookmarkChapterId(row);
                    if (chapterId != -1) {
                        BookmarkDao.removeBookmark(userId, chapterId);
                        JOptionPane.showMessageDialog(null, "已移除书签");
                        loadBookmarks(bookmarkModel);
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(bookmarkTable);
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "书签列表", true);
        dialog.add(scrollPane);
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }


    private int getChapterId(int row) {
        String sql = "SELECT cp_id FROM chapter WHERE b_id = ? LIMIT ?, 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, row);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cp_id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private void loadBookmarks(DefaultTableModel model) {
        String sql = "SELECT c.cp_name FROM chapter c " +
                "JOIN bookmark bm ON c.cp_id = bm.cp_id " +
                "WHERE bm.r_id = ? AND c.b_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            ResultSet rs = stmt.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("cp_name")});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private int getBookmarkChapterId(int row) {
        String sql = "SELECT c.cp_id FROM chapter c " +
                "JOIN bookmark bm ON c.cp_id = bm.cp_id " +
                "WHERE bm.r_id = ? AND c.b_id = ? LIMIT ?, 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setInt(3, row);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cp_id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }
    // 在打开阅读界面时调用
    private void openReadingView() {
        try {
            // 记录浏览行为
            BrowseDao.insertBrowseRecord(userId, bookId);

            // 打开阅读窗口的逻辑...
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "记录浏览历史失败");
        }
    }
}