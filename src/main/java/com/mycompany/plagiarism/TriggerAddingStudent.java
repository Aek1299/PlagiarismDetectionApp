package com.mycompany.plagiarism;

import org.h2.api.Trigger;

import java.sql.*;


/**
 * Класс триггера, срабатывающего при добавлении студента в базу данных.
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class TriggerAddingStudent implements Trigger {

    @Override
    public void init(Connection conn, String schemaName,
                     String triggerName, String tableName, boolean before, int type)
            throws SQLException {}

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow)
            throws SQLException {
        String querySQL = "SELECT id FROM tasks;";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(querySQL);
        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO solutions (student_id, task_id, " +
                "solution, processed) " +
                        "VALUES (?, ?, NULL, NULL)");
        preparedStatement.setObject(1, newRow[2]);
        while(resultSet.next()){
            int taskId = resultSet.getInt("id");
            preparedStatement.setInt(2, taskId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void close() throws SQLException {}

    @Override
    public void remove() throws SQLException {}
}

