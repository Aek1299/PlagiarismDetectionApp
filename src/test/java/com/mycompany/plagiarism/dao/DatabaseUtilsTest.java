package com.mycompany.plagiarism.dao;

import com.mycompany.plagiarism.service.Dispatcher;
import com.mycompany.plagiarism.service.SolutionsHandler;
import org.junit.*;


import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Objects;

import static org.junit.Assert.*;


public class DatabaseUtilsTest {
    private static final String DB_URL = System.getProperty("user.dir")+ File.separator+"test";

    @BeforeClass
    public static void startUp() throws Exception {
        new Dispatcher().databaseInit(DB_URL);
    }

    @AfterClass
    public static void shutDown(){
        File file = new File(DB_URL+".mv.db");
        if(!file.delete()){
            throw new RuntimeException("Unable to delete test database");
        }
    }


    public void addStudent() throws IOException, SQLException {
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("Список студентов.txt");
            InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr)) {
            String[] parameters = br.readLine().split(" ");
            for (int i = 0; i < Integer.parseInt(parameters[1]); i++) {
                new StudentsDao().addStudent(br.readLine(), parameters[0]);
            }
        }
    }


    public void addTask() throws IOException, SQLException {
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("Список заданий.txt");
            InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr)) {
            String[] parameters = br.readLine().split(" ");
            for (int i = 0; i < Integer.parseInt(parameters[1]); i++) {
                TaskDao.addTask(br.readLine(), parameters[0]);
            }
        }
    }


    public void addSolution() throws URISyntaxException, SQLException, IOException {
        File pack = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("ЛР1")).toURI());
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
                            Files.lines(Paths.get(studentFile.toURI()), StandardCharsets.ISO_8859_1).forEach(
                                    solution::append);
                        }
                    }
                    new SolutionsDao().addSolution(task, studentName, group, solution.toString());
                }
            }
        }
    }

    @Test
    public void startProcessing() throws SQLException, IOException, URISyntaxException {
        addStudent();
        addTask();
        addSolution();
        new SolutionsHandler(1, 75);
        int[][] matrix = new ResultsDao().getCorrelationMatrix(1);
        assertEquals(100, matrix[0][1]);
        assertEquals(100, matrix[1][0]);
        assertEquals(2, matrix[0][2]);
        assertEquals(43, matrix[0][3]);
        assertEquals(4, matrix[2][3]);
        assertEquals(4, matrix[3][2]);
    }

}