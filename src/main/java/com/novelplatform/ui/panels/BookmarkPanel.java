package src.main.java.com.novelplatform.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import src.main.java.com.novelplatform.dao.BookmarkDao;
import src.main.java.com.novelplatform.util.DBUtil;

public class BookmarkPanel extends JPanel {
    private final int userId;
    private int hoverRow = -1;
    private JTable bookmarkTable;

    public BookmarkPanel(int userId) {
        this.userId = userId;
        initUI();
    }

    private int getChapterId(String bookName, String chapterName) {
        String sql = "SELECT c.cp_id FROM chapter c " +
                "JOIN books b ON c.b_id = b.b_id " +
                "WHERE b.b_name = ? AND c.cp_name = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookName);
            stmt.setString(2, chapterName);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("cp_id") : -1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

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

    private void initUI() {
        setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(new String[]{"书籍", "章节", "添加时间"}, 0);
        bookmarkTable = new JTable(model);
        loadBookmarks(model);

        bookmarkTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = bookmarkTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        String bookName = (String) model.getValueAt(row, 0);
                        String chapterName = (String) model.getValueAt(row, 1);
                        int chapterId = getChapterId(bookName, chapterName);
                        if (chapterId != -1) {
                            handleBookmarkAction(chapterId);
                        }
                    }
                }
            }
        });

        bookmarkTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row == hoverRow ? Color.LIGHT_GRAY : table.getBackground());
                return c;
            }
        });

        add(new JScrollPane(bookmarkTable), BorderLayout.CENTER);
    }

    private void handleBookmarkAction(int chapterId) {
        boolean isBookmarked = isChapterBookmarked(chapterId);
        int option = isBookmarked ?
                JOptionPane.showConfirmDialog(this, "是否移除该书签？", "确认移除", JOptionPane.YES_NO_OPTION) :
                JOptionPane.showConfirmDialog(this, "是否添加该书签？", "确认添加", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (isBookmarked) {
                if (BookmarkDao.removeBookmark(userId, chapterId)) {
                    JOptionPane.showMessageDialog(this, "书签已移除");
                } else {
                    JOptionPane.showMessageDialog(this, "移除书签失败");
                }
            } else {
                if (BookmarkDao.addBookmark(userId, chapterId)) {
                    JOptionPane.showMessageDialog(this, "书签已添加");
                } else {
                    JOptionPane.showMessageDialog(this, "添加书签失败");
                }
            }
            loadBookmarks((DefaultTableModel) bookmarkTable.getModel());
        }
    }

    private void loadBookmarks(DefaultTableModel model) {
        String sql = "SELECT b.b_name, c.cp_name, bm.bm_time " +
                "FROM bookmark bm " +
                "JOIN chapter c ON bm.cp_id = c.cp_id " +
                "JOIN books b ON c.b_id = b.b_id " +
                "WHERE bm.r_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("b_name"),
                        rs.getString("cp_name"),
                        rs.getTimestamp("bm_time")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}