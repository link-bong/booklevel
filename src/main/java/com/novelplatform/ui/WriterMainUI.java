package src.main.java.com.novelplatform.ui;

import src.main.java.com.novelplatform.ui.panels.*;
import javax.swing.*;
import java.awt.*;
import src.main.java.com.novelplatform.ui.dialogs.BookUploadDialog;

public class WriterMainUI extends JFrame {
    private final int writerId;
    private final String writerName;
    private final JTabbedPane tabbedPane = new JTabbedPane();

    public WriterMainUI(int writerId, String writerName) {
        this.writerId = writerId;
        this.writerName = writerName;
        initUI();
    }

    private void initUI() {
        setTitle("作者工作台 - " + writerName);
        setSize(800, 600); // 与读者界面一致
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 顶部工具栏
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("欢迎，" + writerName, JLabel.CENTER), BorderLayout.CENTER);

        JButton logoutBtn = new JButton("退出登录");
        logoutBtn.addActionListener(e -> {
            new LoginRegisterUI();
            dispose();
        });
        topPanel.add(logoutBtn, BorderLayout.EAST);

        // 核心功能模块
        tabbedPane.addTab("作品管理", new BookManagementPanel(writerId));
        tabbedPane.addTab("评论管理", new CommentManagementPanel(writerId));
        tabbedPane.addTab("账户设置", new AccountPanel(writerId));
// WriterMainUI.java 中添加上传按钮
        JButton uploadBtn = new JButton("上传新书");
        uploadBtn.addActionListener(e -> new BookUploadDialog(this, writerId).setVisible(true));
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
}