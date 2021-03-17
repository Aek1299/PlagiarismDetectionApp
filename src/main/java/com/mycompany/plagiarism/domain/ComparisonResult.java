package com.mycompany.plagiarism.domain;

public class ComparisonResult {
    private int taskId;
    private int firstStudentId;
    private int secondStudentId;
    private int result;

    public ComparisonResult(int taskId, int firstStudentId, int secondStudentId, int result) {
        this.taskId = taskId;
        this.firstStudentId = firstStudentId;
        this.secondStudentId = secondStudentId;
        this.result = result;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getFirstStudentId() {
        return firstStudentId;
    }

    public void setFirstStudentId(int firstStudentId) {
        this.firstStudentId = firstStudentId;
    }

    public int getSecondStudentId() {
        return secondStudentId;
    }

    public void setSecondStudentId(int secondStudentId) {
        this.secondStudentId = secondStudentId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
