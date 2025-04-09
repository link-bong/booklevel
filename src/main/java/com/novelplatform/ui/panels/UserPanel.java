package src.main.java.com.novelplatform.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

import src.main.java.com.novelplatform.dao.BrowseDao;
import src.main.java.com.novelplatform.util.DBUtil;
import src.main.java.com.novelplatform.ui.LoginRegisterUI;
import src.main.java.com.novelplatform.ui.ReaderMainUI;
import javax.swing.table.DefaultTableModel;

public class UserPanel extends JPanel {
    private static UserPanel instance;
    private int userId;
    private JFrame parent;
    private DefaultTableModel browseHistoryModel;

    public UserPanel(int userId, JFrame parent) {
        this.userId = userId;
        this.parent = parent;
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("浏览历史", createHistoryPanel());
        tabbedPane.addTab("我的互动", createInteractionPanel());
        tabbedPane.addTab("账户管理", createAccountPanel());
        add(tabbedPane);
    }

    public static UserPanel getInstance(int userId, JFrame parent) {
        if (instance == null) {
            instance = new UserPanel(userId, parent);
        }
        return instance;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"书名", "最后浏览时间"}, 0);
        browseHistoryModel = new DefaultTableModel(new String[]{"书名", "最后浏览时间"}, 0);

        try (ResultSet rs = BrowseDao.getRecentBrowseHistory(userId)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                Timestamp lastBrowseTime = rs.getTimestamp("last_browse_time");
                String formattedTime = lastBrowseTime != null ? dateFormat.format(lastBrowseTime) : "";
                model.addRow(new Object[]{
                        rs.getString("b_name"),
                        formattedTime
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton changeUserBtn = new JButton("修改用户名");
        JButton changePassBtn = new JButton("修改密码");
        JButton logoutBtn = new JButton("退出登录");

        changeUserBtn.addActionListener(e -> showChangeUsernameDialog());
        changePassBtn.addActionListener(e -> showChangePasswordDialog());
        logoutBtn.addActionListener(e -> {
            parent.dispose();
            new LoginRegisterUI();
        });

        panel.add(changeUserBtn);
        panel.add(changePassBtn);
        panel.add(logoutBtn);
        return panel;
    }

    private void showChangeUsernameDialog() {
        JDialog dialog = new JDialog(parent, "修改用户名", true);
        dialog.setSize(300, 150);

        JPanel panel = new JPanel(new GridLayout(3, 1));
        JTextField newNameField = new JTextField();
        JButton confirmBtn = new JButton("确认修改");

        panel.add(new JLabel("新用户名:"));
        panel.add(newNameField);
        panel.add(confirmBtn);

        confirmBtn.addActionListener(e -> {
            String newName = newNameField.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "用户名不能为空");
                return;
            }

            String sql = "UPDATE reader SET r_name = ? WHERE r_id = ?";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newName);
                stmt.setInt(2, userId);
                if (stmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(dialog, "修改成功");
                    dialog.dispose();
                    parent.dispose();
                    new ReaderMainUI(userId, newName);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "用户名已存在");
            }
        });

        dialog.add(panel);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(parent, "修改密码", true);
        dialog.setSize(300, 200);

        JPanel panel = new JPanel(new GridLayout(4, 1));
        JPasswordField oldPassField = new JPasswordField();
        JPasswordField newPassField = new JPasswordField();
        JPasswordField confirmPassField = new JPasswordField();
        JButton confirmBtn = new JButton("确认修改");

        panel.add(new JLabel("旧密码:"));
        panel.add(oldPassField);
        panel.add(new JLabel("新密码:"));
        panel.add(newPassField);
        panel.add(new JLabel("确认密码:"));
        panel.add(confirmPassField);
        panel.add(confirmBtn);

        confirmBtn.addActionListener(e -> {
            String oldPass = new String(oldPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "密码不能为空");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog, "两次输入的密码不一致");
                return;
            }

            // 验证旧密码并更新新密码的逻辑
            // 这里需要根据实际情况添加验证旧密码的逻辑
            String sql = "UPDATE reader SET r_password = ? WHERE r_id = ? AND r_password = ?";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newPass);
                stmt.setInt(2, userId);
                stmt.setString(3, oldPass);
                if (stmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(dialog, "密码修改成功");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "旧密码输入错误");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "密码修改失败");
            }
        });

        dialog.add(panel);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public void refreshBrowseHistory() {
        browseHistoryModel.setRowCount(0);
        try (ResultSet rs = BrowseDao.getRecentBrowseHistory(userId)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                Timestamp lastBrowseTime = rs.getTimestamp("last_browse_time");
                String formattedTime = lastBrowseTime != null ? dateFormat.format(lastBrowseTime) : "";
                browseHistoryModel.addRow(new Object[]{
                        rs.getString("b_name"),
                        formattedTime
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private JPanel createInteractionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"书籍", "我的评论", "回复内容", "回复时间"}, 0
        );

        String sql = "SELECT b.b_name, c.cm_content, rr.rr_content, rr.rr_time " +
                "FROM comment c " +
                "LEFT JOIN reader_reply rr ON c.cm_id = rr.cm_id " +
                "JOIN books b ON c.b_id = b.b_id " +
                "WHERE c.r_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("b_name"),
                        rs.getString("cm_content"),
                        rs.getString("rr_content"),
                        rs.getTimestamp("rr_time")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        panel.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        return panel;
    }
}