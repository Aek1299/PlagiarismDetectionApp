package com.mycompany.plagiarism;

import org.antlr.v4.runtime.*;
import com.antlr.Java9.*;
import com.antlr.CPP14.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;


/**
 * Класс взаимодействия с базой данных H2. Реализует всю логику взаимодействия с базой данных.
 * @author Aleksandr Karetnikov
 * @version 1.0
 */

public class DatabaseUtils {
    private static final String jdbcURL = "jdbc:h2:";
    private static final String jdbcUsername = "sa";
    private static final String jdbcPassword = "";
    private final Connection connection;


    /**
     * Конструктор - получение соединения с базой данных, которое сохраняется в поле connection объекта данного класса.
     * @param URI адрес базы данных, к которой необходимо подключиться.
     * @throws ClassNotFoundException исключение, связанное с загрузкой драйвера базы данных.
     * @throws SQLException исключение, связанное с ошибкой соединения с базой данных.
     */

    public DatabaseUtils(String URI) throws SQLException, ClassNotFoundException {
        connection = getConnection(URI);
    }


    /**
     * Метод для получения соединения с базой данных.
     * @param name адрес базы данных, к которой необходимо подключиться.
     * @return объект класса Connection, который инкапсулирует соединение с базой данных.
     * @throws ClassNotFoundException исключение, связанное с загрузкой драйвера базы данных.
     * @throws SQLException исключение, связанное с ошибкой соединения с базой данных.
     */

    public static Connection getConnection(String name) throws ClassNotFoundException, SQLException {
        Connection connection;

        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection(jdbcURL+ File.separator+name, jdbcUsername, jdbcPassword);

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
     * Метод для добавления студента в базу данных.
     * @param name имя студента.
     * @param group группа студента.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public void addStudent(String name, String group) throws SQLException {
        String insertStudentSQL = "INSERT INTO students" +
                "  (full_name, group_number) VALUES " +
                " (?, ?);";
        try(PreparedStatement preparedStatement = connection.prepareStatement(insertStudentSQL)) {
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
        try(Statement statement = connection.createStatement()) {
            statement.execute(deleteStudentSQL);
        }
    }


    /**
     * Метод для добавления задания в базу данных.
     * @param title название задания.
     * @param lang язык программирования, на котором выполняется задание.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public void addTask(String title, String lang) throws SQLException {
        String insertStudentSQL = "INSERT INTO tasks" +
                "  (title, language) VALUES " +
                " (?, ?);";
        try(PreparedStatement preparedStatement = connection.prepareStatement(insertStudentSQL)){
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
        try(Statement statement = connection.createStatement()) {
            statement.execute(deleteStudentSQL);
        }

    }


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

        try(PreparedStatement preparedStatement = connection.prepareStatement(addSolutionSQL)) {

            preparedStatement.setString(1, studentName);
            preparedStatement.setString(2, group);
            preparedStatement.setString(3, task);
            preparedStatement.setString(4, solution);

            preparedStatement.executeUpdate();
        }
    }


    /**
     * Метод для запуска процесса обработки студенческих решений.
     * @param taskId идентификатор задания, для которого выполняется обработка.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public void startProcessing(int taskId, int threshold) throws SQLException {
        String query;
        if(taskId==-1) {
            query = "SELECT * FROM solutions WHERE processed IS NOT NULL ORDER BY task_id, processed";
        }
        else{
            query = "SELECT * FROM solutions WHERE processed IS NOT NULL AND task_id = "+taskId+" ORDER BY processed";
        }
        try(Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            while (!resultSet.isAfterLast()) {
                if (resultSet.getBoolean("processed")) resultSet.next();
                else taskProcessing(resultSet);
            }

            if (taskId == -1) {
                Integer[] tasks = getTasksId();
                for (Integer i :
                        tasks) {
                    assignClusters(i, threshold);
                }
            } else {
                assignClusters(taskId, threshold);
            }

            String statusesUpdate = "UPDATE SOLUTIONS SET " +
                    "processed = true WHERE solution IS NOT NULL";
            statement.executeUpdate(statusesUpdate);
        }
    }


    /**
     * Вспомогательный метод, в котором выполняется обработка студенческих решений.
     * @param rs результат запроса, получаемый в методе startProcessing.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    private void taskProcessing(ResultSet rs) throws SQLException {

        int taskId = rs.getInt("task_id");
        String lang = getTaskLanguage(taskId);
        while(!rs.isAfterLast() && rs.getInt("task_id")==taskId && !rs.getBoolean("processed")){
            int cur = rs.getRow();
            int studentId1 = rs.getInt("student_id");
            String solution1 = rs.getString("solution");
            rs.next();
            while(!rs.isAfterLast() && rs.getInt("task_id")==taskId){
                int studentId2 = rs.getInt("student_id");
                String solution2 = rs.getString("solution");
                CharStream input1 = CharStreams.fromString(solution1);
                CharStream input2 = CharStreams.fromString(solution2);
                Lexer lexer1;
                Lexer lexer2;

                if(lang.equals("Java")) {
                    lexer1 = new Java9Lexer(input1);
                    lexer2 = new Java9Lexer(input2);
                }
                else{
                    lexer1 = new CPP14Lexer(input1);
                    lexer2 = new CPP14Lexer(input2);
                }

                CommonTokenStream tokens1 = new CommonTokenStream(lexer1);
                tokens1.fill();
                CommonTokenStream tokens2 = new CommonTokenStream(lexer2);
                tokens2.fill();


                int res = Algorithms.getSimilarity(tokens1.getTokens().toArray(new Token[0]),
                        tokens2.getTokens().toArray(new Token[0]));

                insertComparisonResult(taskId, studentId1, studentId2, res);
                rs.next();
            }
            rs.absolute(cur+1);
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
        try(Statement statement = connection.createStatement()) {
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
     * Метод для получения языка программирования, на котором необходимо выполнить задание.
     * @param task_id идентификактор задания.
     * @return язык программирования, на котором необходимо выполнить задание.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    private String getTaskLanguage(int task_id) throws SQLException {
        String query = "SELECT language FROM TASKS WHERE ID = "+task_id;
        try(Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getString("language");
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

    private void insertComparisonResult(int taskId, int studentId1, int studentId2, int result) throws SQLException {
        String insertResult = "MERGE INTO comparison_results KEY (task_id, student1_id, student2_id) " +
                "VALUES ( ?, ?, ?, ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(insertResult)) {
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
     * Метод для получения списка заданий из базы данных.
     * @return словарь с парами название задания : идентификатор задания.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public HashMap<String, Integer> getTasks() throws SQLException {

        String query = "SELECT title, id FROM tasks";

        try(Statement statement = connection.createStatement()) {
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

    public Integer[] getTasksId() throws SQLException {
        Integer[] tasks;
        String query = "SELECT id FROM tasks";
        try(Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            ArrayList<Integer> listOfTasks = new ArrayList<>(10);
            while (resultSet.next()) {
                listOfTasks.add(resultSet.getInt("id"));
            }
            tasks = listOfTasks.toArray(new Integer[0]);
            return tasks;
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
        try(Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                groups.add(resultSet.getString("group_number"));
            }

            return groups;
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
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, taskId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                students.add(resultSet.getInt("student1_id"));
            }
            return students;
        }
    }


    /**
     * Метод для кластеризации и последующего присвоения номеров кластеров студентам.
     * @param taskId индентификатор задания.
     * @param threshold порог, начиная с которого программы считаются очень похожими.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public void assignClusters(int taskId, int threshold) throws SQLException {
        ArrayList<Integer> students = getStudentsId(taskId);
        int[][] correlationMatrix = getCorrelationMatrix(taskId);
        for(int i = 0; i<correlationMatrix.length;i++){
            for(int j = 0; j<correlationMatrix.length;j++){
                if(correlationMatrix[i][j]<threshold){
                    correlationMatrix[i][j]=0;
                }
            }
        }
        Vertex[] vertexList = new Vertex[students.size()];
        for(int i = 0; i<students.size();i++){
            vertexList[i] = new Vertex(students.get(i));
        }
        int k = 1;

        for(int i = 0; i<vertexList.length;i++){
            if(!vertexList[i].wasVisited){
                ArrayList<Integer> list= dfs(i, correlationMatrix,vertexList);
                addCluster(list, taskId, k);
                if(list.size()>1) k++;
            }
        }

    }


    /**
     * Метод для добавления кластера в базу данных.
     * @param cluster список идентификаторов студентов, относящихся к добавляемому кластеру.
     * @param taskId идентификатор задания.
     * @param clusterNumber номер добавляемого кластера.
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    private void addCluster(ArrayList<Integer> cluster, int taskId, int clusterNumber) throws SQLException {
        String addCluster = "MERGE INTO clusters (TASK_ID, STUDENT_ID, CLUSTER_NUMBER) VALUES (?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(addCluster)) {
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
     * Метод для осуществления обхода в глубину графа, заданного матрицей смежности.
     * @param startVertex индекс начальной вершины.
     * @param adjMat матрицы смежности.
     * @param vertexList список вершин.
     * @return список вершин, являющийся компонентой связности графа, т.е. кластер.
     */

    private ArrayList<Integer> dfs(int startVertex,int[][] adjMat, Vertex[] vertexList){
        ArrayList<Integer> result = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        vertexList[startVertex].wasVisited = true;
        result.add(vertexList[startVertex].label);
        stack.push(startVertex);
        while(!stack.isEmpty()){
            int v = this.getAdjUnvisitedVertex(adjMat, vertexList, stack.peek());
            if(v==-1) stack.pop();
            else{
                vertexList[v].wasVisited = true;
                result.add(vertexList[v].label);
                stack.push(v);
            }
        }
        return result;
    }


    /**
     * Вспомогательный метод для обхода графа в глубину, который осуществляет поиск непосещенной вершины, смежной для
     * данной.
     * @param adjMat матрица смежности графа.
     * @param vertexList список вершин графа.
     * @param v индекс данной (первой) вершины.
     * @return индекс непосещенной вершины.
     */

    private int getAdjUnvisitedVertex(int[][] adjMat,Vertex[] vertexList, int v){
        int nVerts = vertexList.length;
        for(int i=0; i<nVerts; i++)

            if(adjMat[v][i]!=0 && !vertexList[i].wasVisited)
                return i;
        return -1;
    }


    /**
     * Метод для получения итоговой таблицы с результатами из базы данных.
     * @param groups список групп.
     * @param task_id идентификатор задания.
     * @return итоговая таблица (в виде динамического массива динамических массивов записей).
     * @throws SQLException исключение, возникающее при обработке запроса.
     */

    public ArrayList<ArrayList<ExcelWriter.Record>> getRecords(List<String> groups, int task_id) throws SQLException {
        ArrayList<ArrayList<ExcelWriter.Record>> records = new ArrayList<>();
        String query = "SELECT students.full_name, s.processed, c.cluster_number FROM students LEFT JOIN solutions s ON " +
                "students.id = s.student_id LEFT JOIN " +
                "clusters c on students.id = c.student_id AND s.task_id = c.task_id WHERE group_number = ?" +
                " AND s.task_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(2, task_id);
            for (String group :
                    groups) {
                statement.setString(1, group);
                ArrayList<ExcelWriter.Record> recordsList = new ArrayList<>();
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

                    recordsList.add(new ExcelWriter.Record(rs.getString("full_name"), completed,
                            cluster, successfully));
                }
                records.add(recordsList);
            }
            return records;
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
        try(PreparedStatement statement = connection.prepareStatement(query)) {
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
        try(Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(query);
            ArrayList<String> students = new ArrayList<>();
            while (rs.next()) {
                students.add(rs.getString("full_name") + " (" + rs.getString("group_number") + ")");
            }
            return students;
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

        try(Statement statement = connection.createStatement()) {
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
                    " \"com.mycompany.plagiarism.TriggerAddingStudent\"";
            statement.execute(createTriggerSQL);

            createTriggerSQL = "CREATE TRIGGER add_tasks AFTER INSERT ON tasks FOR EACH ROW CALL" +
                    " \"com.mycompany.plagiarism.TriggerAddingTask\"";
            statement.execute(createTriggerSQL);
        }
    }


    /**
     * Вспомогательный класс для хранения вершины графа.
     */

    private static class Vertex {
        public int label;
        public boolean wasVisited;

        Vertex(int label) {
            this.label = label;
            wasVisited = false;
        }
    }

}
