package src.main.java.com.novelplatform.ui.panels;

import src.main.java.com.novelplatform.dao.UserDao;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class AccountPanel extends JPanel {
    private final int writerId;
    private final JPasswordField oldPassField = new JPasswordField(20);
    private final JPasswordField newPassField = new JPasswordField(20);
    private final JPasswordField confirmPassField = new JPasswordField(20);

    public AccountPanel(int writerId) {
        this.writerId = writerId;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("旧密码:"));
        add(oldPassField);
        add(new JLabel("新密码:"));
        add(newPassField);
        add(new JLabel("确认密码:"));
        add(confirmPassField);

        JButton submitBtn = new JButton("修改密码");
        submitBtn.addActionListener(this::handlePasswordChange);
        add(submitBtn);
    }

    private void handlePasswordChange(ActionEvent e) {
        String oldPass = new String(oldPassField.getPassword());
        String newPass = new String(newPassField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "两次输入密码不一致");
            return;
        }

        try {
            if (UserDao.updatePassword("writer", writerId, oldPass, newPass)) {
                JOptionPane.showMessageDialog(this, "密码修改成功");
            } else {
                JOptionPane.showMessageDialog(this, "旧密码错误");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "修改失败: " + ex.getMessage());
        }
    }
}
