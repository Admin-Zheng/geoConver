package org.lovey;

import com.alibaba.fastjson.JSON;
import org.lovey.core.ShpService;
import org.lovey.utils.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author l_y
 * @version 1.0.0
 * @ClassName Main.java
 * @Description TODO
 * @createTime 2022年01月18日 16:05:00
 */

public class Main {
    public static void main(String[] args) {
        // 创建 JFrame 实例
        JFrame frame = new JFrame("SHP Conver");
        // Setting the width and height of frame
        frame.setSize(600, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);
        /*
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);
        // 设置界面可见
        frame.setVisible(true);
    }

    public static void placeComponents(JPanel panel) {

        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);

        // 创建 JLabel
        JLabel userLabel = new JLabel("Shp文件夹路径:");
        userLabel.setBounds(100, 70, 120, 25);
        panel.add(userLabel);

        JLabel passwordLabel = new JLabel("原坐标系4326:");
        passwordLabel.setBounds(100, 140, 200, 25);
        panel.add(passwordLabel);

        JLabel passwordLabel1 = new JLabel("至坐标系4326:");
        passwordLabel1.setBounds(100, 180, 200, 25);
        panel.add(passwordLabel1);

        JTextField userText = new JTextField(20);
        userText.setBounds(200, 70, 215, 25);
        panel.add(userText);

        JTextField passwordText = new JTextField(20);
        passwordText.setBounds(200, 140, 215, 25);
        panel.add(passwordText);

        JTextField passwordText1 = new JTextField(20);
        passwordText1.setBounds(200, 180, 215, 25);
        panel.add(passwordText1);


        // 创建登录按钮
        JButton loginButton = new JButton("导出");
        loginButton.setBounds(220, 250, 120, 25);
        panel.add(loginButton);

        // 创建登录按钮
        JButton jsonButton = new JButton("校验JSON");
        jsonButton.setBounds(220, 280, 120, 25);
        panel.add(jsonButton);


        jsonButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CopyOnWriteArrayList<String> errPath = new CopyOnWriteArrayList<>();
                List<String> fileList = new ArrayList<>();
                List<String> dirInFile = FileUtils.getDirInFile(fileList, userText.getText(), ".geojson");
                dirInFile.forEach(file -> {
                    StringBuilder builder = new StringBuilder();
                    File jsonFile = new File(file);
                    FileReader fileReader = null;
                    try {
                        fileReader = new FileReader(jsonFile);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    char[] chars = new char[1024];
                    while (true) {
                        try {
                            if (!(-1 != fileReader.read(chars))) break;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        builder.append(Arrays.toString(chars));
                    }
                    try {
                        fileReader.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        Object parse = JSON.parse(builder.toString());
                    } catch (Exception ee) {
                        errPath.add(file);
                    }
                });
                File file = new File(userText.getText() + File.pathSeparator + "errJson.log");
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    errPath.forEach(err -> {
                        try {
                            fileWriter.write(err);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                    fileWriter.flush();
                    fileWriter.close();
                }catch (Exception eqq){
                    eqq.printStackTrace();
                }
                JOptionPane.showConfirmDialog(panel, "校验结束", "提示", 2, 1);
            }
        });


        loginButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> fileList = new ArrayList<>();
                List<String> dirInFile = FileUtils.getDirInFile(fileList, userText.getText(), ".shp");
                dirInFile.forEach(file -> {
                    try {
                        synchronized (this) {
                            new ShpService().converShpFileToGeoJson(passwordText.getText(), passwordText1.getText(), new File(file), file.substring(0, file.lastIndexOf(".shp")) + ".geojson");
                        }
                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                });
                JOptionPane.showConfirmDialog(panel, "导出完毕", "提示", 2, 1);
            }
        });

    }
}
