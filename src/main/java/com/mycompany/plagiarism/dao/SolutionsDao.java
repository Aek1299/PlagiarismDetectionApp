package com.mycompany.plagiarism.dao;

import com.mycompany.plagiarism.domain.Solution;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SolutionsDao {


    /**
     * Метод для добавления решения (программного кода) студента в базу данных.
     * @param task название задания, в рамках которого подготовлено решение.
     * @param studentName имя студента, подготовившего решение.
     * @param group группа студента, подготовившего решение.
     * @param solution решение (программный код) студента.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public void addSolution(String task, String studentName, String group, String solution) throws SQLException {
        String addSolutionSQL = "WITH info_table AS ( " +
                "SELECT s.id AS student_id, t.id AS task_id "+
                "FROM students AS s " +
                "JOIN tasks AS t  " +
                "WHERE full_name  = ? AND group_number = ? AND title = ?) " +
                "UPDATE solutions SET " +
                "solution = ?," +
                "processed = false " +
                "WHERE student_id = (SELECT student_id FROM info_table) " +
                "AND task_id = (SELECT task_id FROM info_table);";

        try(Connection connection = DatabaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(addSolutionSQL);
            preparedStatement.setString(1, studentName);
            preparedStatement.setString(2, group);
            preparedStatement.setString(3, task);
            preparedStatement.setString(4, solution);

            preparedStatement.executeUpdate();
        }
    }



    public List<Solution> getSolutionsForTask(int taskId) throws SQLException {
        String query;
        List<Solution> solutionList = new ArrayList<>();
        query = "SELECT * FROM solutions WHERE processed IS NOT NULL AND task_id = "+taskId+" ORDER BY processed";

        try(Connection connection = DatabaseUtils.getConnection()) {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                solutionList.add(new Solution(
                        resultSet.getInt("student_id"),
                        resultSet.getInt("task_id"),
                        resultSet.getString("solution"),
                        resultSet.getBoolean("processed")
                ));
            }
        }
        return solutionList;
    }

    public void updateStatuses(int taskId) throws SQLException {
        String statusesUpdate = "UPDATE SOLUTIONS SET " +
                "processed = true WHERE solution IS NOT NULL AND task_id = ?";
        try(Connection connection = DatabaseUtils.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(statusesUpdate);
            statement.setInt(1, taskId);
            statement.executeUpdate();
        }
    }
}
