package com.mycompany.plagiarism.gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Objects;
import java.util.Properties;

/**
 * Класс главного меню.
 * Возможность:
 * -запуска программы;
 * -вызова окна с настройками.
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class MainMenu extends JFrame {

    /**
     * Конструктор - создание и отображение графического окна с главным меню.
     */

    public MainMenu(){

        super("Plagiarism Detection App v1.0");

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Ошибка загрузки config.properties", "Уведомление об ошибке",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }

        boolean flag = false;
        try{
            Objects.requireNonNull(properties.getProperty("DatabasesDirectoryURL"));
            Objects.requireNonNull(properties.getProperty("ResultsDirectoryURL"));
            Objects.requireNonNull(properties.getProperty("WorkDirectoryURL"));
            Objects.requireNonNull(properties.getProperty("MiddleThreshold"));
            Objects.requireNonNull(properties.getProperty("HighThreshold"));
        }catch(NullPointerException exception){
            JOptionPane.showMessageDialog(null,"Повреждён файл config.properties",
                    "Уведомление об ошибке",  JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        if(properties.getProperty("DatabasesDirectoryURL").equals("")){
            properties.replace("DatabasesDirectoryURL",System.getProperty("user.dir")+
                    System.getProperty("file.separator")+"databases");
            flag = true;
        }

        if(properties.getProperty("ResultsDirectoryURL").equals("")){
            properties.replace("ResultsDirectoryURL",System.getProperty("user.dir")+
                    System.getProperty("file.separator")+"results");
            flag = true;
        }

        if(properties.getProperty("WorkDirectoryURL").equals("")){
            properties.replace("WorkDirectoryURL",System.getProperty("user.dir"));
            flag = true;
        }

        if(properties.getProperty("MiddleThreshold").equals("")){
            properties.replace("MiddleThreshold", "50");
            flag = true;
        }

        if(properties.getProperty("HighThreshold").equals("")){
            properties.replace("HighThreshold", "75");
            flag = true;
        }

        if (flag) {
            try {
                properties.store(new FileWriter("config.properties"),null);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,"Ошибка сохранения config.properties",
                        "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(1);
            }
        }

        setBounds(0, 0, 450, 400);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lbl = new JLabel("Plagiarism Detection App v1.0", JLabel.CENTER);
        Font f = new Font(Font.MONOSPACED, Font.BOLD, 20);
        lbl.setFont(f);
        lbl.setBounds(50,0,350,80);
      //  lbl.setOpaque(true);
      //  lbl.setBackground(Color.lightGray);
        add(lbl) ;

        JButton btn1 = new JButton("Начать");
        btn1.setBounds(75, 100, 300, 40);
        btn1.addActionListener(e->{
            dispose();
            new DatabaseSelection(properties);
        });
        btn1.setFocusPainted(false);
        add(btn1);

//        JButton btn2 = new JButton("Продолжить работу с раннее созданной");
//        btn2.setBounds(75, 160, 300, 40);
//        btn2.addActionListener(e->{
//            System.exit(0);
//        });
//        add(btn2);


        JButton btn3 = new JButton("Настройки");
        btn3.setBounds(75, 160, 300, 40);
        btn3.addActionListener(e->{
            dispose();
            new SettingsMenu(properties);
        });
        add(btn3);


        JButton btn4 = new JButton("Выйти");
        btn4.setBounds(75, 220, 300, 40);
        btn4.addActionListener(e->{
            dispose();
            System.exit(0);
        });
        add(btn4);

        setVisible(true);
    }
}
