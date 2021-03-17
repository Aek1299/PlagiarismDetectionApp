package com.mycompany.plagiarism.service;

import com.antlr.CPP14.CPP14Lexer;
import com.antlr.Java9.Java9Lexer;
import com.mycompany.plagiarism.dao.*;
import com.mycompany.plagiarism.domain.ComparisonResult;
import com.mycompany.plagiarism.domain.Solution;
import org.antlr.v4.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SolutionsHandler {
    private static final Logger logger = LoggerFactory.getLogger(SolutionsHandler.class);
    public SolutionsHandler(int taskId, int threshold) throws SQLException {
        TaskDao taskDao = new TaskDao();
        try {
            if (taskId != 0) {
                startProcessing(taskDao, taskId, threshold);
            } else {
                Integer[] tasksIds = taskDao.getTasksIds();
                for (Integer task :
                        tasksIds) {
                    startProcessing(taskDao, taskId, threshold);
                }
            }
        }
        catch (SQLException e){
            logger.error(e.getMessage(), e);
            throw new SQLException();
        }

    }

    public void startProcessing(TaskDao taskDao, int taskId, int threshold) throws SQLException {
        try {
            SolutionsDao solutionsDao = new SolutionsDao();
            String lang = taskDao.getTaskLanguage(taskId);
            ResultsDao resultsDao = new ResultsDao();
            List<Solution> solutions = solutionsDao.getSolutionsForTask(taskId);
            for (int i = 0; i < solutions.size() - 1; i++) {
                if (solutions.get(i).isProcessed()) break;
                else {
                    List<ComparisonResult> results = taskProcessing(solutions, i, lang);
                    resultsDao.insertComparisonResults(results);
                }
            }
            assignClusters(resultsDao, taskId, threshold);
            solutionsDao.updateStatuses(taskId);
        }
        catch (SQLException e){
            logger.error(e.getMessage(), e);
            throw new SQLException();
        }
    }

    private List<ComparisonResult> taskProcessing(List<Solution> solutionList, int index, String lang){

        ArrayList<ComparisonResult> results = new ArrayList<>();

        Solution firstSolution = solutionList.get(index);

        for (int i = index+1; i < solutionList.size(); i++) {

            Solution secondSolution = solutionList.get(i);

            CharStream input1 = CharStreams.fromString(firstSolution.getSolution());
            CharStream input2 = CharStreams.fromString(secondSolution.getSolution());
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
            results.add(new ComparisonResult(firstSolution.getTaskId(), firstSolution.getStudentId(),
                    secondSolution.getStudentId(),res));
        }

        return results;
    }


    public void assignClusters(ResultsDao resultsDao, int taskId, int threshold) throws SQLException {
        ClustersDao clustersDao = new ClustersDao();
        ArrayList<Integer> students = resultsDao.getStudentsId(taskId);
        int[][] correlationMatrix = resultsDao.getCorrelationMatrix(taskId);
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
                clustersDao.addCluster(list, taskId, k);
                if(list.size()>1) k++;
            }
        }

    }


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

    private int getAdjUnvisitedVertex(int[][] adjMat, Vertex[] vertexList, int v){
        int nVerts = vertexList.length;
        for(int i=0; i<nVerts; i++)

            if(adjMat[v][i]!=0 && !vertexList[i].wasVisited)
                return i;
        return -1;
    }

    private static class Vertex {
        public int label;
        public boolean wasVisited;

        Vertex(int label) {
            this.label = label;
            wasVisited = false;
        }
    }

}
