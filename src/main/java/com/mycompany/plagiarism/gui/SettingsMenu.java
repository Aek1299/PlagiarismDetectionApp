package com.mycompany.plagiarism.gui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Класс настроек.
 * Позволяет выбирать следующие настройки:
 * - DatabasesDirectoryURL - директория, в которой сохраняются базы данных;
 * - ResultsDirectoryURL - директория, в которой сохраняются результаты;
 * - MiddleThreshold - процентное значение схожести программ, после которого программы будут считаться программами
 * средней схожести;
 * - HighThreshold - процентное значение схожести программ, после которого программы будут считаться программами
 * высокой схожести (плагиат);
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class SettingsMenu extends JFrame {

    /**
     * Конструктор - создание и отображение графического окна для выбора настроек.
     */

    public SettingsMenu(Properties properties){
        super("Настройки");
        setBounds(0, 0, 450, 510);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel labelNameCurURI = new JLabel("Директория для хранения баз данных:");
        labelNameCurURI.setBounds(10,10,250,30);


        JLabel labelCurURI = new JLabel(properties.getProperty("DatabasesDirectoryURL"));
        labelCurURI.setBounds(10,50,350-25-25,30);
        labelCurURI.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));



        JButton newURIButton = new JButton("Выбрать");
        newURIButton.setBounds(330,50,95,30);
        newURIButton.setFocusPainted(false);
        newURIButton.addActionListener(e->{
            String newUri = getNewURI("Выберите директорию для хранения баз данных");
            if(!newUri.equals("")) labelCurURI.setText(newUri);
        });
        JLabel labelResultsURIName = new JLabel("Директория для сохранения результатов:");
        labelResultsURIName.setBounds(10,90,290,30);


        JLabel labelResultsCur = new JLabel(properties.getProperty("ResultsDirectoryURL"));
        labelResultsCur.setBounds(10,130,350-50,30);
        labelResultsCur.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JButton newURIButton2 = new JButton("Выбрать");
        newURIButton2.setBounds(330,130,95,30);
        newURIButton2.setFocusPainted(false);
        newURIButton2.addActionListener(e->{
            String newUri = getNewURI("Выберите директорию для сохранения результатов");
            if(!newUri.equals("")) labelResultsCur.setText(newUri);
        });


        JLabel labelWorkURIName = new JLabel("Рабочая директория:");
        labelWorkURIName.setBounds(10,170,290,30);


        JLabel labelWorkCur = new JLabel(properties.getProperty("WorkDirectoryURL"));
        labelWorkCur.setBounds(10,210,350-50,30);
        labelWorkCur.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JButton newURIButton3 = new JButton("Выбрать");
        newURIButton3.setBounds(330,210,95,30);
        newURIButton3.setFocusPainted(false);
        newURIButton3.addActionListener(e->{
            String newUri = getNewURI("Выберите рабочую директорию");
            if(!newUri.equals("")) labelWorkCur.setText(newUri);
        });


        JLabel labelMiddleName = new JLabel("Выберите порог средней схожести программ:");
        labelMiddleName.setBounds(10,250,290,30);
        JSlider middleSlider = new JSlider(0,Integer.parseInt(properties.getProperty("HighThreshold")),
                Integer.parseInt(properties.getProperty("MiddleThreshold")));
        middleSlider.setMajorTickSpacing(50);
        middleSlider.setMinorTickSpacing(10);
        middleSlider.setPaintTicks(true);
        middleSlider.setPaintLabels(true);
        middleSlider.setBounds(10, 290, 300, 50);

        JLabel labelMiddleCurName = new JLabel("Порог:", SwingConstants.CENTER);
        labelMiddleCurName.setBounds(330,250,95,30);

        JLabel labelMiddleCurVal = new JLabel(properties.getProperty("MiddleThreshold")+"%", SwingConstants.CENTER);
        labelMiddleCurVal.setBounds(330,290,95,30);
        labelMiddleCurVal.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        middleSlider.addChangeListener(e -> labelMiddleCurVal.setText(middleSlider.getValue()+"%"));


        JLabel labelHighName = new JLabel("Выберите порог высокой схожести программ:");
        labelHighName.setBounds(10,330,290,30);

        JSlider highSlider = new JSlider(0,100, Integer.parseInt(properties.getProperty("HighThreshold")));
        highSlider.setMajorTickSpacing(50);
        highSlider.setMinorTickSpacing(10);
        highSlider.setPaintTicks(true);
        highSlider.setPaintLabels(true);
        highSlider.setBounds(10, 370, 300, 50);

        JLabel labelHighCurName = new JLabel("Порог:", SwingConstants.CENTER);
        labelHighCurName.setBounds(330,330,95,30);

        JLabel labelHighCurVal = new JLabel(properties.getProperty("HighThreshold")+"%", SwingConstants.CENTER);
        labelHighCurVal.setBounds(330,370,95,30);
        labelHighCurVal.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        highSlider.addChangeListener(e ->{
            labelHighCurVal.setText(highSlider.getValue()+"%");
            middleSlider.setMaximum(highSlider.getValue());
        });


        JButton extB = new JButton("Назад");
        extB.setBounds(35,430,180,30);
        extB.setFocusPainted(false);
        extB.addActionListener(e ->{
            this.dispose();
            new MainMenu();
        });

        JButton appB = new JButton("Применить");
        appB.setBounds(extB.getX()+extB.getWidth()+20, 430, extB.getWidth(), extB.getHeight());

        appB.setFocusPainted(false);
        appB.addActionListener(e->{
            properties.replace("DatabasesDirectoryURL", labelCurURI.getText());
            properties.replace("ResultsDirectoryURL", labelResultsCur.getText());
            properties.replace("WorkDirectoryURL", labelWorkCur.getText());
            properties.replace("MiddleThreshold", Integer.toString(middleSlider.getValue()));
            properties.replace("HighThreshold", Integer.toString(highSlider.getValue()));
            try (FileOutputStream fos = new FileOutputStream("config.properties");
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)){
                properties.store(osw,null);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(null,"Ошибка сохранения config.properties",
                        "Уведомление об ошибке", JOptionPane.ERROR_MESSAGE);
                ioException.printStackTrace();
                System.exit(1);
            }
        });



        add(labelNameCurURI);
        add(labelCurURI);
        add(labelResultsURIName);
        add(labelResultsCur);
        add(newURIButton);
        add(appB);
        add(newURIButton2);
        add(extB);
        add(labelWorkURIName);
        add(labelWorkCur);
        add(newURIButton3);
        add(middleSlider);
        add(labelMiddleName);
        add(labelHighName);
        add(highSlider);
        add(labelMiddleCurName);
        add(labelMiddleCurVal);
        add(labelHighCurName);
        add(labelHighCurVal);
        setVisible(true);
    }


    /**
     * Метод для выбора директории.
     * @param text заголовок окна.
     * @return путь к выбранной директории.
     */

    private String getNewURI(String text){
        JFileChooser fch = new JFileChooser();
        fch.setDialogTitle(text);
        setUpdateUI(fch);
        fch.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "";
            }
        });
        fch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int res = fch.showDialog(null, "Выбрать директорию");
        if(res!=JFileChooser.APPROVE_OPTION){
            return "";
        }
        return(fch.getSelectedFile().getAbsolutePath());
    }


    /**
     * Метод для локализауии JFileChooser.
     * @param choose JFileChooser, который необходимо локализировать.
     */

    public static void setUpdateUI(JFileChooser choose) {
        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.lookInLabelText", "Смотреть в");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файла");

        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить");
        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.openButtonToolTipText", "Открыть");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.cancelButtonToolTipText", "Отмена");

        UIManager.put("FileChooser.lookInLabelText", "Папка");
        UIManager.put("FileChooser.saveInLabelText", "Папка");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов");

        UIManager.put("FileChooser.upFolderToolTipText", "На один уровень вверх");
        UIManager.put("FileChooser.newFolderToolTipText", "Создание новой папки");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Список");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Таблица");
        UIManager.put("FileChooser.fileNameHeaderText", "Имя");
        UIManager.put("FileChooser.fileSizeHeaderText", "Размер");
        UIManager.put("FileChooser.fileTypeHeaderText", "Тип");
        UIManager.put("FileChooser.fileDateHeaderText", "Изменен");
        UIManager.put("FileChooser.fileAttrHeaderText", "Атрибуты");

        UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");
        choose.updateUI();
    }
}
