package com.mycompany.plagiarism.gui;

import com.mycompany.plagiarism.dao.DatabaseUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Класс управления списком студентов: добавление и удаление студентов.
 * Возможна обработка как списка студентов, так и одиночной записи.
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class StudentsManagement extends JFrame {
    private boolean isList;

    /**
     * Конструктор - созданиие и отображение графического окна для управления списком студентов.
     * @param databaseUtils объект для взаимодействия с выбранной базой данных.
     * @param properties свойства, заданные пользователем.
     */

    public StudentsManagement(DatabaseUtils databaseUtils, Properties properties){
        super("Управление списком студентов");
        setBounds(0, 0, 450, 295);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel labelListSelection = new JLabel("Выберите список студентов");
        labelListSelection.setBounds(30,10,400,30);

        JLabel labelSelectedList = new JLabel("");
        labelSelectedList.setBounds(30,50,245,30);
        labelSelectedList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JLabel labelStudentSelection = new JLabel("или введите данные студента");
        labelStudentSelection.setBounds(30,90,400,30);

        JLabel labelFullName = new JLabel("ФИО");
        labelFullName.setBounds(30,130,40, 30);

        JTextField fieldStudentSelection = new JTextField();
        fieldStudentSelection.setBounds(80,130, 195, 30);

        JLabel labelGroupNumber = new JLabel("Группа");
        labelGroupNumber.setBounds(300,130,50, 30);

        JTextField fieldGroupSelection = new JTextField();
        fieldGroupSelection.setBounds(360,130, 50, 30);

        JButton buttonUserSelection = new JButton("Выбрать");
        buttonUserSelection.setBounds(310,50,90,30);
        buttonUserSelection.setFocusPainted(false);
        buttonUserSelection.addActionListener(e->{
            JFileChooser chooserDatabase = new JFileChooser(properties.getProperty("WorkDirectoryURL"));
            chooserDatabase.setFileSelectionMode(JFileChooser.FILES_ONLY);
//            chooserDatabase.setFileFilter(new FileFilter() {
//                @Override
//                public boolean accept(File f) {
//                    return f.getName().endsWith(".txt");
//                }
//
//                @Override
//                public String getDescription() {
//                    return "Текстовые файлы (*.txt)";
//                }
//            });

            int res = chooserDatabase.showDialog(null, "Выберите список");
            if(res!=JFileChooser.APPROVE_OPTION){
                labelSelectedList.setText("");
            }
            else {
                labelSelectedList.setText(chooserDatabase.getSelectedFile().getAbsolutePath());
                fieldStudentSelection.setText("");
                fieldStudentSelection.setEnabled(false);
                fieldGroupSelection.setText("");
                fieldGroupSelection.setEnabled(false);
                isList = true;
            }
        });

        JButton buttonBack = new JButton("Назад");
        buttonBack.setBounds(30,195,110,30);
        buttonBack.setFocusPainted(false);
        buttonBack.addActionListener(e->{
            dispose();
            new DatabaseManagement(databaseUtils, properties);
        });

        JButton buttonReset = new JButton("Сбросить");
        buttonReset.setBounds(165,195,110,30);
        buttonReset.setFocusPainted(false);
        buttonReset.addActionListener(e -> {
            labelGroupNumber.setText("");
            fieldStudentSelection.setText("");
            fieldStudentSelection.setEnabled(true);
            fieldGroupSelection.setText("");
            fieldGroupSelection.setEnabled(true);
            isList = false;
        });

        JButton buttonAdd = new JButton("Добавить");
        buttonAdd.setBounds(300,175,110,30);
        buttonAdd.setFocusPainted(false);
        buttonAdd.addActionListener(e -> {
            if(isList&&!labelSelectedList.getText().equals("")){
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader
                            (new FileInputStream(labelSelectedList.getText()), StandardCharsets.UTF_8));
                    String str = reader.readLine();
                    String[] temp;
                    while(str!=null){
                        temp = str.split(" ");
                        for(int i = 0; i< Integer.parseInt(temp[1]);i++){
                            databaseUtils.addStudent(reader.readLine(), temp[0]);
                        }
                        str = reader.readLine();
                    }
                    JOptionPane.showMessageDialog(this,"Добавление завершено успешно!", "Сообщение",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException fileNotFoundException) {
                    JOptionPane.showMessageDialog(null,"Ошибка чтения файла",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    fileNotFoundException.printStackTrace();
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,"Ошибка добавления студента в базу данных",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    throwables.printStackTrace();
                }


            }
            else if(!fieldStudentSelection.getText().equals("")){
                try {
                    databaseUtils.addStudent(fieldStudentSelection.getText(),fieldGroupSelection.getText());
                    JOptionPane.showMessageDialog(this,"Добавление завершено успешно!", "Сообщение",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,"Ошибка удаления студента из базы данных",
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
                    String[] temp;
                    while(str!=null){
                        temp = str.split(" ");

                        for(int i = 0; i< Integer.parseInt(temp[1]);i++){
                            databaseUtils.removeStudent(reader.readLine(), temp[0]);
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
                    JOptionPane.showMessageDialog(null,"Ошибка удаления студента из базы данных",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    throwables.printStackTrace();
                }
            }

            else if(!fieldStudentSelection.getText().equals("")){
                try {
                    databaseUtils.removeStudent(fieldStudentSelection.getText(),fieldGroupSelection.getText());
                    JOptionPane.showMessageDialog(this,"Удаление завершено успешно!", "Сообщение",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,"Ошибка удаления студента из базы данных",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    throwables.printStackTrace();
                }
            }

        });



        add(labelListSelection);
        add(labelSelectedList);
        add(labelFullName);
        add(fieldStudentSelection);
        add(buttonUserSelection);
        add(labelStudentSelection);
        add(labelGroupNumber);
        add(fieldGroupSelection);

        add(buttonBack);
        add(buttonReset);
        add(buttonAdd);
        add(buttonRemove);

        setVisible(true);
    }

}
