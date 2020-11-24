package com.mycompany.plagiarism.gui;

import com.mycompany.plagiarism.DatabaseUtils;
import com.mycompany.plagiarism.ExcelWriter;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

/**
 * Класс получения результатов.
 * Возможность вызвать:
 * -обработку данных;
 * -получение Excel отчёта.
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class GettingResults extends JFrame {

    /**
     * Конструктор - создание и отображение графического окна получения результатов.
     * @param databaseUtils  объект для взаимодействия с базой доанных;
     * @param properties  свойства, задаваемые пользователем.
     * @see com.mycompany.plagiarism.DatabaseUtils
     * @see SettingsMenu
     */

    public GettingResults(DatabaseUtils databaseUtils, Properties properties){
        super("Результаты");
        int middleThreshold = Integer.parseInt(properties.getProperty("MiddleThreshold"));
        int highThreshold = Integer.parseInt(properties.getProperty("HighThreshold"));
        String resultsURL = properties.getProperty("ResultsDirectoryURL");
        setBounds(0, 0, 450, 350);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        String[] variants = new String[1];
        HashMap<String,Integer> tasks = null;
        try {
            tasks = databaseUtils.getTasks();
            variants = new String[tasks.keySet().size()+1];
            int i = 1;
            for (String key:
                    tasks.keySet()) {
                variants[i] = key;
                i++;
            }

        } catch (SQLException throwables) {
            JOptionPane.showMessageDialog(null,"Ошибка при работе с базой данных",
                    "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
            throwables.printStackTrace();
            System.exit(1);
        }
        variants[0] = "Все";

        JComboBox<String> comboBox = new JComboBox<>(variants);
        int indent = 40;
        comboBox.setBounds(75 , indent, 300, 40);
        comboBox.setSelectedIndex(0);
        add(comboBox);




        JButton btn1 = new JButton("Обработать решения");
        btn1.setBounds(75, indent+60, 300, 40);

        String[] finalVariants = variants;
        HashMap<String, Integer> finalTasks = tasks;
        btn1.addActionListener(e->{

            // "включаем" GlassPane
            getGlassPane().setVisible(true);
            // "отключаем" компоненты
            doEnableDisable(this); // f - ссылка на JFrame/JDialog
            // запуск "процесса"
            Container cont = this;
            new SwingWorker() {
                @Override
                protected Object doInBackground() {
                    try {
                        if(comboBox.getSelectedIndex()==0) databaseUtils.startProcessing(-1, highThreshold);
                        else{
                            databaseUtils.startProcessing(finalTasks.get(finalVariants[comboBox.getSelectedIndex()]),
                                    highThreshold);
                        }

                    } catch (SQLException throwables) {
                        JOptionPane.showMessageDialog(null,"Ошибка при работе с базой данных",
                                "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                        throwables.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    // "отключаем" GlassPane
                    getGlassPane().setVisible(false);
                    // "включаем" компоненты
                    doEnableDisable(cont);
                    JOptionPane.showMessageDialog(cont,"Обработка завершена успешно!", "Сообщение",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }.execute();

        });
        btn1.setFocusPainted(false);
        add(btn1);

        JButton btn2 = new JButton("Excel отчёт");
        btn2.setBounds(75, indent+60*2, 300, 40);
        btn2.addActionListener(e->{
            try {
                if((Objects.requireNonNull(comboBox.getSelectedItem())).equals("Все")){
                    for (String key:
                         finalTasks.keySet()) {
                        ExcelWriter.write(databaseUtils, finalTasks.get(key),
                                key, middleThreshold, highThreshold, resultsURL);
                    }
                }
                else {
                    ExcelWriter.write(databaseUtils, finalTasks.get(comboBox.getSelectedItem()),
                            (String)comboBox.getSelectedItem(), middleThreshold, highThreshold, resultsURL);
                }
                JOptionPane.showMessageDialog(this, "Отчёт выполнен успешно!", "Сообщение",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException throwables) {
                JOptionPane.showMessageDialog(null,"Ошибка при работе с базой данных",
                        "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                throwables.printStackTrace();
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(null,"Ошибка при записи файла",
                        "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                ioException.printStackTrace();
            }


        });
        add(btn2);

//        JButton btn4 = new JButton("Получить граф");
//        btn4.setBounds(75, indent+60*3, 300, 40);
//        btn4.addActionListener(e->{
//
//        });
//        add(btn4);
//
//        JButton btn5 = new JButton("");
//        btn5.setBounds(75, indent+60*4, 300, 40);
//        btn5.addActionListener(e->{
//
//        });
//        add(btn5);

        JButton btn6 = new JButton("Назад");
        btn6.setBounds( 75, indent+60*3, 300, 40);

        //btn6.setBounds(75, indent+60*5, 300, 40);
        btn6.addActionListener(e->{
            dispose();
            new DatabaseManagement(databaseUtils, properties);
        });
        add(btn6);


        setVisible(true);
    }

    /**
     * Метод для отключения возможности взаимодействия с окном (его компонентами).
     * @param cont контейнер с графическими компонентами.
     */

    private void doEnableDisable(final Container cont) {

        for (Component c : cont.getComponents()) {
            c.setEnabled(!c.isEnabled());
            if (c instanceof Container) {
                doEnableDisable((Container) c);
            }
        }

    }
}