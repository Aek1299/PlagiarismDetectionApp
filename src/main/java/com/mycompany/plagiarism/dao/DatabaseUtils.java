package com.mycompany.plagiarism.dao;


import java.io.File;
import java.sql.*;


/**
 * Класс взаимодействия с базой данных H2. Реализует всю логику взаимодействия с базой данных.
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class DatabaseUtils {
    private static final String jdbcURL = "jdbc:h2:";
    private static final String jdbcUsername = "sa";
    private static final String jdbcPassword = "";
    private static String URI;


    public static void setURI(String URI){
        DatabaseUtils.URI = URI;
    }

    /**
     * Метод для получения соединения с базой данных.
     * @return объект класса Connection, который инкапсулирует соединение с базой данных.
     * @throws ClassNotFoundException исключение, связанное с загрузкой драйвера базы данных.
     * @throws SQLException исключение, связанное с ошибкой соединения с базой данных.
     */

    public static Connection getConnection() throws SQLException {
        Connection connection;

        connection = DriverManager.getConnection(jdbcURL+ File.separator+URI, jdbcUsername, jdbcPassword);

        return connection;
    }

    /**
     * Вспомогательный метод для отображения исключения.
     * @param ex исключение типа SQLException.
     */

    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }


    /**
     * Метод для создания необходимых таблиц в новой базе данных.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public void databaseInitialization() throws SQLException {
        String createTableSQL = "CREATE TABLE students" +
                "("
                +"full_name varchar(60)," +
                "group_number varchar(10)," +
                "id int auto_increment,"+
                "PRIMARY KEY(full_name, group_number)" +
                ");";

        try(Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute(createTableSQL);
            createTableSQL = "CREATE TABLE tasks" +
                    "(" +
                    "id int auto_increment," +
                    "title varchar(15)," +
                    "language varchar(10) NOT NULL," +
                    "PRIMARY KEY(title)," +
                    "CHECK (language IN('Java','C++'))" +
                    ");";
            statement.execute(createTableSQL);

            createTableSQL = "CREATE TABLE solutions" +
                    "(" +
                    "student_id int REFERENCES students(id) ON DELETE CASCADE," +
                    "task_id int REFERENCES tasks(id) ON DELETE CASCADE," +
                    "solution text," +
                    "processed boolean," +
                    "PRIMARY KEY (student_id, task_id)" +
                    ");";

            statement.execute(createTableSQL);

            createTableSQL = "CREATE TABLE comparison_results" +
                    "(" +
                    "task_id int REFERENCES tasks(id) ON DELETE CASCADE," +
                    "student1_id int REFERENCES students(id) ON DELETE CASCADE," +
                    "student2_id int REFERENCES students(id) ON DELETE CASCADE," +
                    "result int NOT NULL," +
                    "PRIMARY KEY (task_id, student1_id, student2_id)" +
                    ");";

            statement.execute(createTableSQL);

            createTableSQL = "CREATE TABLE clusters" +
                    "(" +
                    "task_id int REFERENCES tasks(id) ON DELETE CASCADE," +
                    "student_id int REFERENCES students(id) ON DELETE CASCADE," +
                    "cluster_number int NOT NULL," +
                    "PRIMARY KEY (task_id, student_id)" +
                    ");";

            statement.execute(createTableSQL);

            String createTriggerSQL = "CREATE TRIGGER add_students AFTER INSERT ON students FOR EACH ROW CALL" +
                    " \"com.mycompany.plagiarism.dao.TriggerAddingStudent\"";
            statement.execute(createTriggerSQL);

            createTriggerSQL = "CREATE TRIGGER add_tasks AFTER INSERT ON tasks FOR EACH ROW CALL" +
                    " \"com.mycompany.plagiarism.dao.TriggerAddingTask\"";
            statement.execute(createTriggerSQL);

            connection.commit();
        }
    }
}
