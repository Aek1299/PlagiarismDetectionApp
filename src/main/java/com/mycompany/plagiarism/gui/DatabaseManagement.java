package com.mycompany.plagiarism.gui;

import javax.swing.*;
import java.util.Properties;


/**
 * Класс управления выбранной базой данных.
 * Возможность выбрать следующие действия:
 * -изменение списка студентов;
 * -изменение списка заданий;
 * -добавление выполненных работ;
 * -получение результатов.
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class DatabaseManagement extends JFrame {

    /**
     * Конструктор - создание и отображение графического окна для управления выбранной базой данных.
     * @param properties свойства, заданные пользователем.
     */

    public DatabaseManagement(Properties properties){
        super("Управление БД");
        setBounds(0, 0, 450, 400);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);



        int indent = 40;
        JButton btn1 = new JButton("Изменить список студентов");
        btn1.setBounds(75, indent, 300, 40);
        btn1.addActionListener(e->{
            dispose();
            new StudentsManagement(properties);
        });
        btn1.setFocusPainted(false);
        add(btn1);

        JButton btn2 = new JButton("Изменить список заданий");
        btn2.setBounds(75, indent+60, 300, 40);
        btn2.addActionListener(e->{
            dispose();
            new TasksManagement(properties);
        });
        add(btn2);

        JButton btn4 = new JButton("Добавить выполненные работы");
        btn4.setBounds(75, indent+60*2, 300, 40);
        btn4.addActionListener(e->{
            dispose();
            new StudentSolutionManagement(properties);
        });
        add(btn4);

        JButton btn5 = new JButton("Результаты");
        btn5.setBounds(75, indent+60*3, 300, 40);
        btn5.addActionListener(e->{
            dispose();
            new GettingResults(properties);
        });
        add(btn5);

        JButton btn6 = new JButton("Выйти");
        btn6.setBounds(75, indent+60*4, 300, 40);
        btn6.addActionListener(e-> System.exit(0));
        add(btn6);


        setVisible(true);
    }
}
