package com.mycompany.plagiarism.gui;

import com.mycompany.plagiarism.service.Dispatcher;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Класс управления заданиями: добавление и удаление заданий.
 * Возможна обработка как списка заданий, так и одиночной записи.
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class TasksManagement extends JFrame {
    private boolean isList;

    /**
     * Конструктор - создание и отображение окна для управления списком заданий.
     * @param properties свойства, заданные пользователем.
     */

    public TasksManagement(Properties properties){
        super("Управление списком заданий");
        Dispatcher dispatcher = new Dispatcher();
        setBounds(0, 0, 450, 295);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel labelListSelection = new JLabel("Выберите список заданий");
        labelListSelection.setBounds(30,10,400,30);

        JLabel labelSelectedList = new JLabel("");
        labelSelectedList.setBounds(30,50,245,30);
        labelSelectedList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JLabel labelTaskSelection = new JLabel("или введите название задания");
        labelTaskSelection.setBounds(30,90,400,30);


        JTextField fieldTaskSelection = new JTextField();
        fieldTaskSelection.setBounds(30,130, 245, 30);

        JLabel labelLanguage = new JLabel("Язык");
        labelLanguage.setBounds(300,130,50, 30);

        JComboBox<String> languages = new JComboBox<>(new String[]{"Java", "C++"});

        languages.setBounds(360 , 130, 50, 30);
        languages.setSelectedIndex(0);



        JButton buttonUserSelection = new JButton("Выбрать");
        buttonUserSelection.setBounds(310,50,90,30);
        buttonUserSelection.setFocusPainted(false);
        buttonUserSelection.addActionListener(e->{
            JFileChooser chooserDatabase = new JFileChooser(properties.getProperty("WorkDirectoryURL"));

            chooserDatabase.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int res = chooserDatabase.showDialog(null, "Выберите список");
            if(res!=JFileChooser.APPROVE_OPTION){
                labelSelectedList.setText("");
            }
            else{
                labelSelectedList.setText(chooserDatabase.getSelectedFile().getAbsolutePath());
                fieldTaskSelection.setText("");
                fieldTaskSelection.setEnabled(false);
                languages.setEnabled(false);
                isList = true;
            }

        });

        JButton buttonBack = new JButton("Назад");
        buttonBack.setBounds(30,195,110,30);
        buttonBack.setFocusPainted(false);
        buttonBack.addActionListener(e->{
            dispose();
            new DatabaseManagement(properties);
        });

        JButton buttonReset = new JButton("Сбросить");
        buttonReset.setBounds(165,195,110,30);
        buttonReset.setFocusPainted(false);
        buttonReset.addActionListener(e -> {
            labelLanguage.setText("");
            fieldTaskSelection.setText("");
            fieldTaskSelection.setEnabled(true);
            languages.setEnabled(true);
            isList = false;
        });

        JButton buttonAdd = new JButton("Добавить");
        buttonAdd.setBounds(300,175,110,30);
        buttonAdd.setFocusPainted(false);
        buttonAdd.addActionListener(e -> {
            if(isList&&!labelSelectedList.getText().equals("")){
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            new FileInputStream(labelSelectedList.getText()), StandardCharsets.UTF_8));
                    String str = reader.readLine();
                    String[] temp;
                    while(str!=null){
                        temp = str.split(" ");
                        for(int i = 0; i< Integer.parseInt(temp[1]);i++){
                            dispatcher.addTask(reader.readLine(), temp[0]);
                        }
                        str = reader.readLine();
                    }
                    JOptionPane.showMessageDialog(this,"Добавление завершено успешно!",
                            "Сообщение", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException fileNotFoundException) {
                    JOptionPane.showMessageDialog(null,"Ошибка чтения файла",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    fileNotFoundException.printStackTrace();
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,"Ошибка добавления задания в базу данных",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    throwables.printStackTrace();
                }


            }
            else if(!fieldTaskSelection.getText().equals("")){
                try {
                    dispatcher.addTask(fieldTaskSelection.getText(),(String)languages.getSelectedItem());
                    JOptionPane.showMessageDialog(this,"Добавление завершено успешно!", "Сообщение",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,"Ошибка добавления задания в базу данных",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    throwables.printStackTrace();
                }
            }

        });

        JButton buttonRemove = new JButton("Удалить");
        buttonRemove.setBounds(300,215,110,30);
        buttonRemove.setFocusPainted(false);

        buttonRemove.addActionListener(e -> {
            if(isList&&!labelSelectedList.getText().equals("")){
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(labelSelectedList.getText()));
                    String str = reader.readLine();
                    while(str!=null){
                        for(int i = 0; i< Integer.parseInt(str);i++){
                            dispatcher.removeTask(reader.readLine());
                        }
                        str = reader.readLine();

                    }
                    JOptionPane.showMessageDialog(this,"Удаление завершено успешно!", "Сообщение",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException fileNotFoundException) {
                    JOptionPane.showMessageDialog(null,"Ошибка чтения файла",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    fileNotFoundException.printStackTrace();
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,"Ошибка удаления задания из базы данных",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    throwables.printStackTrace();
                }
            }
            else if(!fieldTaskSelection.getText().equals("")){
                try {
                    dispatcher.removeTask(fieldTaskSelection.getText());
                    JOptionPane.showMessageDialog(this,"Удаление завершено успешно!", "Сообщение",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,"Ошибка удаления задания из базы данных",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    throwables.printStackTrace();
                }
            }
        });



        add(labelListSelection);
        add(labelSelectedList);
        add(fieldTaskSelection);
        add(buttonUserSelection);
        add(labelTaskSelection);
        add(labelLanguage);
        add(languages);

        add(buttonBack);
        add(buttonReset);
        add(buttonAdd);
        add(buttonRemove);

        setVisible(true);
    }

}

