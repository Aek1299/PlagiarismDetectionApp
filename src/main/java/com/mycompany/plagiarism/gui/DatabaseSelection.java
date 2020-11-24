package com.mycompany.plagiarism.gui;

import com.mycompany.plagiarism.DatabaseUtils;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Класс выбора/создания базы данных
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class DatabaseSelection extends JFrame {

    /**
     * Конструктор - создание и отображение графического окна для выбора/создания базы данных.
     * @param properties свойства, заданные пользователем.
     */

    public DatabaseSelection(Properties properties){
        super("Выбор базы данных");
        String databasesDirectory = properties.getProperty("DatabasesDirectoryURL");
        setBounds(0, 0, 450, 280);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel labelSelectionDatabase = new JLabel("Выберите существующую базу данных");
        labelSelectionDatabase.setBounds(30,10,400,30);

        JLabel labelUserSelection = new JLabel("");
        labelUserSelection.setBounds(30,50,245,30);
        labelUserSelection.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JTextField fieldUserSelection = new JTextField();
        fieldUserSelection.setBounds(30,130, 245, 30);

        JButton buttonUserSelection = new JButton("Выбрать");
        buttonUserSelection.setBounds(310,50,90,30);
        buttonUserSelection.setFocusPainted(false);
        buttonUserSelection.addActionListener(e->{
            JFileChooser chooserDatabase = new JFileChooser(databasesDirectory);
            SettingsMenu.setUpdateUI(chooserDatabase);
            chooserDatabase.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooserDatabase.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(".mv.db");
                }

                @Override
                public String getDescription() {
                    return "H2 (*.mv.db)";
                }
            });

            chooserDatabase.setDialogTitle("Выберите базу данных");
            int res = chooserDatabase.showDialog(null, "Выбрать базу данных");
            if(res!=JFileChooser.APPROVE_OPTION){
                labelUserSelection.setText("");
            }
            else {
                labelUserSelection.setText(chooserDatabase.getSelectedFile().getName());
                fieldUserSelection.setText("");
                fieldUserSelection.setEnabled(false);
            }
        });
        JLabel labelSelectionDatabaseOr = new JLabel("или введите название для создания новой");
        labelSelectionDatabaseOr.setBounds(30,90,400,30);




        JButton buttonBack = new JButton("Назад");
        buttonBack.setBounds(30,180,110,30);
        buttonBack.setFocusPainted(false);
        buttonBack.addActionListener(e->{
            dispose();
            new MainMenu();
        });

        JButton buttonReset = new JButton("Сбросить");
        buttonReset.setBounds(165,180,110,30);
        buttonReset.setFocusPainted(false);
        buttonReset.addActionListener(e -> {
            labelUserSelection.setText("");
            fieldUserSelection.setText("");
            fieldUserSelection.setEnabled(true);
        });

        JButton buttonContinue = new JButton("Продолжить");
        buttonContinue.setBounds(300,180,110,30);
        buttonContinue.setFocusPainted(false);
        buttonContinue.addActionListener(e->{
            if(!labelUserSelection.getText().equals("")){
                DatabaseUtils databaseUtils = null;
                try {
                    databaseUtils = new DatabaseUtils(databasesDirectory+
                            System.getProperty("file.separator")+
                            labelUserSelection.getText().substring(0,labelUserSelection.getText().length()-6));
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,"Ошибка соединения с базой данных",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    throwables.printStackTrace();
                    System.exit(1);
                } catch (ClassNotFoundException classNotFoundException) {
                    JOptionPane.showMessageDialog(null,"Ошибка загрузки драйвера базы данных ",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    classNotFoundException.printStackTrace();
                    System.exit(1);
                }
                dispose();
                new DatabaseManagement(databaseUtils, properties);
            }

            else if(!fieldUserSelection.getText().equals("")){
                DatabaseUtils databaseUtils = null;

                try {
                    databaseUtils = new DatabaseUtils(databasesDirectory+
                            System.getProperty("file.separator")+fieldUserSelection.getText());
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,"Ошибка соединения с базой данных",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    throwables.printStackTrace();
                    System.exit(1);
                } catch (ClassNotFoundException classNotFoundException) {
                    JOptionPane.showMessageDialog(null,"Ошибка загрузки драйвера базы данных ",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    classNotFoundException.printStackTrace();
                    System.exit(1);
                }

                try {
                    databaseUtils.databaseInitialization();
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,"Ошибка инициализации базы данных",
                            "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                    throwables.printStackTrace();
                    System.exit(1);
                }
                dispose();
                new DatabaseManagement(databaseUtils, properties);
            }
        });


       add(labelSelectionDatabase);
       add(labelUserSelection);
       add(buttonUserSelection);
       add(labelSelectionDatabaseOr);
       add(fieldUserSelection);
       add(buttonBack);
       add(buttonReset);
       add(buttonContinue);
       setVisible(true);
    }

}
