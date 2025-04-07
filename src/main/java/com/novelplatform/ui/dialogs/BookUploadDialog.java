package src.main.java.com.novelplatform.ui.dialogs;
import src.main.java.com.novelplatform.dao.BookDao;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import src.main.java.com.novelplatform.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

// BookUploadDialog.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import src.main.java.com.novelplatform.util.DBUtil;

public class BookUploadDialog extends JDialog {
    private final int writerId;
    private JTextField bookNameField;
    private JTextField categoryField;
    private JTextField stateField;
    private JTextArea chapterArea;

    public BookUploadDialog(JFrame parent, int writerId) {
        super(parent, "上传小说", true);
        this.writerId = writerId;
        initUI();
        setSize(400, 400);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(5, 2));

        JLabel bookNameLabel = new JLabel("书名:");
        bookNameField = new JTextField();
        JLabel categoryLabel = new JLabel("分类:");
        categoryField = new JTextField();
        JLabel stateLabel = new JLabel("状态:");
        stateField = new JTextField();
        JLabel chapterLabel = new JLabel("章节（每行一个章节名）:");
        chapterArea = new JTextArea();

        panel.add(bookNameLabel);
        panel.add(bookNameField);
        panel.add(categoryLabel);
        panel.add(categoryField);
        panel.add(stateLabel);
        panel.add(stateField);
        panel.add(chapterLabel);
        panel.add(new JScrollPane(chapterArea));

        JButton uploadBtn = new JButton("上传");
        uploadBtn.addActionListener(new UploadActionListener());

        add(panel, BorderLayout.CENTER);
        add(uploadBtn, BorderLayout.SOUTH);
    }

    private class UploadActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String bookName = bookNameField.getText();
            String category = categoryField.getText();
            String state = stateField.getText();
            String[] chapters = chapterArea.getText().split("\n");

            if (bookName.isEmpty() || category.isEmpty() || state.isEmpty() || chapters.length == 0) {
                JOptionPane.showMessageDialog(BookUploadDialog.this, "请填写完整信息");
                return;
            }

            try (Connection conn = DBUtil.getConnection()) {
                // 插入书籍信息
                String bookSql = "INSERT INTO books (b_name, b_category, b_state, w_id) VALUES (?, ?, ?, ?)";
                try (PreparedStatement bookStmt = conn.prepareStatement(bookSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    bookStmt.setString(1, bookName);
                    bookStmt.setString(2, category);
                    bookStmt.setString(3, state);
                    bookStmt.setInt(4, writerId);
                    bookStmt.executeUpdate();

                    int bookId;
                    try (java.sql.ResultSet generatedKeys = bookStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            bookId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("创建书籍失败，未获取到书籍 ID");
                        }
                    }

                    // 插入章节信息
                    String chapterSql = "INSERT INTO chapter (cp_name, b_id) VALUES (?, ?)";
                    try (PreparedStatement chapterStmt = conn.prepareStatement(chapterSql)) {
                        for (String chapter : chapters) {
                            chapterStmt.setString(1, chapter);
                            chapterStmt.setInt(2, bookId);
                            chapterStmt.executeUpdate();
                        }
                    }

                    JOptionPane.showMessageDialog(BookUploadDialog.this, "上传成功");
                    dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(BookUploadDialog.this, "数据库错误: " + ex.getMessage());
            }
        }
    }
}
