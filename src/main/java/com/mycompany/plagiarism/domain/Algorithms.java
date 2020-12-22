package com.mycompany.plagiarism.domain;

import org.antlr.v4.runtime.Token;

/**
 * Класс, содержащий алгоритмы для обработки текста.
 * @author Aleksandr Karetnikov
 * @version 1.0
 */
public class Algorithms {

    /**
     * Функция, в которой реализовн алгоритм Вагнера-Фишера для нахождения расстояния Левенштейна.
     * @param program1 токенизированное представление исходного кода первой программы.
     * @param program2 токенизированное представление исходного кода второй программы.
     * @return матрица расстояний.
     */
    private static int findLevenshteinDistance(Token[] program1, Token[] program2) {

        int len1 = program1.length;
        int len2 = program2.length;
        int[][] distanceMatrix = new int[len1 + 1][len2 + 1];

        for (int i = 0; i < len1 + 1; i++) distanceMatrix[i][0] = i;
        for (int i = 0; i < len2 + 1; i++) distanceMatrix[0][i] = i;

        for (int row = 1; row < len1 + 1; row++) {
            for (int col = 1; col < len2 + 1; col++) {
                if (program1[row-1].getType() == program2[col-1].getType()) {
                    distanceMatrix[row][col] = distanceMatrix[row - 1][col - 1];
                } else {
                    int temp = Math.min(distanceMatrix[row - 1][col - 1], distanceMatrix[row][col - 1]);
                    distanceMatrix[row][col] = Math.min(temp, distanceMatrix[row - 1][col]) + 1;
                }
            }
        }
        return distanceMatrix[len1][len2];
    }

    /**
     * Функция для нахождения схожести программ.
     * @param program1 токенизированное представление исходного кода первой программы.
     * @param program2 токенизированное представление исходного кода второй программы.
     * @return процентное сходство программ.
     */

    public static int getSimilarity(Token[] program1, Token[] program2) {
        return Math.round((1 - (float)findLevenshteinDistance(program1, program2) /
                Math.max(program1.length, program2.length))* 100) ;
    }
}
