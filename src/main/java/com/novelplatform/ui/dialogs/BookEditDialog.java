package src.main.java.com.novelplatform.ui.dialogs;

import src.main.java.com.novelplatform.dao.BookDao;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import src.main.java.com.novelplatform.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookEditDialog extends JDialog {
    private final JTextField nameField = new JTextField(20);
    private final JComboBox<String> categoryCombo = new JComboBox<>(
            new String[]{"小说", "经典文学", "武侠", "仙侠", "都市"}
    );
    private final JComboBox<String> stateCombo = new JComboBox<>(
            new String[]{"连载中", "已完结", "暂停更新"}
    );
    private final int bookId;
    private final boolean isNew;
    private final int writerId;

    public BookEditDialog(JFrame parent, int writerId, boolean isNew, int bookId) {
        super(parent, isNew ? "新增书籍" : "编辑书籍", true);
        this.writerId = writerId;
        this.isNew = isNew;
        this.bookId = bookId;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(6, 2, 10, 10));
        add(new JLabel("书名:"));
        add(nameField);
        add(new JLabel("分类:"));
        add(categoryCombo);
        add(new JLabel("状态:"));
        add(stateCombo);

        if (!isNew) {
            loadBookInfo();
        }

        JButton confirmBtn = new JButton("确认");
        confirmBtn.addActionListener(e -> handleSave());
        add(confirmBtn);

        JButton addChapterBtn = new JButton("添加章节");
        addChapterBtn.addActionListener(e -> handleAddChapter());
        add(addChapterBtn);

        JButton deleteBtn = new JButton("删除书籍");
        deleteBtn.addActionListener(e -> handleDeleteBook());
        add(deleteBtn);

        setSize(400, 300);
        setLocationRelativeTo(getParent());
    }

    private void loadBookInfo() {
        String sql = "SELECT b_name, b_category, b_state FROM books WHERE b_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("b_name"));
                categoryCombo.setSelectedItem(rs.getString("b_category"));
                stateCombo.setSelectedItem(rs.getString("b_state"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void handleSave() {
        String sql = isNew ?
                "INSERT INTO books (b_name, b_category, b_state, w_id) VALUES (?,?,?,?)" :
                "UPDATE books SET b_name=?, b_category=?, b_state=? WHERE b_id=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nameField.getText());
            stmt.setString(2, (String) categoryCombo.getSelectedItem());
            stmt.setString(3, (String) stateCombo.getSelectedItem());

            if (isNew) {
                stmt.setInt(4, writerId);
            } else {
                stmt.setInt(4, bookId);
            }

            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "操作成功");
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "操作失败: " + ex.getMessage());
        }
    }

    private void handleAddChapter() {
        String chapterName = JOptionPane.showInputDialog(this, "请输入章节名称:");
        if (chapterName != null && !chapterName.isEmpty()) {
            String sql = "INSERT INTO chapter (b_id, cp_name) VALUES (?, ?)";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, bookId);
                stmt.setString(2, chapterName);
                if (stmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "章节添加成功");
                } else {
                    JOptionPane.showMessageDialog(this, "章节添加失败");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "数据库错误: " + ex.getMessage());
            }
        }
    }

    private void handleDeleteBook() {
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除这本书吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM books WHERE b_id = ?";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, bookId);
                if (stmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "书籍删除成功");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "书籍删除失败");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "数据库错误: " + ex.getMessage());
            }
        }
    }
}