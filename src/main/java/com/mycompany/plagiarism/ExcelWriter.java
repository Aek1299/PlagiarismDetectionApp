package com.mycompany.plagiarism;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Класс записи результатов в Excel таблицу.
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class ExcelWriter {


    /**
     * Метод для записи отчета в Excel таблицу.
     * @param databaseUtils объект для взаимодействия с базой доанныхю
     * @param task_id идентификатор задания.
     * @param task название задания.
     * @param middleThreshold порог средней схожести программ.
     * @param highThreshold порог высокой схожести программ.
     * @param resultsURL путь к директории для записи отчёта.
     * @throws SQLException исключение, возникающее при обработке запроса.
     * @throws IOException исключение, возникающее при записи файла.
     */

    public static void write(DatabaseUtils databaseUtils, int task_id, String task, int middleThreshold,
                             int highThreshold, String resultsURL) throws SQLException, IOException {
        ArrayList<String> thresholds = new ArrayList<>();

        thresholds.add("Различные");
        thresholds.add("Схожие");
        thresholds.add("Совпадение");
        thresholds.add("Порог");
        thresholds.add("0-"+middleThreshold);
        thresholds.add(middleThreshold+"-"+highThreshold);
        thresholds.add(highThreshold+"-100");
        ArrayList<String> groups = databaseUtils.getGroups();

        ArrayList<ArrayList<Record>> studentsRecords = databaseUtils.getRecords(groups,task_id);


        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Отчёт по "+task);


        CellStyle greenCellStyle = sheet.getWorkbook().createCellStyle();
        greenCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        greenCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle redCellStyle = sheet.getWorkbook().createCellStyle();
        redCellStyle.setFillForegroundColor(IndexedColors.CORAL.index);
        redCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle orangeCellStyle = sheet.getWorkbook().createCellStyle();
        orangeCellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        orangeCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle blackCellStyle = sheet.getWorkbook().createCellStyle();
        blackCellStyle.setFillForegroundColor(IndexedColors.BLACK.index);
        blackCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        int rowNum = 1;
        Row row = sheet.createRow(rowNum++);

        int cellNum = 1;

        for (String group : groups) {
            Cell cell = row.createCell(cellNum++);
            cell.setCellValue("Группа");
            cell = row.createCell(cellNum++);
            cell.setCellValue(group);
            cellNum += 4;
        }

        groups = null;

        rowNum++;
        row = sheet.createRow(rowNum++);
        cellNum = 1;

        for(int i = 0; i<studentsRecords.size(); i++){
            Cell cell = row.createCell(cellNum++);
            cell.setCellValue("Студент");
            cell = row.createCell(cellNum++);
            cell.setCellValue("Выполнено");
            cell = row.createCell(cellNum++);
            cell.setCellValue("Кластер");
            cell = row.createCell(cellNum++);
            cell.setCellValue("Зачтено");
            cellNum+=2;
        }

        int rowNumTemp = rowNum;

        int max = 0;
        for (ArrayList<Record> studentsRecord : studentsRecords) {
            if (studentsRecord.size() > max) max = studentsRecord.size();
        }
        int nextRowNum = rowNumTemp+max;
        for(int i=0;i<max;i++){
            sheet.createRow(rowNumTemp+i);
        }
        cellNum = 1;
        for (ArrayList<Record> records : studentsRecords) {
            row = sheet.getRow(rowNumTemp);
            for (int j = 0; j < records.size(); j++) {
                Record record = records.get(j);
                Cell cell = row.createCell(cellNum++);
                cell.setCellValue(record.student);
                cell = row.createCell(cellNum++);
                if (record.completed.equals("да")) cell.setCellStyle(greenCellStyle);
                else cell.setCellStyle(redCellStyle);
                cell.setCellValue(record.completed);
                cell = row.createCell(cellNum++);
                if (record.cluster.equals("нет")) {
                    cell.setCellStyle(greenCellStyle);
                    cell.setCellValue(record.cluster);
                } else {
                    cell.setCellStyle(redCellStyle);
                    cell.setCellValue(Integer.parseInt(record.cluster));
                }

                cell = row.createCell(cellNum++);
                if (record.successfully.equals("да")) cell.setCellStyle(greenCellStyle);
                else cell.setCellStyle(redCellStyle);
                cell.setCellValue(record.successfully);
                cellNum -= 4;
                row = sheet.getRow(rowNumTemp + j + 1);
            }
            cellNum += 6;
        }

        studentsRecords = null;

        nextRowNum+=2;
        rowNum = nextRowNum;
        row = sheet.createRow(rowNum++);
        cellNum = 1;
        Cell cell = row.createCell(cellNum);
        cell.setCellValue("Кластеры");
        rowNum++;

        ArrayList<ArrayList<String>> clusters = databaseUtils.getClusters(task_id);

        for (ArrayList<String> cluster : clusters) {
            cellNum = 1;
            row = sheet.createRow(rowNum++);
            for (String s : cluster) {
                cell = row.createCell(cellNum++);
                cell.setCellValue(s);
            }
        }
        clusters = null;


        ArrayList<String> students = databaseUtils.getStudents(task_id);
        int[][] correlationMatrix = databaseUtils.getCorrelationMatrix(task_id);

        cellNum = 1;
        rowNum+=2;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(cellNum);
        cell.setCellValue("Корреляционная матрица");

        cellNum = 6;
        for(int i = 0; i<3;i++){
            cell = row.createCell(cellNum++);
            cell.setCellValue(thresholds.get(i));

        }

        cellNum =5;
        row = sheet.createRow(rowNum++);

        cell = row.createCell(cellNum++);
        cell.setCellValue(thresholds.get(3));
        cell = row.createCell(cellNum++);
        cell.setCellStyle(greenCellStyle);
        cell.setCellValue(thresholds.get(4));
        cell = row.createCell(cellNum++);
        cell.setCellStyle(orangeCellStyle);
        cell.setCellValue(thresholds.get(5));
        cell = row.createCell(cellNum);
        cell.setCellStyle(redCellStyle);
        cell.setCellValue(thresholds.get(6));


        rowNum+=2;
        cellNum=2;

        row = sheet.createRow(rowNum++);

        for (String student : students) {
            cell = row.createCell(cellNum++);
            cell.setCellValue(student);
        }

        for (int i = 0; i < students.size(); i++) {
            row = sheet.createRow(rowNum++);
            cellNum = 1;
            cell = row.createCell(cellNum++);
            cell.setCellValue(students.get(i));
            for (int j = 0; j < students.size(); j++) {
                cell = row.createCell(cellNum++);
                if(i==j){
                    cell.setCellStyle(blackCellStyle);
                }
                else if(correlationMatrix[i][j]>=highThreshold){
                    cell.setCellStyle(redCellStyle);
                }
                else if(correlationMatrix[i][j]>=middleThreshold){
                    cell.setCellStyle(orangeCellStyle);
                }
                else cell.setCellStyle(greenCellStyle);
                cell.setCellValue(correlationMatrix[i][j]);
            }
        }

        FileOutputStream out = new FileOutputStream(new File(resultsURL+File.separator+task+".xlsx"));
        workbook.write(out);
        out.close();

    }


    public static class Record{
        private final String student;
        private final String completed;
        private final String cluster;
        private final String successfully;

        public Record(String student, String completed, String cluster, String successfully) {
            this.student = student;
            this.completed = completed;
            this.cluster = cluster;
            this.successfully = successfully;
        }
    }
}
