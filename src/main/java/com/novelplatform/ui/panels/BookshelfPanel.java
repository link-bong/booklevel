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

import src.main.java.com.novelplatform.ui.ReaderMainUI;
import src.main.java.com.novelplatform.util.DBUtil;

public class BookshelfPanel extends JPanel {
    private final int userId;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"书名", "作者"}, 0
    );
    private JTable table;

    public BookshelfPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());
        initializeComponents();
        loadBooks();
    }

    private void initializeComponents() {
        JLabel title = new JLabel("我的书架 (" + getBookCount() + "本)");
        title.setFont(new Font("宋体", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        table = new JTable(model);
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            private Timer timer; // 用于区分单击和双击
            private int clickCount = 0;

            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col < 0) return;

                // 单击选中行
                table.setRowSelectionInterval(row, row);

                // 处理双击事件
                if (e.getClickCount() == 2) {
                    if (timer != null) timer.stop(); // 停止单击计时器
                    handleDoubleClick(row, col);
                } else {
                    clickCount++;
                    if (timer == null) {
                        timer = new Timer(300, event -> {
                            if (clickCount == 1) {
                                handleSingleClick(row, col);
                            }
                            clickCount = 0;
                            timer.stop();
                        });
                    }
                    timer.restart();
                }
            }

            private void handleSingleClick(int row, int col) {
                // 单击选中行（已默认实现）
            }

            private void handleDoubleClick(int row, int col) {
                String bookName = (String) model.getValueAt(row, 0);
                String author = (String) model.getValueAt(row, 1);
                if (col == 0) { // 双击书名
                    openBookDetail(bookName, author);
                } else if (col == 1) { // 双击作者
                    openAuthorPage(author);
                }
            }
        });

        JButton removeBtn = new JButton("移除选中书籍");
        removeBtn.addActionListener(e -> removeSelectedBook(table));
        add(removeBtn, BorderLayout.SOUTH);
    }

    private int getBookCount() {
        String sql = "SELECT COUNT(*) FROM addbooks WHERE bs_id IN " +
                "(SELECT bs_id FROM bookshelf WHERE r_id = ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public void reloadBooks() {
        loadBooks();
    }

    private void loadBooks() {
        model.setRowCount(0);
        String sql = "SELECT b.b_name, w.w_name " +
                "FROM addbooks a " +
                "JOIN books b ON a.b_id = b.b_id " +
                "JOIN writer w ON b.w_id = w.w_id " +
                "WHERE a.bs_id IN (SELECT bs_id FROM bookshelf WHERE r_id = ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("b_name"),
                        rs.getString("w_name")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载失败: " + ex.getMessage());
        }
        updateTitle();
    }

    private void removeSelectedBook(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要移除《" + model.getValueAt(row, 0) + "》吗？",
                "确认移除",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String bookName = (String) model.getValueAt(row, 0);
            String sqlGetBsId = "SELECT a.bs_id FROM addbooks a " +
                    "JOIN books b ON a.b_id = b.b_id " +
                    "WHERE b.b_name = ? AND a.bs_id IN (SELECT bs_id FROM bookshelf WHERE r_id = ?)";
            int bsId = -1;
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmtGetBsId = conn.prepareStatement(sqlGetBsId)) {
                stmtGetBsId.setString(1, bookName);
                stmtGetBsId.setInt(2, userId);
                ResultSet rs = stmtGetBsId.executeQuery();
                if (rs.next()) {
                    bsId = rs.getInt("bs_id");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "获取书籍 ID 失败");
                return;
            }

            if (bsId != -1) {
                String sql = "DELETE FROM addbooks WHERE bs_id = ?";
                try (Connection conn = DBUtil.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, bsId);
                    if (stmt.executeUpdate() > 0) {
                        model.removeRow(row);
                        updateTitle();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "移除失败");
                }
            }
        }
    }

    private void updateTitle() {
        ((JLabel) getComponent(0)).setText("我的书架 (" + model.getRowCount() + "本)");
    }

    private void openBookDetail(String bookName, String author) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();

        frame.add(new BookDetailPanel(
                userId,
                bookName,
                author,
                (ReaderMainUI) frame,
                this::loadBooks // ✅ 添加第5个参数
        ));

        frame.revalidate();
        frame.repaint();
    }

    private void openAuthorPage(String authorName) {
        // 这里可根据实际情况实现打开作者页面的逻辑，例如弹出新窗口显示作者相关书籍
        JOptionPane.showMessageDialog(this, "你点击了作者：" + authorName);
    }
}