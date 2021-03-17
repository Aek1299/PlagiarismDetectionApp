package com.mycompany.plagiarism.domain;

/**
 * Класс для работы с информацией о студенте.
 */

public class Record{
    private final String student;
    private final String completed;
    private final String cluster;
    private final String successfully;


    /**
     * Конструктор для создания записи о студенте.
     * @param student имя студента.
     * @param completed выполнено задание студентом или нет.
     * @param cluster номер кластера.
     * @param successfully выполнено задание успешно или нет.
     */

    public Record(String student, String completed, String cluster, String successfully) {
        this.student = student;
        this.completed = completed;
        this.cluster = cluster;
        this.successfully = successfully;
    }

    public String getStudent() {
        return student;
    }

    public String getCompleted() {
        return completed;
    }

    public String getCluster() {
        return cluster;
    }

    public String getSuccessfully() {
        return successfully;
    }
}