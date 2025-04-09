package src.main.java.com.novelplatform.ui.panels;

import src.main.java.com.novelplatform.dao.BookDao;
import src.main.java.com.novelplatform.util.DBUtil;
import src.main.java.com.novelplatform.ui.dialogs.BookEditDialog;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class BookManagementPanel extends JPanel {
    private final int writerId;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"书名", "分类", "状态", "收藏量"}, 0
    );
    private final JTable bookTable = new JTable(tableModel);

    public BookManagementPanel(int writerId) {
        this.writerId = writerId;
        initUI();
        loadBooks();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 操作工具栏
        JPanel toolPanel = new JPanel();
        JButton addBtn = new JButton("新增作品");
        JButton editBtn = new JButton("编辑选中");
        JButton refreshBtn = new JButton("刷新列表");

        addBtn.addActionListener(this::handleAddBook);
        editBtn.addActionListener(this::handleEditBook);
        refreshBtn.addActionListener(e -> loadBooks());

        toolPanel.add(addBtn);
        toolPanel.add(editBtn);
        toolPanel.add(refreshBtn);

        // 书籍列表
        bookTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        add(toolPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        String sql = "SELECT b_id, b_name, b_category, b_state, " +
                "(SELECT COUNT(*) FROM addbooks WHERE b_id = books.b_id) AS collect_count " +
                "FROM books WHERE w_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, writerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("b_name"),
                        rs.getString("b_category"),
                        rs.getString("b_state"),
                        rs.getInt("collect_count")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载失败: " + ex.getMessage());
        }
    }

    private void handleAddBook(ActionEvent e) {
        // 实现新增书籍弹窗逻辑
        new BookEditDialog((JFrame) SwingUtilities.getWindowAncestor(this), writerId, true, -1).setVisible(true);
    }

    private void handleEditBook(ActionEvent e) {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择一本书进行编辑。");
            return;
        }
        String bookName = (String) tableModel.getValueAt(selectedRow, 0);
        int bookId = getBookId(bookName);
        new BookEditDialog((JFrame) SwingUtilities.getWindowAncestor(this), writerId, false, bookId).setVisible(true);
    }

    private int getBookId(String bookName) {
        String sql = "SELECT b_id FROM books WHERE b_name = ? AND w_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookName);
            stmt.setInt(2, writerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("b_id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }
}