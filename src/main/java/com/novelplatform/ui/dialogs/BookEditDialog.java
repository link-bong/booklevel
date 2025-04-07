package src.main.java.com.novelplatform.ui.dialogs;

import src.main.java.com.novelplatform.dao.BookDao;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import src.main.java.com.novelplatform.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookEditDialog extends JDialog {
    private final JTextField nameField = new JTextField(20);
    private final JComboBox<String> categoryCombo = new JComboBox<>(
            new String[]{"小说", "经典文学", "武侠", "仙侠", "都市"}
    );
    private final JComboBox<String> stateCombo = new JComboBox<>(
            new String[]{"连载中", "已完结", "暂停更新"}
    );

    public BookEditDialog(JFrame parent, int writerId, boolean isNew) {
        super(parent, isNew ? "新增书籍" : "编辑书籍", true);
        initUI(writerId, isNew);
    }

    private void initUI(int writerId, boolean isNew) {
        setLayout(new GridLayout(4, 2, 10, 10));
        add(new JLabel("书名:"));
        add(nameField);
        add(new JLabel("分类:"));
        add(categoryCombo);
        add(new JLabel("状态:"));
        add(stateCombo);

        JButton confirmBtn = new JButton("确认");
        confirmBtn.addActionListener(e -> handleSave(writerId, isNew));
        add(confirmBtn);

        setSize(400, 200);
        setLocationRelativeTo(getParent());
    }

    private void handleSave(int writerId, boolean isNew) {
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
                // 编辑时需要设置b_id
                // stmt.setInt(4, bookId);
            }

            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "操作成功");
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "操作失败: " + ex.getMessage());
        }
    }
}
