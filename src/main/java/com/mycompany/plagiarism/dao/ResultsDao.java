package com.mycompany.plagiarism.dao;

import com.mycompany.plagiarism.domain.ComparisonResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ResultsDao {


    public void insertComparisonResults(List<ComparisonResult> results) throws SQLException {
        String insertResults = "MERGE INTO comparison_results KEY (task_id, student1_id, student2_id) " +
                "VALUES ";
        StringJoiner stringJoiner = new StringJoiner(", ", insertResults,"");
        for (int i = 0; i < results.size()*2; i++) {
            stringJoiner.add("(?, ?, ?, ?)");
        }

        try(Connection connection = DatabaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(stringJoiner.toString());
            for (int i = 0, j=1; i < results.size(); i+=1, j+=8) {
                ComparisonResult result = results.get(i);
                preparedStatement.setInt(j, result.getTaskId());
                preparedStatement.setInt(j+1, result.getFirstStudentId());
                preparedStatement.setInt(j+2, result.getSecondStudentId());
                preparedStatement.setInt(j+3, result.getResult());

                preparedStatement.setInt(j+4, result.getTaskId());
                preparedStatement.setInt(j+5, result.getSecondStudentId());
                preparedStatement.setInt(j+6, result.getFirstStudentId());
                preparedStatement.setInt(j+7, result.getResult());
            }
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Метод для вставки результата сравнения двух решений в базу данных.
     * @param taskId идентификатор задания.
     * @param studentId1 идентификатор первого студента.
     * @param studentId2 идентификатор второго студента.
     * @param result результат сравнения решений (процентное сходство).
     * @throws SQLException исключение, возникающее при обработке запроса.
     */


    public void insertComparisonResult(int taskId, int studentId1, int studentId2, int result) throws SQLException {
        String insertResult = "MERGE INTO comparison_results KEY (task_id, student1_id, student2_id) " +
                "VALUES ( ?, ?, ?, ?)";
        try(Connection connection = DatabaseUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(insertResult);
            preparedStatement.setInt(1, taskId);
            preparedStatement.setInt(2, studentId1);
            preparedStatement.setInt(3, studentId2);
            preparedStatement.setInt(4, result);
            preparedStatement.executeUpdate();
            preparedStatement.setInt(3, studentId1);
            preparedStatement.setInt(2, studentId2);
            preparedStatement.executeUpdate();
        }
    }











    /**
     * Метод для получения списка студентов с выполненным заданием в порядке возрастания идентификаторов студентов.
     * @param taskId идентификатор задания.
     * @return список студентов с выполненным заданием.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public ArrayList<Integer> getStudentsId(int taskId) throws SQLException {
        ArrayList<Integer> students = new ArrayList<>();
        String query = "SELECT student1_id FROM COMPARISON_RESULTS WHERE task_id = ? GROUP BY student1_id ORDER BY " +
                "student1_id";
        try(Connection connection = DatabaseUtils.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, taskId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                students.add(resultSet.getInt("student1_id"));
            }
            return students;
        }
    }








    /**
     * Метод для получения корреляционной матрицы обработанных студенческих решений.
     * @param task_id идентификатор задания.
     * @return корреляуионная матрица, состоящая из процентных значений схожести программ.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public int[][] getCorrelationMatrix(int task_id) throws SQLException {
        String query = "SELECT student1_id, student2_id, result FROM comparison_results WHERE task_id = "+task_id+" " +
                "ORDER BY student1_id, student2_id";
        try(Connection connection = DatabaseUtils.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            ArrayList<ArrayList<Integer>> result = new ArrayList<>();
            rs.next();
            int id1 = rs.getInt("student1_id");
            int i = 0;
            result.add(new ArrayList<>());

            while (!rs.isAfterLast()) {
                result.get(i).add(rs.getInt("result"));
                rs.next();
                if (!rs.isAfterLast() && id1 != rs.getInt("student1_id")) {
                    id1 = rs.getInt("student1_id");

                    result.add(new ArrayList<>());
                    i++;
                }
            }
            int[][] resultArr = new int[result.size()][result.size()];
            for (i = 0; i < result.size(); i++) {
                for (int j = 0; j < result.size() - 1; j++) {
                    if (i == j) {
                        resultArr[i][j] = 0;
                        for (int k = j; k < result.size() - 1; k++) {
                            resultArr[i][k + 1] = result.get(i).get(k);
                        }
                        break;
                    } else {
                        resultArr[i][j] = result.get(i).get(j);
                    }
                }
            }
            return resultArr;
        }
    }




    /**
     * Метод для получения списка студентов с порядком, соответствующим порядку результатов в корреляционной матрице.
     * @param task_id идентификатор задания.
     * @return список студентов.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public ArrayList<String> getStudents(int task_id) throws SQLException {
        String query = "WITH si as (SELECT student1_id FROM comparison_results WHERE task_id = "+task_id+" " +
                "GROUP BY student1_id) SELECT s.full_name, group_number FROM students s JOIN si ON " +
                "s.id = si.student1_id ORDER BY si.student1_id";
        try(Connection connection = DatabaseUtils.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            ArrayList<String> students = new ArrayList<>();
            while (rs.next()) {
                students.add(rs.getString("full_name") + " (" + rs.getString("group_number") + ")");
            }
            return students;
        }
    }

}
