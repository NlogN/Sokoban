package ru.ifmo;

/**
 * Created with IntelliJ IDEA.
 * User: Ilya
 * Date: 19.12.13
 * Time: 2:47
 */

// конфигурация уровня
public class Position implements Comparable {
    byte[][] a;  // содержит положение всех объектов позиции
    String moveSequence; // последовательность движений sokoban
    int sumDist = 10000;

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
        this.sumDist = sum;

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

        int result = sumDist - entry.sumDist;
        if (result != 0) {
            return (int) result / Math.abs(result);
        }
        return 0;

    }

    public  void print() {
        int k = a.length;
        int l = a[0].length;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < l; j++) {
                System.out.print(a[i][j]);
            }
            System.out.println();
        }

    }


}