package com.mycompany.plagiarism.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClustersDao {

    /**
     * Метод для добавления кластера в базу данных.
     * @param cluster список идентификаторов студентов, относящихся к добавляемому кластеру.
     * @param taskId идентификатор задания.
     * @param clusterNumber номер добавляемого кластера.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public void addCluster(ArrayList<Integer> cluster, int taskId, int clusterNumber) throws SQLException {
        String addCluster = "MERGE INTO clusters (TASK_ID, STUDENT_ID, CLUSTER_NUMBER) VALUES (?, ?, ?)";
        try(Connection connection = DatabaseUtils.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(addCluster);
            if (cluster.size() == 1) {
                statement.setInt(1, taskId);
                statement.setInt(2, cluster.get(0));
                statement.setInt(3, 0);
                statement.executeUpdate();
            } else if (cluster.size() > 1) {
                statement.setInt(1, taskId);
                statement.setInt(3, clusterNumber);
                for (Integer integer : cluster) {
                    statement.setInt(2, integer);
                    statement.executeUpdate();
                }
            }
        }
    }



    /**
     * Метод для получения списка кластеров, который записывается в итоговый отчет.
     * @param task_id идентификатор задания.
     * @return список кластеров.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public ArrayList<ArrayList<String>> getClusters(int task_id) throws SQLException {
        ArrayList<ArrayList<String>> clusters = new ArrayList<>();
        String query = "SELECT students.full_name, group_number, c.cluster_number FROM students JOIN clusters c " +
                "ON students.id = c.student_id WHERE c.cluster_number <> 0 AND c.task_id = ? ORDER BY c.cluster_number";
        try(Connection connection = DatabaseUtils.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, task_id);
            ResultSet rs = statement.executeQuery();
            int i = 0;
            while (rs.next()) {
                if (rs.getInt("cluster_number") > i) {
                    i += 1;
                    clusters.add(new ArrayList<>());
                    clusters.get(i - 1).add(Integer.toString(i));
                }
                clusters.get(i - 1).add(rs.getString("full_name") + " (" + rs.getString("group_number") + ")");
            }

            return clusters;
        }
    }
}
