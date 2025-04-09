package src.main.java.com.novelplatform.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import src.main.java.com.novelplatform.ui.panels.BookCityPanel;
import src.main.java.com.novelplatform.ui.panels.BookshelfPanel;
import src.main.java.com.novelplatform.ui.panels.UserPanel;

public class ReaderMainUI extends JFrame {
    private final int userId;
    private final String username;
    private final JTabbedPane tabbedPane; // 添加成员变量

    public ReaderMainUI(int userId, String username) {
        this.userId = userId;
        this.username = username;
        tabbedPane = new JTabbedPane(); // 初始化成员变量
        initUI();
    }

    private void initUI() {
        setTitle("读者主页 - 欢迎 " + username);
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 添加标签页
        tabbedPane.addTab("书城", new BookCityPanel(userId));
        tabbedPane.addTab("书架", new BookshelfPanel(userId));
        tabbedPane.addTab("我的", new UserPanel(userId, this));

        add(tabbedPane); // 使用成员变量
        setVisible(true);
    }

    public JTabbedPane getTabbedPane() { // 添加 getter 方法
        return tabbedPane;
    }

    public BookshelfPanel getBookshelfPanel() {
        // 遍历标签页，找到书架面板
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component tab = tabbedPane.getComponentAt(i);
            if (tab instanceof BookshelfPanel) {
                return (BookshelfPanel) tab;
            }
        }
        return null;
    }
}