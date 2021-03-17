package com.mycompany.plagiarism.dao;

import java.sql.*;
import java.util.ArrayList;

public class StudentsDao {

    /**
     * Метод для добавления студента в базу данных.
     * @param name имя студента.
     * @param group группа студента.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public void addStudent(String name, String group) throws SQLException {
        String insertStudentSQL = "INSERT INTO students" +
                "  (full_name, group_number) VALUES " +
                " (?, ?);";
        try(Connection connection = DatabaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(insertStudentSQL);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, group);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Метод для удаления студента из базы данных.
     * @param name имя студента.
     * @param group группа студента.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public void removeStudent(String name, String group) throws SQLException {
        String quot = "'";
        String deleteStudentSQL = "DELETE FROM students WHERE full_name ="+quot+name+quot +" AND group_number = "+group+";";
        try(Connection connection = DatabaseUtils.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(deleteStudentSQL);
        }
    }

    /**
     * Метод для получения списка групп из базы данных.
     * @return список групп студентов.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public ArrayList<String> getGroups() throws SQLException {
        ArrayList<String> groups = new ArrayList<>();
        String query = "SELECT group_number FROM students GROUP BY group_number";
        try(Connection connection = DatabaseUtils.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                groups.add(resultSet.getString("group_number"));
            }

            return groups;
        }
    }

}
