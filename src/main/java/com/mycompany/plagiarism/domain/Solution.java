package com.mycompany.plagiarism.domain;

public class Solution {
    private int studentId;
    private int taskId;
    private String solution;
    private boolean processed;

    public Solution(int studentId, int taskId, String solution, boolean processed) {
        this.studentId = studentId;
        this.taskId = taskId;
        this.solution = solution;
        this.processed = processed;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
