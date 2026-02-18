package stima;

import java.util.concurrent.TimeUnit;

public class Solver {
    public static class Solution {
        boolean isSolved;
        char[][] grid;
        int totalCase;
        int totalIteration;
        long time_ms;
    }

    public static enum VisualizationOption {
        Live,
        None
    }

    private static class StatCount {
        int totalCase;
        int totalIteration;

        StatCount() {
            this.totalCase = 0;
            this.totalIteration = 0;
        }
    }

    public static Solution solve(char[][] input) {
        return solve(input, VisualizationOption.None);
    }

    public static Solution solve(char[][] input, VisualizationOption option) {
        // Thread for visualization
        // Biar GUI nya ga ngefreeze
        IndexController.instance.drawBoard(input);
        IndexController.instance.setTotalCase(0);
        IndexController.instance.setTotalIteration(0);
        if (option == VisualizationOption.Live) {
            IndexController.instance.liveUpdateThread = new Thread(() -> {
                IndexController.instance.isOnLiveUpdate = true;
                long startTime = System.nanoTime();
                int n = input.length;
                boolean[] usedColor = new boolean[26];
                int[] usedColumn = new int[n];
                for (int i = 0; i < n; i++) {
                    usedColor[i] = false;
                    usedColumn[i] = -1;
                }
                Solution solution = solveRecursive(input, 0, usedColor, usedColumn, option, new StatCount());

                long endTime = System.nanoTime();
                solution.time_ms = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
                
                IndexController.instance.outputSolution(solution);
                IndexController.instance.isOnLiveUpdate = false;
            });
            IndexController.instance.liveUpdateThread.start();
            return null;
        } else {
            long startTime = System.nanoTime();
            int n = input.length;
            boolean[] usedColor = new boolean[26];
            int[] usedColumn = new int[n];
            for (int i = 0; i < n; i++) {
                usedColor[i] = false;
                usedColumn[i] = -1;
            }

            Solution solution = solveRecursive(input, 0, usedColor, usedColumn, VisualizationOption.None,
                    new StatCount());

            long endTime = System.nanoTime();
            solution.time_ms = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

            if (option == VisualizationOption.None) {
                tryUpdateGUI(() -> {
                    if (solution.isSolved)
                        IndexController.instance.drawBoard(input, solution.grid);
                    IndexController.instance.setTotalIteration(solution.totalIteration);
                    IndexController.instance.setTotalCase(solution.totalCase);
                });
            }

            return solution;
        }
    }

    private static Solution solveRecursive(char[][] input, int i, boolean[] usedColor, int[] usedColumn,
            VisualizationOption option, StatCount statCount) {
        int n = input.length;
        boolean hasSolution = false;
        for (int j = 0; j < n; j++) {
            statCount.totalIteration++;
            if (!checkValidity(input, i, j, usedColor, usedColumn)) {
                statCount.totalCase++;
                if (option != VisualizationOption.None) {
                    int temp = j;
                    tryUpdateGUI(() -> {
                        IndexController.instance.drawCell(i, temp, input.length, '#');
                        IndexController.instance.setTotalCase(statCount.totalCase);
                        IndexController.instance.setTotalIteration(statCount.totalIteration);
                    });
                    tryUpdateGUI(() -> {
                        IndexController.instance.drawCell(i, temp, input.length, input[i][temp]);
                    });
                }
                continue;
            }

            // Tandai kolom dan warna yang dipakai
            addQueen(input, i, j, usedColor, usedColumn, option, statCount);

            // Sudah sampai ujung, pasti ini solusinya
            if (i == n - 1) {
                hasSolution = true;
                statCount.totalCase++;
                break;
            }

            // Lanjut ke baris selanjutnya
            Solution solution = solveRecursive(input, i + 1, usedColor, usedColumn, option, statCount);

            if (solution.isSolved) { // Dapat solusi
                return solution;
            } else { // Tidak dapat solusi, balikkan state seperti semula
                removeQueen(input, i, j, usedColor, usedColumn, option, statCount);
            }
        }

        Solution solution = new Solution();
        solution.totalCase = statCount.totalCase;
        solution.totalIteration = statCount.totalIteration;
        solution.isSolved = hasSolution;
        if (hasSolution) {
            solution.grid = generateSolutionGrid(input, usedColumn);
        }
        return solution;
    }

    private static boolean checkValidity(char[][] input, int i, int j, boolean[] usedColor, int[] usedColumn) {
        int n = input.length;

        // Kalau kolom ini sudah dipakai
        if (usedColumn[j] != -1) {
            return false;
        }
        // Kalau warna ini sudah dipakai
        if (usedColor[input[i][j] - 'A']) {
            return false;
        }
        // Kalau "queen" sebelumnya berdekatan (cukup cek diagonal karena kolom sudah
        // dicek)
        if (i > 0 && ((j > 0 && usedColumn[j - 1] == i - 1) || (j < n - 1 && usedColumn[j + 1] == i - 1))) {
            return false;
        }
        return true;
    }

    private static char[][] generateSolutionGrid(char[][] input, int[] usedColumn) {
        int n = input.length;
        char[][] solutionGrid = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (usedColumn[j] == i) {
                    solutionGrid[i][j] = '#';
                } else {
                    solutionGrid[i][j] = input[i][j];
                }
            }
        }
        return solutionGrid;
    }

    private static void addQueen(char[][] input, int i, int j, boolean[] usedColor, int[] usedColumn,
            VisualizationOption option, StatCount statCount) {
        usedColor[input[i][j] - 'A'] = true;
        usedColumn[j] = i;
        if (option != VisualizationOption.None) {
            tryUpdateGUI(() -> {
                IndexController.instance.drawCell(i, j, input.length, '#');
                IndexController.instance.setTotalCase(statCount.totalCase);
                IndexController.instance.setTotalIteration(statCount.totalIteration);
            });
        }
    }

    private static void removeQueen(char[][] input, int i, int j, boolean[] usedColor, int[] usedColumn,
            VisualizationOption option, StatCount statCount) {
        usedColor[input[i][j] - 'A'] = false;
        usedColumn[j] = -1;
        if (option != VisualizationOption.None) {
            tryUpdateGUI(() -> {
                IndexController.instance.drawCell(i, j, input.length, input[i][j]);
                IndexController.instance.setTotalCase(statCount.totalCase);
                IndexController.instance.setTotalIteration(statCount.totalIteration);
            });
        }
    }

    private static void tryUpdateGUI(Runnable task) {
        try {
            task.run();
            Thread.sleep(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(e.getStackTrace());
        }
    }
}