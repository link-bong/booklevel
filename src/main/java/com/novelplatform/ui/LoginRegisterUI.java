package src.main.java.com.novelplatform.ui;


import src.main.java.com.novelplatform.dao.UserDao;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginRegisterUI extends JFrame {
    private String userType;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    // 无参构造：显示身份选择
    public LoginRegisterUI() {
        new RoleSelectionUI();
        dispose();
    }
    // 有参构造：直接进入指定身份界面
    public LoginRegisterUI(String userType) {
        this.userType = userType;
        initUI();
    }

    private void initUI() {
        setTitle(userType.equals("reader") ? "读者平台" : "作者平台");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("登录", createLoginPanel());
        tabbedPane.addTab("注册", createRegisterPanel());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(new JLabel("用户名:"));
        panel.add(usernameField);
        panel.add(new JLabel("密码:"));
        panel.add(passwordField);

        JButton loginBtn = new JButton("登录");
        loginBtn.addActionListener(e -> handleLogin());
        panel.add(loginBtn);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField regUserField = new JTextField();
        JPasswordField regPassField = new JPasswordField();
        JPasswordField confirmPassField = new JPasswordField();

        panel.add(new JLabel("用户名:"));
        panel.add(regUserField);
        panel.add(new JLabel("密码:"));
        panel.add(regPassField);
        panel.add(new JLabel("确认密码:"));
        panel.add(confirmPassField);

        JButton regBtn = new JButton("注册");
        regBtn.addActionListener(e -> handleRegister(
                regUserField.getText(),
                new String(regPassField.getPassword()),
                new String(confirmPassField.getPassword())
        ));
        panel.add(regBtn);

        return panel;
    }

    // LoginRegisterUI.java 中的关键代码
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // 输入验证
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空");
            return;
        }

        try {
            // 验证登录
            boolean loginSuccess = UserDao.loginUser(username, password, userType);

            if (loginSuccess) {
                // 获取用户ID
                int userId = UserDao.getUserId(username, userType);

                // 在事件线程更新界面
                SwingUtilities.invokeLater(() -> {
                    // 根据身份跳转
                    if ("reader".equalsIgnoreCase(userType)) {
                        new ReaderMainUI(userId, username).setVisible(true);
                    } else if ("writer".equalsIgnoreCase(userType)) {
                        new WriterMainUI(userId, username).setVisible(true);
                    }
                    dispose(); // 关闭当前窗口
                });

            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库错误: " + ex.getMessage());
        }
    }

    private void handleRegister(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名或密码不能为空");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "两次密码不一致");
            return;
        }

        try {
            if (UserDao.usernameExists(username, userType)) {
                JOptionPane.showMessageDialog(this, "用户名已存在");
                return;
            }

            if (UserDao.registerUser(username, password, userType)) {
                JOptionPane.showMessageDialog(this, "注册成功");
                // TODO: 自动跳转到登录界面
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "注册失败");
        }
    }
}
