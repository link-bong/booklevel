package src.main.java.com.novelplatform.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoleSelectionUI extends JFrame {
    public RoleSelectionUI() {
        setTitle("选择身份");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("请选择您的身份：");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnReader = new JButton("读者");
        JButton btnWriter = new JButton("作者");

        btnReader.addActionListener(e -> {
            new LoginRegisterUI("reader");
            dispose();
        });

        btnWriter.addActionListener(e -> {
            new LoginRegisterUI("writer");
            dispose();
        });

        panel.add(label);
        panel.add(btnReader);
        panel.add(btnWriter);
        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new RoleSelectionUI();
    }
}
