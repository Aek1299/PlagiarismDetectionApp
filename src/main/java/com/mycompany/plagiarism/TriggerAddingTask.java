package com.mycompany.plagiarism;

import org.h2.api.Trigger;

import java.sql.*;


/**
 * Класс триггера, срабатывающего при добавлении задания в базу данных.
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class TriggerAddingTask implements Trigger {

    @Override
    public void init(Connection conn, String schemaName,
                     String triggerName, String tableName, boolean before, int type)
            throws SQLException {}

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow)
            throws SQLException {
        String querySQL = "SELECT id FROM students;";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(querySQL);
        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO solutions (student_id, task_id, " +
                "solution, processed) " +
                "VALUES (?, ?, NULL, NULL)");
        preparedStatement.setObject(2, newRow[0]);
        while(resultSet.next()){
            int studentId = resultSet.getInt("id");
            preparedStatement.setInt(1, studentId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void close() throws SQLException {}

    @Override
    public void remove() throws SQLException {}
}


