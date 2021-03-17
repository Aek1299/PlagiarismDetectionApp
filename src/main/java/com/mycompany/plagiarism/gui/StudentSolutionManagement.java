package com.mycompany.plagiarism.gui;


import com.mycompany.plagiarism.service.Dispatcher;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

/**
 * Класс управления студенческими работами-решениями.
 * Добавление осуществляется путем выбора директории с именем, являющимся названием задания, в которой находятся
 * директории с названием групп, в которых в свою очередь находятся директории с фамилиями студентов, в которых
 * находятся решения студентов.
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class StudentSolutionManagement extends JFrame {

    /**
     * Конструктор - создание и отображение окна для управления студенческими работами-решенями.
     * @param properties свойства, заданные пользователем.
     */

    public StudentSolutionManagement(Properties properties){
        super("Добавление работ");
        setBounds(0, 0, 450, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel labelListSelection = new JLabel("Выберите каталог с работами");
        labelListSelection.setBounds(30,10,400,30);

        JLabel labelSelectedList = new JLabel("");
        labelSelectedList.setBounds(30,50,245,30);
        labelSelectedList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JButton buttonUserSelection = new JButton("Выбрать");
        buttonUserSelection.setBounds(310,50,90,30);
        buttonUserSelection.setFocusPainted(false);
        buttonUserSelection.addActionListener(e->{
            JFileChooser chooserDatabase = new JFileChooser(properties.getProperty("WorkDirectoryURL"));
            chooserDatabase.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "";
                }
            });
            chooserDatabase.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int res = chooserDatabase.showDialog(null, "Выберите директорию");
            if(res!=JFileChooser.APPROVE_OPTION){
                labelSelectedList.setText("");
            }

            labelSelectedList.setText(chooserDatabase.getSelectedFile().getAbsolutePath());
        });

        JButton buttonBack = new JButton("Назад");
        buttonBack.setBounds(30,115,110,30);
        buttonBack.setFocusPainted(false);
        buttonBack.addActionListener(e->{
            dispose();
            new DatabaseManagement(properties);
        });

        JButton buttonReset = new JButton("Сбросить");
        buttonReset.setBounds(165,115,110,30);
        buttonReset.setFocusPainted(false);
        buttonReset.addActionListener(e -> {
        });

        JButton buttonAdd = new JButton("Добавить");
        buttonAdd.setBounds(300,115,110,30);
        buttonAdd.setFocusPainted(false);
        buttonAdd.addActionListener(e -> {
            if(!labelSelectedList.getText().equals("")) {
                File pack = new File(labelSelectedList.getText());
                String task = pack.getName();
                File[] groupPacks = pack.listFiles();
                if (groupPacks != null) {
                    for (File groupPack : groupPacks) {
                        String group = groupPack.getName();
                        for(File studentDir: Objects.requireNonNull(groupPack.listFiles())){
                            String studentName = studentDir.getName();
                            StringBuilder solution = new StringBuilder();
                            for(File studentFile : Objects.requireNonNull(studentDir.listFiles())){
                                try {
                                    Files.lines(Paths.get(studentFile.toURI()), StandardCharsets.UTF_8).forEach(
                                            solution::append);
                                } catch (UncheckedIOException exception) {
                                    try {
                                        Files.lines(Paths.get(studentFile.toURI()), StandardCharsets.ISO_8859_1).forEach(
                                                solution::append);
                                    } catch (UncheckedIOException ex) {
                                        JOptionPane.showMessageDialog(null,"Ошибка: " +
                                                        "неподдерживаемая кодировка", "Уведомление об ошибке",
                                                JOptionPane.ERROR_MESSAGE);
                                    } catch (IOException ioException) {
                                        JOptionPane.showMessageDialog(null,"Ошибка чтения файла",
                                                "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                                        ioException.printStackTrace();
                                    }
                                } catch (IOException ioException) {
                                    JOptionPane.showMessageDialog(null,"Ошибка чтения файла",
                                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                                    ioException.printStackTrace();
                                }
                            }
                            try {
                                new Dispatcher().addSolution(task, studentName, group, solution.toString());
                            } catch (SQLException throwables) {
                                JOptionPane.showMessageDialog(null,"Ошибка добавления решения",
                                        "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                                throwables.printStackTrace();
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(this,"Добавление завершено успешно!", "Сообщение",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        add(labelListSelection);
        add(labelSelectedList);
        add(buttonUserSelection);
        add(buttonBack);
        add(buttonReset);
        add(buttonAdd);
        setVisible(true);
    }
}
