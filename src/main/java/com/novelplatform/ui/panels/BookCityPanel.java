package src.main.java.com.novelplatform.ui.panels;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import src.main.java.com.novelplatform.util.DBUtil;
import javax.swing.table.DefaultTableModel;
import src.main.java.com.novelplatform.ui.ReaderMainUI;

public class BookCityPanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private int userId;

    public BookCityPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        // 搜索栏
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("搜索");
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        add(searchPanel, BorderLayout.NORTH);

        // 书籍表格
        String[] columns = {"书名", "作者", "状态", "分类", "浏览量", "收藏量"};
        tableModel = new DefaultTableModel(columns, 0);
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // 双击事件
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = bookTable.rowAtPoint(e.getPoint());
                int col = bookTable.columnAtPoint(e.getPoint());

                if (row >= 0 && col >= 0) {
                    String bookName = (String) tableModel.getValueAt(row, 0);
                    String author = (String) tableModel.getValueAt(row, 1);

                    // 根据点击列判断跳转类型
                    if (col == 0) { // 点击书名列
                        openBookDetail(bookName, author);
                    } else if (col == 1) { // 点击作者列
                        openAuthorDetail(author);
                    }
                }
            }
        });

        // 单元格渲染器
        bookTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // 设置可点击列的样式
                if (column == 0 || column == 1) { // 书名和作者列
                    c.setForeground(Color.BLUE);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.BLACK);
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }
                return c;
            }
        });

        // 搜索事件
        searchBtn.addActionListener(e -> searchBooks(searchField.getText()));
        searchBooks(""); // 初始加载
    }

    private void searchBooks(String keyword) {
        tableModel.setRowCount(0);
        String sql = "SELECT b.b_name, w.w_name, b.b_state, b.b_category, " +
                "(SELECT COUNT(*) FROM browse WHERE b_id = b.b_id) AS view_count, " +
                "(SELECT COUNT(*) FROM addbooks WHERE b_id = b.b_id) AS collect_count " +
                "FROM books b JOIN writer w ON b.w_id = w.w_id " +
                "WHERE b.b_name LIKE ? OR w.w_name LIKE ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("b_name"),
                        rs.getString("w_name"),
                        rs.getString("b_state"),
                        rs.getString("b_category"),
                        rs.getInt("view_count"),
                        rs.getInt("collect_count")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "搜索失败");
        }
    }

    private void openBookDetail(String bookName, String author) {
        ReaderMainUI parentFrame = (ReaderMainUI) SwingUtilities.getWindowAncestor(this);
        parentFrame.getContentPane().removeAll();
        parentFrame.add(new BookDetailPanel(
                userId,
                bookName,
                author,
                parentFrame,
                () -> {
                    // 获取书架面板并刷新
                    BookshelfPanel shelfPanel = parentFrame.getBookshelfPanel();
                    if (shelfPanel != null) {
                        shelfPanel.reloadBooks();
                    }
                }
        ));
        parentFrame.revalidate();
        parentFrame.repaint();
    }


    private void openAuthorDetail(String authorName) {
        ReaderMainUI parentFrame = (ReaderMainUI) SwingUtilities.getWindowAncestor(this);
        parentFrame.getContentPane().removeAll();
        parentFrame.add(new AuthorPanel(authorName, parentFrame)); // 传递 ReaderMainUI 实例
        parentFrame.revalidate();
        parentFrame.repaint();
    }

}