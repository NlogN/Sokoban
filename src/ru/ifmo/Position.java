package ru.ifmo;

/**
 * Created with IntelliJ IDEA.
 * User: Ilya
 * Date: 19.12.13
 * Time: 2:47
 */

public class Position implements Comparable {
    byte[][] a;
    String moveSequence;
    int score = 10000;

    public Position(byte[][] a, String path) {
        this.a = a;
        this.moveSequence = path;

        int sum = 0;
        int k = a.length;
        int l = a[0].length;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < l; j++) {
                if (a[i][j] == 2) {
                    int pathLeng = APath.closestGoalDistance(a, j, i);
                    sum += pathLeng;
                }
            }
        }
        this.score = sum;

    }


    @Override
    public int hashCode() {
        int result = 0;
        int k = this.a.length;
        int l = this.a[0].length;

        for (int i = 0; i < k; i++) {
            for (int j = 0; j < l; j++) {
                if (a[i][j] == 5) {
                    result += i * 2 + j * 3;
                }
                if (a[i][j] == 2) {
                    result += i * 5 + j * 7;
                }
                if (a[i][j] == 3) {
                    result += i * 11 + j * 17;
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        Position other = (Position) obj;
        int k = this.a.length;
        int l = this.a[0].length;

        for (int i = 0; i < k; i++) {
            for (int j = 0; j < l; j++) {
                if (a[i][j] != other.a[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int compareTo(Object obj) {
        Position entry = (Position) obj;

        int result = score - entry.score;
        if (result != 0) {
            return (int) result / Math.abs(result);
        }
        return 0;

    }


}