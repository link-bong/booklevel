package src.main.java.com.novelplatform.ui.dialogs;
import src.main.java.com.novelplatform.dao.BookDao;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import src.main.java.com.novelplatform.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookUploadDialog extends JDialog {
    private final int writerId;
    private JTextField bookNameField = new JTextField(20);
    private JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"小说", "文学", "武侠"});
    private JTextArea chapterArea = new JTextArea(10, 30);

    public BookUploadDialog(JFrame parent, int writerId) {
        super(parent, "上传新书", true);
        this.writerId = writerId;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 书籍基本信息
        JPanel infoPanel = new JPanel(new GridLayout(3, 2));
        infoPanel.add(new JLabel("书名:"));
        infoPanel.add(bookNameField);
        infoPanel.add(new JLabel("分类:"));
        infoPanel.add(categoryCombo);

        // 章节输入
        JPanel chapterPanel = new JPanel(new BorderLayout());
        chapterPanel.add(new JLabel("章节内容:"), BorderLayout.NORTH);
        chapterPanel.add(new JScrollPane(chapterArea), BorderLayout.CENTER);

        // 提交按钮
        JButton submitBtn = new JButton("发布");
        submitBtn.addActionListener(e -> uploadBook());

        add(infoPanel, BorderLayout.NORTH);
        add(chapterPanel, BorderLayout.CENTER);
        add(submitBtn, BorderLayout.SOUTH);
        pack();
    }

    private void uploadBook() {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // 开启事务

            // 插入书籍
            String bookSql = "INSERT INTO books (b_name, b_category, w_id) VALUES (?,?,?)";
            try (PreparedStatement bookStmt = conn.prepareStatement(bookSql, Statement.RETURN_GENERATED_KEYS)) {
                bookStmt.setString(1, bookNameField.getText());
                bookStmt.setString(2, (String) categoryCombo.getSelectedItem());
                bookStmt.setInt(3, writerId);
                bookStmt.executeUpdate();

                // 获取生成的书籍ID
                ResultSet rs = bookStmt.getGeneratedKeys();
                if (!rs.next()) throw new SQLException("创建书籍失败");
                int bookId = rs.getInt(1);

                // 插入章节
                String[] chapters = chapterArea.getText().split("\n");
                String chapterSql = "INSERT INTO chapter (cp_name, b_id) VALUES (?,?)";
                try (PreparedStatement chapStmt = conn.prepareStatement(chapterSql)) {
                    for (String chapName : chapters) {
                        if (chapName.trim().isEmpty()) continue;
                        chapStmt.setString(1, chapName.trim());
                        chapStmt.setInt(2, bookId);
                        chapStmt.addBatch();
                    }
                    chapStmt.executeBatch();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "上传成功");
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "上传失败: " + ex.getMessage());
        }
    }
}
