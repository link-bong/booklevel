package src.main.java.com.novelplatform.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import src.main.java.com.novelplatform.ui.ReaderMainUI;
import src.main.java.com.novelplatform.util.DBUtil;
import javax.swing.table.DefaultTableModel;

public class AuthorPanel extends JPanel {
    private final String authorName;
    private ReaderMainUI previousFrame; // 修改为 ReaderMainUI 类型

    public AuthorPanel(String authorName, ReaderMainUI previousFrame) {
        this.authorName = authorName;
        this.previousFrame = previousFrame;
        setLayout(new BorderLayout());
        loadAuthorInfo();
    }

    private void loadAuthorInfo() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT w.*, COUNT(b.b_id) AS book_count " +
                             "FROM writer w LEFT JOIN books b ON w.w_id = b.w_id " +
                             "WHERE w.w_name = ? GROUP BY w.w_id")) {

            stmt.setString(1, authorName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // 顶部工具栏
                JPanel topPanel = new JPanel(new BorderLayout());
                JButton backBtn = new JButton("← 返回");
                backBtn.addActionListener(e -> goBack());
                topPanel.add(backBtn, BorderLayout.WEST);
                topPanel.add(new JLabel("作者详情", JLabel.CENTER), BorderLayout.CENTER);
                add(topPanel, BorderLayout.NORTH);

                // 作者基本信息
                JPanel infoPanel = new JPanel(new GridLayout(4, 1));
                infoPanel.add(new JLabel("作者姓名: " + rs.getString("w_name")));
                infoPanel.add(new JLabel("作品数量: " + rs.getInt("book_count")));

                // 创建一个新面板，将 topPanel 和 infoPanel 组合
                JPanel northPanel = new JPanel(new BorderLayout());
                northPanel.add(topPanel, BorderLayout.NORTH);
                northPanel.add(infoPanel, BorderLayout.CENTER);

                // 将组合面板添加到 AuthorPanel 的 NORTH 区域
                add(northPanel, BorderLayout.NORTH);

                // 作品列表
                DefaultTableModel model = new DefaultTableModel(
                        new String[]{"书名", "状态", "分类"}, 0
                );
                loadAuthorBooks(rs.getInt("w_id"), model);
                JTable bookTable = new JTable(model);
                bookTable.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        int row = bookTable.rowAtPoint(evt.getPoint());
                        if (row >= 0) {
                            String bookName = (String) model.getValueAt(row, 0);
                            // 点击书名跳转到书籍详情页
                            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(AuthorPanel.this);
                            frame.getContentPane().removeAll();
                            frame.add(new BookDetailPanel(
                                    -1,
                                    bookName,
                                    authorName,
                                    previousFrame,
                                    () -> { // 添加第5个参数（即使不需要刷新也可以传空回调）
                                        if (previousFrame != null) {
                                            BookshelfPanel shelfPanel = previousFrame.getBookshelfPanel();
                                            if (shelfPanel != null) {
                                                shelfPanel.reloadBooks();
                                            }
                                        }
                                    }
                            )); // 传递 ReaderMainUI 实例
                            frame.revalidate();
                            frame.repaint();
                        }
                    }
                });
                JScrollPane scrollPane = new JScrollPane(bookTable);
                scrollPane.setBorder(BorderFactory.createTitledBorder("作品列表"));
                add(scrollPane, BorderLayout.CENTER);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载失败: " + ex.getMessage());
        }
    }

    private void loadAuthorBooks(int writerId, DefaultTableModel model) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT b_name, b_state, b_category FROM books WHERE w_id = ?")) {

            stmt.setInt(1, writerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("b_name"),
                        rs.getString("b_state"),
                        rs.getString("b_category")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void goBack() {
        previousFrame.getContentPane().removeAll();
        previousFrame.add(previousFrame.getTabbedPane());
        previousFrame.revalidate();
        previousFrame.repaint();
    }
}