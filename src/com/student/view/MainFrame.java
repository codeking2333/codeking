package com.student.view;

import com.student.util.Constant;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {

    public MainFrame() {
        this.getContentPane().setLayout(new BorderLayout());
        initMenus();

        this.setSize(600, 500);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void initMenus() {
        JMenuBar mainMenu = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        JMenuItem changeClassMenuItem = new JMenuItem("切换当前班");
        JMenuItem exportScoreMenuItem = new JMenuItem("导出当前班成绩");
        JMenuItem exitMenuItem = new JMenuItem("退出");

        JMenu classMenu = new JMenu("班级管理");
        JMenuItem addClassMenuItem = new JMenuItem("新增班级");
        JMenuItem classListMenuItem = new JMenuItem("班级列表");

        JMenu groupMenu = new JMenu("小组管理");
        JMenuItem addGroupMenuItem = new JMenuItem("新增小组");
        JMenuItem groupListMenuItem = new JMenuItem("小组列表");

        JMenu studentMenu = new JMenu("学生管理");
        JMenuItem addStudentMenuItem = new JMenuItem("新增学生");
        JMenuItem studentListMenuItem = new JMenuItem("学生列表");

        JMenu onClassMenu = new JMenu("课堂管理");
        JMenuItem randomGroupMenuItem = new JMenuItem("随机小组");
        JMenuItem randomStudentMenuItem = new JMenuItem("随机学生");

        this.getContentPane().add(mainMenu, BorderLayout.NORTH);
        mainMenu.add(fileMenu);
        mainMenu.add(classMenu);
        mainMenu.add(groupMenu);
        mainMenu.add(studentMenu);
        mainMenu.add(onClassMenu);
        fileMenu.add(changeClassMenuItem);
        fileMenu.add(exportScoreMenuItem);
        fileMenu.add(exitMenuItem);
        classMenu.add(addClassMenuItem);
        classMenu.add(classListMenuItem);
        groupMenu.add(addGroupMenuItem);
        groupMenu.add(groupListMenuItem);
        studentMenu.add(addStudentMenuItem);
        studentMenu.add(studentListMenuItem);
        onClassMenu.add(randomGroupMenuItem);
        onClassMenu.add(randomStudentMenuItem);
        // 添加菜单事件
        // 切换班级
        changeClassMenuItem.addActionListener(e -> {
            this.getContentPane().removeAll();
            initMenus();
            ChangeClassPanel changeClassPanel = new ChangeClassPanel(this, "ChangeClass");
            this.getContentPane().add(changeClassPanel, BorderLayout.CENTER);
            this.getContentPane().validate();
        });
        // 导出成绩
        exportScoreMenuItem.addActionListener(e -> {
            if (Constant.CLASS_PATH.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请先选择班级", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                // 创建预览数据
                DefaultTableModel previewModel = new DefaultTableModel(
                    new String[]{"学号", "姓名", "小组", "成绩"}, 0);

                // 读取学生数据和成绩数据
                Map<String, String> studentScores = new HashMap<>();
                File scoreFile = new File("D:" + File.separator + "workspacemax" 
                    + File.separator + "java" 
                    + File.separator + "student" 
                    + File.separator + "student_score.txt");

                if (scoreFile.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split(",");
                            if (parts.length >= 3 && parts[0].equals(Constant.CLASS_PATH)) {
                                studentScores.put(parts[1], parts[2]);  // 使用学生姓名作为key
                            }
                        }
                    }
                }

                // 读取学生数据
                File studentFile = new File("D:" + File.separator + "workspacemax" 
                    + File.separator + "java" 
                    + File.separator + "student" 
                    + File.separator + "student.txt");

                if (studentFile.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(studentFile))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split(",");
                            if (parts.length >= 4 && parts[0].equals(Constant.CLASS_PATH)) {
                                String score = studentScores.getOrDefault(parts[2], "0");  // 使用学生姓名获取成绩
                                previewModel.addRow(new String[]{
                                    parts[1],  // 学号
                                    parts[2],  // 姓名
                                    parts[3],  // 小组
                                    score     // 成绩
                                });
                            }
                        }
                    }
                }

                // 如果没有数据，显示提示
                if (previewModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "当前班级没有学生数据", "", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // 创建预览窗口
                JDialog previewDialog = new JDialog(this, Constant.CLASS_PATH + " - 成绩预览", true);
                previewDialog.setLayout(new BorderLayout());
                
                // 创建表格
                JTable previewTable = new JTable(previewModel);
                previewTable.setEnabled(false);  // 设置表格不可编辑
                
                // 添加滚动面板
                JScrollPane scrollPane = new JScrollPane(previewTable);
                previewDialog.add(scrollPane, BorderLayout.CENTER);
                
                // 添加导出按钮
                JPanel buttonPanel = new JPanel();
                JButton exportButton = new JButton("导出到文件");
                exportButton.addActionListener(event -> {
                    try {
                        // 创建导出文件
                        File exportFile = new File("D:" + File.separator + "workspacemax" 
                            + File.separator + "java" 
                            + File.separator + "student" 
                            + File.separator + "export" 
                            + File.separator + Constant.CLASS_PATH + "_成绩.csv");

                        // 确保导出目录存在
                        if (!exportFile.getParentFile().exists()) {
                            exportFile.getParentFile().mkdirs();
                        }

                        // 写入导出文件
                        try (PrintWriter writer = new PrintWriter(new FileWriter(exportFile))) {
                            // 写入表头
                            writer.println("学号,姓名,小组,成绩");
                            
                            // 写入数据
                            for (int i = 0; i < previewModel.getRowCount(); i++) {
                                writer.println(
                                    previewModel.getValueAt(i, 0) + "," +
                                    previewModel.getValueAt(i, 1) + "," +
                                    previewModel.getValueAt(i, 2) + "," +
                                    previewModel.getValueAt(i, 3)
                                );
                            }
                            
                            JOptionPane.showMessageDialog(previewDialog, 
                                "成绩已导出到：" + exportFile.getAbsolutePath(), 
                                "导出成功", 
                                JOptionPane.INFORMATION_MESSAGE);
                            previewDialog.dispose();
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(previewDialog, 
                            "导出成绩失败：" + ex.getMessage(), 
                            "错误", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
                buttonPanel.add(exportButton);
                previewDialog.add(buttonPanel, BorderLayout.SOUTH);
                
                // 设置窗口大小和位置
                previewDialog.setSize(500, 400);
                previewDialog.setLocationRelativeTo(this);
                previewDialog.setVisible(true);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "导出成绩失败：" + ex.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        // 退出程序
        exitMenuItem.addActionListener(e -> System.exit(0));
        // 新增班级
        addClassMenuItem.addActionListener(e -> {
            this.getContentPane().removeAll();
            initMenus();
            ClassAddPanel classAddPanel = new ClassAddPanel();
            this.getContentPane().add(classAddPanel, BorderLayout.CENTER);
            this.getContentPane().validate();
        });
        // 班级列表
        classListMenuItem.addActionListener(e -> {
            this.getContentPane().removeAll();
            initMenus();
            ClassListPanel classListPanel = new ClassListPanel();
            this.getContentPane().add(classListPanel, BorderLayout.CENTER);
            this.getContentPane().validate();
        });
        // 新增小组
        addGroupMenuItem.addActionListener(e -> {
            if (Constant.CLASS_PATH == null || Constant.CLASS_PATH.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请先在【文件】菜单中选择班级", "", JOptionPane.WARNING_MESSAGE);
                this.getContentPane().removeAll();
                initMenus();
                ChangeClassPanel changeClassPanel = new ChangeClassPanel(this, "GroupAdd");
                this.getContentPane().add(changeClassPanel, BorderLayout.CENTER);
                this.getContentPane().validate();
            } else {
                this.getContentPane().removeAll();
                initMenus();
                this.getContentPane().add(new GroupAddPanel(), BorderLayout.CENTER);
                this.getContentPane().repaint();
                this.getContentPane().validate();
            }
        });
        // 小组列表
        groupListMenuItem.addActionListener(e -> {
            this.getContentPane().removeAll();
            initMenus();
            this.getContentPane().add(new GroupListPanel(), BorderLayout.CENTER);
            this.getContentPane().repaint();
            this.getContentPane().validate();
        });
        // 新增学生
        addStudentMenuItem.addActionListener(e -> {
            if (Constant.CLASS_PATH == null || Constant.CLASS_PATH.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请先在【文件】菜单中选择班级", "", JOptionPane.WARNING_MESSAGE);
                this.getContentPane().removeAll();
                initMenus();
                ChangeClassPanel changeClassPanel = new ChangeClassPanel(this, "StudentAdd");
                this.getContentPane().add(changeClassPanel, BorderLayout.CENTER);
                this.getContentPane().validate();
            } else {
                this.getContentPane().removeAll();
                initMenus();
                this.getContentPane().add(new StudentAddPanel(), BorderLayout.CENTER);
                this.getContentPane().repaint();
                this.getContentPane().validate();
            }
        });
        // 学生列表
        studentListMenuItem.addActionListener(e -> {
            this.getContentPane().removeAll();
            initMenus();
            this.getContentPane().add(new StudentListPanel(), BorderLayout.CENTER);
            this.getContentPane().repaint();
            this.getContentPane().validate();
        });
        // 随机抽取小组
        randomGroupMenuItem.addActionListener(e -> {
            this.getContentPane().removeAll();
            initMenus();
            this.getContentPane().add(new RandomGroupPanel(), BorderLayout.CENTER);
            this.getContentPane().repaint();
            this.getContentPane().validate();
        });
        // 随机抽取学生
        randomStudentMenuItem.addActionListener(e -> {
            this.getContentPane().removeAll();
            initMenus();
            this.getContentPane().add(new RandomStudentPanel(), BorderLayout.CENTER);
            this.getContentPane().repaint();
            this.getContentPane().validate();
        });
    }

    public void refreshClassList() {
        // 移除旧的班级列表面板
        for (Component component : this.getContentPane().getComponents()) {
            if (component instanceof ClassListPanel) {
                this.remove(component);
            }
        }
        // 添加新的班级列表面板
        ClassListPanel classListPanel = new ClassListPanel();
        this.add(classListPanel);
        this.revalidate();
        this.repaint();
    }

    public void refreshGroupList() {
        // 移除旧的小组列表面板
        for (Component component : this.getContentPane().getComponents()) {
            if (component instanceof GroupListPanel) {
                this.remove(component);
            }
        }
        // 添加新的小组列表面板
        GroupListPanel groupListPanel = new GroupListPanel();
        this.add(groupListPanel);
        this.revalidate();
        this.repaint();
    }
}
