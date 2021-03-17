package com.mycompany.plagiarism.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskDao {



    /**
     * Метод для добавления задания в базу данных.
     * @param title название задания.
     * @param lang язык программирования, на котором выполняется задание.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public static void addTask(String title, String lang) throws SQLException {
        String insertStudentSQL = "INSERT INTO tasks" +
                "  (title, language) VALUES " +
                " (?, ?);";
        try(Connection connection = DatabaseUtils.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(insertStudentSQL);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, lang);
            preparedStatement.executeUpdate();
        }
    }


    /**
     * Метод для удаления задания из базы данных.
     * @param title название задания.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public void removeTask(String title) throws SQLException {
        String quot = "'";
        String deleteStudentSQL = "DELETE FROM tasks WHERE title ="+quot+title+quot+";";
        try(Connection connection = DatabaseUtils.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(deleteStudentSQL);
        }
    }

    /**
     * Метод для получения языка программирования, на котором необходимо выполнить задание.
     * @param task_id идентификактор задания.
     * @return язык программирования, на котором необходимо выполнить задание.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public String getTaskLanguage(int task_id) throws SQLException {
        String query = "SELECT language FROM TASKS WHERE ID = "+task_id;
        try(Connection connection = DatabaseUtils.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getString("language");
        }
    }


    /**
     * Метод для получения списка заданий из базы данных.
     * @return словарь с парами название задания : идентификатор задания.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public HashMap<String, Integer> getTasks() throws SQLException {

        String query = "SELECT title, id FROM tasks";

        try(Connection connection = DatabaseUtils.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            HashMap<String, Integer> tasks = new HashMap<>();
            while (resultSet.next()) {
                tasks.put(resultSet.getString("title"), resultSet.getInt("id"));
            }

            return tasks;
        }
    }


    /**
     * Метод для получения массива идентификаторов заданий из базы данных.
     * @return массив идентификаторов заданий.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public Integer[] getTasksIds() throws SQLException {
        Integer[] tasks;
        String query = "SELECT id FROM tasks";
        try(Connection connection = DatabaseUtils.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ArrayList<Integer> listOfTasks = new ArrayList<>(10);
            while (resultSet.next()) {
                listOfTasks.add(resultSet.getInt("id"));
            }
            tasks = listOfTasks.toArray(new Integer[0]);
            return tasks;
        }
    }
}
