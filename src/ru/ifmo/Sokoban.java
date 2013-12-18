package ru.ifmo;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ilya
 * Date: 25.10.13
 */

public class Sokoban {

    static HashSet<Integer> allPositionSet = new HashSet<Integer>();
    static List<Pair> goalFieldList = new ArrayList<Pair>();
    static List<Pair> lockFieldList = new ArrayList<Pair>();

    static boolean stop = false;


    public static void main(String[] args) throws IOException {

        byte[][] a = getInputData();
        setLockFieldList(a);

        Position startPosition = new Position(a, "");
        HashSet<Position> levelPositionList = new HashSet<Position>();
        levelPositionList.add(startPosition);


        for (int i = 0; i < 100; i++) {
            for (Position position : levelPositionList) {
                int hashCode = hashCode(position);
                allPositionSet.add(hashCode);
            }

            levelPositionList = getNextLevelList(levelPositionList);

            if (stop) {
                return;
            }

           // System.out.println("level# " + i);
          //  System.out.println(levelPositionList.size());
//            if(i==50){
//                for (Position p:levelList){
//                    print(p);
//                }
//            }
        }


    }




    static HashSet<Position> getNextLevelList(HashSet<Position> levelPositionList) {
        HashSet<Position> newLevelPositionList = new HashSet<Position>();

        Object[]  posArray = levelPositionList.toArray();
        levelPositionList.clear();
        Arrays.sort(posArray);
        int l = posArray.length / 2;
        for (int j = 0; j < posArray.length; j++) {

            Position position = (Position) posArray[j];


            HashSet<Position> newList = getNewPositionList(position);
            if (j < l) {
                Object[] addList = newList.toArray();
                for (Object obj : addList) {
                    HashSet<Position> newList1 = getNewPositionList((Position) obj);
                    for (Position p : newList1) {
                        newList.add(p);
                    }
                }
            }
            for (Position pos : newList) {
                if (!isComplete(pos.a)) {
                    int hashCode = hashCode(pos);

                    if (!allPositionSet.contains(hashCode)) {
                        newLevelPositionList.add(pos);
                    }
                } else {
                    System.out.println(pos.moveSequence);

                    stop = true;
                    break;
                }
            }
            if(stop){
                break;
            }

        }

       return newLevelPositionList;
    }


    public static byte[][] getInputData() throws IOException {
        int k = 0;
        int l = 0;
        List<String> list = getInputList1();
        for (String line : list) {
            k++;
            l = Math.max(l, line.length());
        }

        byte[][] a = new byte[k][l];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < l; j++) {
                a[i][j] = 1;
            }
        }

        for (int i = 0; i < k; i++) {
            for (int j = 0; j < list.get(i).length(); j++) {
                char c = list.get(i).charAt(j);
                if (c == ' ') {
                    a[i][j] = 0;
                }
                if (c == '$' || c == '*') {
                    a[i][j] = 2;
                }
                if (c == '.') {
                    a[i][j] = 3;
                }

                if (c == '@' || c == '+') {
                    a[i][j] = 5;
                }

                if (c == '.' || c == '+' || c == '*') {
                    goalFieldList.add(new Pair(j, i));
                }

            }
        }
        for (int i = 0; i < k; i++) {
            a[i][0] = 1;
        }
        for (int i = 0; i < l; i++) {
            a[0][i] = 1;
        }

        return a;
    }

    public static void printLock(byte[][] a) {
        byte[][] newArr1 = getCopy(a);
        for (Pair p : lockFieldList) {
            newArr1[p.y][p.x] = 8;
        }
        APath.print(newArr1);

    }

    public static boolean checkLock(byte[][] a) {
        for (Pair pair : lockFieldList) {
            if (a[pair.y][pair.x] == 2) {
                return false;
            }
        }

        return true;
    }

    static void setLockFieldList(byte[][] a) {
        int k = a.length;
        int l = a[0].length;
        for (int i = 1; i < k - 1; i++) {
            for (int j = 1; j < l - 1; j++) {
                if (a[i][j] == 0 || a[i][j] == 5 || a[i][j] == 2) {
                    if (!isGoalField(j, i)) {
                        if ((a[i + 1][j] == 1 && a[i][j + 1] == 1) || (a[i + 1][j] == 1 && a[i][j - 1] == 1) || (a[i - 1][j] == 1 && a[i][j + 1] == 1) || (a[i - 1][j] == 1 && a[i][j - 1] == 1)) {
                            lockFieldList.add(new Pair(j, i));
                        }
                    }
                }
            }
        }
        List<Pair> newList = new ArrayList<Pair>();
        for (int i = 0; i < lockFieldList.size(); i++) {
            for (int j = 0; j < i; j++) {
                Pair lock1 = lockFieldList.get(i);
                Pair lock2 = lockFieldList.get(j);
                if (lock1.getX() == lock2.getX()) {
                    int y1 = Math.min(lock1.getY(), lock2.getY());
                    int y2 = Math.max(lock1.getY(), lock2.getY());
                    boolean t = true;
                    for (int f = y1 + 1; f < y2; f++) {
                        if (a[f][lock1.getX()] == 1 || isGoalField(lock1.getX(), f)) {
                            t = false;
                            break;
                        }
                    }
                    if (t) {
                        for (int f = y1; f <= y2; f++) {
                            if (a[f][lock1.getX() - 1] != 1) {
                                t = false;
                            }
                        }
                        if (!t) {
                            t = true;
                            for (int f = y1; f <= y2; f++) {
                                if (a[f][lock1.getX() + 1] != 1) {
                                    t = false;
                                }
                            }
                        }
                    }
                    if (t) {
                        for (int f = y1 + 1; f < y2; f++) {
                            newList.add(new Pair(lock1.getX(), f));
                        }
                    }

                }

                if (lock1.getY() == lock2.getY()) {
                    int x1 = Math.min(lock1.getX(), lock2.getX());
                    int x2 = Math.max(lock1.getX(), lock2.getX());
                    boolean t = true;
                    for (int f = x1 + 1; f < x2; f++) {
                        if (a[lock1.getY()][f] == 1 || isGoalField(f, lock1.getY())) {
                            t = false;
                            break;
                        }
                    }
                    if (t) {
                        for (int f = x1; f <= x2; f++) {
                            if (a[lock1.getY() - 1][f] != 1) {
                                t = false;
                            }
                        }
                        if (!t) {
                            t = true;
                            for (int f = x1; f <= x2; f++) {
                                if (a[lock1.getY() + 1][f] != 1) {
                                    t = false;
                                }
                            }
                        }
                    }
                    if (t) {
                        for (int f = x1 + 1; f < x2; f++) {
                            newList.add(new Pair(f, lock1.getY()));
                        }
                    }

                }
            }
        }

        for (Pair p : newList) {
            lockFieldList.add(p);
        }

    }

    public static int hashCode(Position pos) {
        return Arrays.hashCode(pos.a);
    }


    static HashSet<Position> getNewPositionList(Position pos) {
        HashSet<Position> newPositionList = new HashSet<Position>();

        byte[][] a = pos.a;

        if (!stop) {
            int k = a.length;
            int l = a[0].length;
            Pair sokPos = getSokobanField(a);

            for (int i = 0; i < l; i++) {  //x
                for (int j = 0; j < k; j++) { //y

                    if (a[j][i] == 2 || a[j][i] == 6) {
                        if ((a[j][i - 1] == 0 || a[j][i - 1] == 3 || a[j][i - 1] == 5) && (a[j][i + 1] == 0 || a[j][i + 1] == 3 || a[j][i + 1] == 5)) {
                            String path = APath.getMoveSequence(a, i - 1, j);
                            if (!path.equals("-1")) {
                                byte[][] newArr = getCopy(a);
                                newArr[sokPos.getY()][sokPos.getX()] = 0;
                                newArr[j][i] = 5;
                                newArr[j][i + 1] = 2;

                                checkGoalFields(newArr);
                                if (checkLock(newArr)) {
                                    Position newPos = new Position(newArr, pos.moveSequence + path + "R");
                                    newPositionList.add(newPos);
                                    print1(pos, newPos);
                                }
                            }
                            path = APath.getMoveSequence(a, i + 1, j);
                            if (!path.equals("-1")) {
                                byte[][] newArr = getCopy(a);
                                newArr[sokPos.getY()][sokPos.getX()] = 0;
                                newArr[j][i] = 5;
                                newArr[j][i - 1] = 2;

                                checkGoalFields(newArr);
                                if (checkLock(newArr)) {
                                    Position newPos = new Position(newArr, pos.moveSequence + path + "L");
                                    newPositionList.add(newPos);
                                    print1(pos, newPos);
                                }
                            }
                        }
                        if ((a[j - 1][i] == 0 || a[j - 1][i] == 3 || a[j - 1][i] == 5) && (a[j + 1][i] == 0 || a[j + 1][i] == 3 || a[j + 1][i] == 5)) {
                            String path = APath.getMoveSequence(a, i, j - 1);
                            if (!path.equals("-1")) {
                                byte[][] newArr = getCopy(a);
                                newArr[sokPos.getY()][sokPos.getX()] = 0;
                                newArr[j][i] = 5;
                                newArr[j + 1][i] = 2;
                                newArr[j + 1][i] = 2;

                                checkGoalFields(newArr);
                                if (checkLock(newArr)) {
                                    Position newPos = new Position(newArr, pos.moveSequence + path + "D");
                                    newPositionList.add(newPos);
                                    print1(pos, newPos);
                                }
                            }
                            path = APath.getMoveSequence(a, i, j + 1);
                            if (!path.equals("-1")) {
                                byte[][] newArr = getCopy(a);
                                newArr[sokPos.getY()][sokPos.getX()] = 0;
                                newArr[j][i] = 5;
                                newArr[j - 1][i] = 2;

                                checkGoalFields(newArr);
                                if (checkLock(newArr)) {
                                    Position newPos = new Position(newArr, pos.moveSequence + path + "U");
                                    newPositionList.add(newPos);
                                    print1(pos, newPos);
                                }
                            }
                        }

                    }
                }
            }

        }

        return newPositionList;
    }


    static void checkGoalFields(byte[][] a) {
        for (Pair pair : goalFieldList) {
            int x = pair.getX();
            int y = pair.getY();
            if (a[y][x] == 0) {
                a[y][x] = 3;
            }
        }
    }


    static boolean isGoalField(int p, int q) {
        for (Pair pair : goalFieldList) {
            int x = pair.getX();
            int y = pair.getY();
            if (p == x && q == y) {
                return true;
            }
        }
        return false;
    }



    static void print1(Position pos, Position newPos) {
//        System.out.println("======" );
//        print(pos);
//        print(newPos);
    }


    public static byte[][] getCopy(byte[][] a) {
        int k = a.length;
        int l = a[0].length;
        byte[][] a1 = new byte[k][l];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < l; j++) {
                a1[i][j] = a[i][j];
            }
        }
        return a1;
    }

    public static List<String> getInputList2() throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        String line;
        List<String> list = new ArrayList<String>();
        while ((line = r.readLine()) != null) {
            list.add(line);
        }

        return list;
    }

    public static List<String> getInputList1() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("input"));
        String line;
        List<String> list = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            list.add(line);
        }

        return list;
    }


    public static boolean isComplete(byte[][] a) {
        boolean t = true;
        for (Pair pair : goalFieldList) {
            int x = pair.getX();
            int y = pair.getY();
            if (a[y][x] != 2) {
                t = false;
                break;
            }
        }
        return t;
    }


    public static void print(Position pos) {
        System.out.println(pos.hashCode());
        System.out.println(pos.moveSequence);
        print(pos.a);
    }


    public static void print(byte[][] a) {
        int k = a.length;
        int l = a[0].length;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < l; j++) {
                if (a[i][j] == 1) {
                    System.out.print("#");
                }
                if (a[i][j] == 0) {
                    System.out.print(" ");
                }
                if (a[i][j] == 5) {
                    System.out.print("@");
                }
                if (a[i][j] == 2) {
                    System.out.print("$");
                }
                if (a[i][j] == 3) {
                    System.out.print(".");
                }

            }
            System.out.println();
        }
        System.out.println();
    }


    public static Pair getSokobanField(byte[][] a) {
        int k = a.length;
        int l = a[0].length;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < l; j++) {
                if (a[i][j] == 5) {
                    return new Pair(j, i);
                }
            }
        }

        return new Pair(0, 0);
    }

}

class Position implements Comparable {
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

class Pair {
    int x;
    int y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

}


class APath {

    public static String getMoveSequence(byte[][] a, int p, int q) {
        String res = "";
        Pair sokPos = Sokoban.getSokobanField(a);
        if (p == sokPos.getX() && q == sokPos.getY()) {
            return res;
        } else {
            if (a[q][p] == 0 || a[q][p] == 3) {
                byte[][] a1 = Sokoban.getCopy(a);

                Queue<Pair> queue = new LinkedList<Pair>();
                queue.add(sokPos);

                while (!queue.isEmpty()) {
                    Pair pair = queue.remove();
                    int x = pair.getX();
                    int y = pair.getY();
                    if (a1[y][x + 1] == 0 || a1[y][x + 1] == 3) {
                        a1[y][x + 1] = -1;
                        queue.add(new Pair(x + 1, y));
                        if (x + 1 == p && y == q) {
                            break;
                        }
                    }
                    if (a1[y][x - 1] == 0 || a1[y][x - 1] == 3) {
                        a1[y][x - 1] = -2;
                        queue.add(new Pair(x - 1, y));
                        if (x - 1 == p && y == q) {
                            break;
                        }
                    }
                    if (a1[y + 1][x] == 0 || a1[y + 1][x] == 3) {
                        a1[y + 1][x] = -3;
                        queue.add(new Pair(x, y + 1));
                        if (x == p && y + 1 == q) {
                            break;
                        }
                    }
                    if (a1[y - 1][x] == 0 || a1[y - 1][x] == 3) {
                        a1[y - 1][x] = -4;
                        queue.add(new Pair(x, y - 1));
                        if (x == p && y - 1 == q) {
                            break;
                        }
                    }
                }


                int x = p;
                int y = q;
                int k = a.length;
                int l = a[0].length;
                int n = 0;
                while (!(x == sokPos.getX() && y == sokPos.getY()) && n < k * l) {

                    if (a1[y][x] == -1) {
                        res = "r" + res;
                        x -= 1;
                    } else {
                        if (a1[y][x] == -4) {
                            res = "u" + res;
                            y += 1;
                        } else {
                            if (a1[y][x] == -3) {
                                res = "d" + res;
                                y -= 1;
                            } else {
                                if (a1[y][x] == -2) {
                                    res = "l" + res;
                                    x += 1;
                                }
                            }
                        }
                    }


                    n++;
                }


            }
        }
        if (res.length() == 0) {
            return "-1";
        } else {
            return res;
        }

    }


    public static int closestGoalDistance(byte[][] a, int p, int q) {
        for (Pair pair : Sokoban.goalFieldList) {
            int x = pair.getX();
            int y = pair.getY();
            if (x == p && y == q) {
                return 0;
            }
        }
        int k = a.length;
        int l = a[0].length;
        int res = 0;
        Pair startPos = new Pair(p, q);
        byte[][] a1 = Sokoban.getCopy(a);

        Queue<Pair> queue = new LinkedList<Pair>();
        queue.add(startPos);
        Pair endPos = new Pair(-1, -1);
        while (!queue.isEmpty()) {
            Pair pair = queue.remove();
            int x = pair.getX();
            int y = pair.getY();

            if (a1[y][x + 1] == 0 || a1[y][x + 1] == 3) {
                a1[y][x + 1] = -1;
                queue.add(new Pair(x + 1, y));
                if (a[y][x + 1] == 3) {
                    endPos.x = x + 1;
                    endPos.y = y;
                    break;
                }
            }
            if (a1[y][x - 1] == 0 || a1[y][x - 1] == 3) {
                a1[y][x - 1] = -2;
                queue.add(new Pair(x - 1, y));
                if (a[y][x - 1] == 3) {
                    endPos.x = x - 1;
                    endPos.y = y;
                    break;
                }
            }
            if (a1[y + 1][x] == 0 || a1[y + 1][x] == 3) {
                a1[y + 1][x] = -3;
                queue.add(new Pair(x, y + 1));
                if (a[y + 1][x] == 3) {
                    endPos.x = x;
                    endPos.y = y + 1;
                    break;
                }
            }
            if (a1[y - 1][x] == 0 || a1[y - 1][x] == 3) {
                a1[y - 1][x] = -4;
                queue.add(new Pair(x, y - 1));
                if (a[y - 1][x] == 3) {
                    endPos.x = x;
                    endPos.y = y - 1;
                    break;
                }
            }
        }


        if (endPos.x == -1 && endPos.y == -1) {
            res = k * l;

        } else {
            int x = endPos.x;
            int y = endPos.y;

            int n = 0;
            while (!(x == startPos.getX() && y == startPos.getY()) && n < k * l) {

                if (a1[y][x] == -1) {
                    res += 1;
                    x -= 1;
                } else {
                    if (a1[y][x] == -4) {
                        res += 1;
                        y += 1;
                    } else {
                        if (a1[y][x] == -3) {
                            res += 1;
                            y -= 1;
                        } else {
                            if (a1[y][x] == -2) {
                                res += 1;
                                x += 1;
                            }
                        }
                    }
                }

                n++;
            }
        }

        return res;
    }


    public static void print(byte[][] a) {
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