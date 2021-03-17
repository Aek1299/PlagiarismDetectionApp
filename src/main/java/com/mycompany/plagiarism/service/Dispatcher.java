package com.mycompany.plagiarism.service;

import com.mycompany.plagiarism.dao.DatabaseUtils;
import com.mycompany.plagiarism.dao.SolutionsDao;
import com.mycompany.plagiarism.dao.StudentsDao;
import com.mycompany.plagiarism.dao.TaskDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;

public class Dispatcher {
    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);
    private TaskDao taskDao = new TaskDao();
    private StudentsDao studentsDao = new StudentsDao();
    private SolutionsDao solutionsDao = new SolutionsDao();

    public void addStudent(String readLine, String s) throws SQLException {
        try{
            studentsDao.addStudent(readLine, s);
        }catch (SQLException e){
            logger.error(e.getMessage(), e);
            throw new SQLException();
        }
    }

    public HashMap<String, Integer> getTasks() throws SQLException {
        try {
            return taskDao.getTasks();
        }
        catch (SQLException e){
            logger.error(e.getMessage(), e);
            throw new SQLException();
        }
    }

    public void databaseInit(String URI) throws SQLException {
        try {
            DatabaseUtils.setURI(URI);
            new DatabaseUtils().databaseInitialization();
        }
        catch (SQLException e){
            logger.error(e.getMessage(), e);
            throw new SQLException();
        }
    }

    public void setDatabase(String URI) throws SQLException {
        DatabaseUtils.setURI(URI);
    }

    public void removeStudent(String readLine, String s) throws SQLException {
        try {
            studentsDao.removeStudent(readLine, s);
        }
        catch (SQLException e){
            logger.error(e.getMessage(), e);
            throw new SQLException();
        }
    }

    public void addSolution(String task, String studentName, String group, String toString) throws SQLException {
        try{
            solutionsDao.addSolution(task, studentName, group, toString);
        }
        catch (SQLException e){
            logger.error(e.getMessage(), e);
            throw new SQLException();
        }
    }

    public void removeTask(String text) throws SQLException {
        try{
            taskDao.removeTask(text);
        }
        catch (SQLException e){
            logger.error(e.getMessage(), e);
            throw new SQLException();
        }
    }

    public void addTask(String readLine, String s) throws SQLException {
        try{
            taskDao.addTask(readLine, s);
        }
        catch (SQLException e){
            logger.error(e.getMessage(), e);
            throw new SQLException();
        }
    }
}
