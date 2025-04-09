package src.main.java.com.novelplatform.ui.dialogs;

import src.main.java.com.novelplatform.util.DBUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class ChapterManagementDialog extends JDialog {
    private final int bookId;
    private final DefaultTableModel chapterTableModel = new DefaultTableModel(
            new Object[]{"章节ID", "章节名称"}, 0
    );
    private final JTable chapterTable = new JTable(chapterTableModel);

    public ChapterManagementDialog(JFrame parent, int bookId) {
        super(parent, "章节管理", true);
        this.bookId = bookId;
        initUI();
        loadChapters();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 章节列表
        chapterTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(chapterTable);

        // 操作按钮
        JPanel btnPanel = new JPanel();
        JButton editBtn = new JButton("修改章节名");
        JButton deleteBtn = new JButton("删除章节");

        editBtn.addActionListener(this::handleEditChapter);
        deleteBtn.addActionListener(this::handleDeleteChapter);

        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(getParent());
    }

    private void loadChapters() {
        chapterTableModel.setRowCount(0);
        String sql = "SELECT cp_id, cp_name FROM chapter WHERE b_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                chapterTableModel.addRow(new Object[]{
                        rs.getInt("cp_id"),
                        rs.getString("cp_name")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载章节失败: " + ex.getMessage());
        }
    }

    private void handleEditChapter(ActionEvent e) {
        int selectedRow = chapterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择一个章节进行修改。");
            return;
        }
        int chapterId = (int) chapterTableModel.getValueAt(selectedRow, 0);
        String newChapterName = JOptionPane.showInputDialog(this, "请输入新的章节名:");
        if (newChapterName != null && !newChapterName.isEmpty()) {
            String sql = "UPDATE chapter SET cp_name = ? WHERE cp_id = ?";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newChapterName);
                stmt.setInt(2, chapterId);
                if (stmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "章节名修改成功");
                    loadChapters();
                } else {
                    JOptionPane.showMessageDialog(this, "章节名修改失败");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "数据库错误: " + ex.getMessage());
            }
        }
    }

    private void handleDeleteChapter(ActionEvent e) {
        int selectedRow = chapterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择一个章节进行删除。");
            return;
        }
        int chapterId = (int) chapterTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除这个章节吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM chapter WHERE cp_id = ?";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, chapterId);
                if (stmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "章节删除成功");
                    loadChapters();
                } else {
                    JOptionPane.showMessageDialog(this, "章节删除失败");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "数据库错误: " + ex.getMessage());
            }
        }
    }
}