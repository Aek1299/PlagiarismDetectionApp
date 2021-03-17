package com.mycompany.plagiarism.dao;

import com.mycompany.plagiarism.domain.Record;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecordDao {

    /**
     * Метод для получения итоговой таблицы с результатами из базы данных.
     * @param groups список групп.
     * @param task_id идентификатор задания.
     * @return итоговая таблица (в виде динамического массива динамических массивов записей).
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public ArrayList<ArrayList<Record>> getRecords(List<String> groups, int task_id) throws SQLException {
        ArrayList<ArrayList<Record>> records = new ArrayList<>();
        String query = "SELECT students.full_name, s.processed, c.cluster_number FROM students LEFT JOIN solutions s ON " +
                "students.id = s.student_id LEFT JOIN " +
                "clusters c on students.id = c.student_id AND s.task_id = c.task_id WHERE group_number = ?" +
                " AND s.task_id = ?";
        try(Connection connection = DatabaseUtils.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(2, task_id);
            for (String group :
                    groups) {
                statement.setString(1, group);
                ArrayList<Record> recordsList = new ArrayList<>();
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {

                    String completed;
                    if (rs.getBoolean("processed")) completed = "да";
                    else completed = "нет";

                    String cluster = "" + rs.getInt("cluster_number");
                    if (cluster.equals("0")) cluster = "нет";

                    String successfully;
                    if (completed.equals("да") && cluster.equals("нет")) successfully = "да";
                    else successfully = "нет";

                    recordsList.add(new Record(rs.getString("full_name"), completed,
                            cluster, successfully));
                }
                records.add(recordsList);
            }
            return records;
        }
    }
}
