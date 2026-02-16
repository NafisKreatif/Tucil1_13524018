package stima;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Solver {
    public static enum ValidationResult {
        Valid,
        InconsistentDimension,
        InvalidColorCode,
        ColorWithZeroAppearance,
        SeparatedColor
    }

    public static class Solution {
        boolean isSolved;
        char[][] grid;
        int totalCase;
        long time_ms;
    }

    public static ValidationResult ValidateInput(char[][] input) {
        int n = input.length;
        for (char[] cs : input) {
            if (cs.length != n)
                return ValidationResult.InconsistentDimension;
        }

        boolean[] hasColor = new boolean[n];
        Arrays.fill(hasColor, false);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (input[i][j] < 'A' || input[i][j] >= 'A' + n)
                    return ValidationResult.InvalidColorCode;
                if (hasColor[input[i][j] - 'A']) {
                    boolean hasAdjacentColor = false;
                    if (i > 0 && input[i - 1][j] == input[i][j])
                        hasAdjacentColor = true;
                    if (i < n - 1 && input[i + 1][j] == input[i][j])
                        hasAdjacentColor = true;
                    if (j > 0 && input[i][j - 1] == input[i][j])
                        hasAdjacentColor = true;
                    if (j < n - 1 && input[i][j + 1] == input[i][j])
                        hasAdjacentColor = true;

                    if (!hasAdjacentColor)
                        return ValidationResult.SeparatedColor;
                } else {
                    hasColor[input[i][j] - 'A'] = true;
                }
            }
        }
        for (int i = 0; i < n; i++) {
            if (!hasColor[i])
                return ValidationResult.ColorWithZeroAppearance;
        }
        return ValidationResult.Valid;
    }

    public static Solution Solve(char[][] input) {
        long startTime = System.nanoTime();
        
        int n = input.length;
        boolean[] usedColor = new boolean[n];
        int[] usedColumn = new int[n];
        for (int i = 0; i < n; i++) {
            usedColor[i] = false;
            usedColumn[i] = -1;
        }

        Solution solution = Bruteforce(input, 0, usedColor, usedColumn);
        long endTime = System.nanoTime();
        solution.time_ms = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        return solution;
    }

    private static Solution Bruteforce(char[][] input, int i, boolean[] usedColor, int[] usedColumn) {
        int n = input.length;
        boolean hasSolution = false;
        int totalCase = 0;
        for (int j = 0; j < n; j++) {
            // Kalau kolom ini sudah dipakai
            if (usedColumn[j] != -1) {
                totalCase++;
                continue;
            }
            // Kalau warna ini sudah dipakai
            if (usedColor[input[i][j] - 'A']) {
                totalCase++;
                continue;
            }
            // Kalau "queen" sebelumnya berdekatan (cukup cek diagonal)
            if (i > 0 && ((j > 0 && usedColumn[j - 1] == i - 1) || (j < n - 1 && usedColumn[j + 1] == i - 1))) {
                totalCase++;
                continue;
            }

            // Tandai kolom dan warna yang dipakai
            usedColor[input[i][j] - 'A'] = true;
            usedColumn[j] = i;

            if (i == n - 1) { // Sudah sampai ujung, pasti ini solusinya
                totalCase++;
                hasSolution = true;
                break;
            }

            Solution solution1 = Bruteforce(input, i + 1, usedColor, usedColumn);
            if (solution1.grid != null) { // Dapat solusi
                solution1.totalCase += totalCase;
                return solution1;

            } else { // Tidak dapat solusi, balikkan state seperti semula
                usedColor[input[i][j] - 'A'] = false;
                usedColumn[j] = -1;
            }
        }

        Solution solution = new Solution();
        solution.totalCase = totalCase;
        solution.isSolved = hasSolution;
        if (hasSolution) {
            // Tulis solusi ke dalam array 2d
            solution.grid = new char[n][n];
            for (int p = 0; p < n; p++) {
                for (int q = 0; q < n; q++) {
                    if (usedColumn[q] == p) {
                        solution.grid[p][q] = '#';
                    } else {
                        solution.grid[p][q] = input[p][q];
                    }
                }
            }
        }
        return solution;
    }
}
