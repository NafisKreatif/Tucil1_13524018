package stima;

import java.util.Arrays;

public class Validation {
    public static final int maxGridSize = 26;

    public static enum ValidationResult {
        Valid,
        InconsistentDimension,
        InvalidColorCode,
        WrongNumberOfRegion,
        SeparatedRegion,
        TooBig,
        ZeroSize
    }

    public static ValidationResult validateInput(char[][] input) {
        int n = input.length;
        for (char[] cs : input) {
            if (cs.length != n)
                return ValidationResult.InconsistentDimension;
        }

        if (n == 0)
            return ValidationResult.ZeroSize;
        if (n > maxGridSize) {
            return ValidationResult.TooBig;
        }

        int totalRegion = 0;
        boolean[] hasColor = new boolean[26];
        boolean[][] isAdjacent = new boolean[n][n];
        Arrays.fill(hasColor, false);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (input[i][j] < 'A' || input[i][j] > 'Z')
                    return ValidationResult.InvalidColorCode;
                if (hasColor[input[i][j] - 'A']) {
                    if (!isAdjacent[i][j])
                        return ValidationResult.SeparatedRegion;
                } else {
                    hasColor[input[i][j] - 'A'] = true;
                    dfsAdjacentColor(i, j, isAdjacent, input);
                    totalRegion++;
                }
            }
        }
        if (totalRegion != n)
            return ValidationResult.WrongNumberOfRegion;
        return ValidationResult.Valid;
    }

    public static void dfsAdjacentColor(int i, int j, boolean[][] isAdjacent, char[][] input) {
        isAdjacent[i][j] = true;
        int n = isAdjacent.length;
        int[] offsetI = { 0, -1, 0, 1 };
        int[] offsetJ = { -1, 0, 1, 0 };
        for (int k = 0; k < 4; k++) {
            int new_i = i + offsetI[k];
            int new_j = j + offsetJ[k];
            if (new_i < 0 || new_j < 0 || new_i >= n || new_j >= n)
                continue;
            if (input[i][j] != input[new_i][new_j] || isAdjacent[new_i][new_j])
                continue;
            dfsAdjacentColor(new_i, new_j, isAdjacent, input);
        }
    }
}
