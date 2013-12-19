package ru.ifmo;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Ilya
 * Date: 19.12.13
 * Time: 2:46
 */

public class Sokoban {

    static HashSet<Integer> allPositionSet = new HashSet<Integer>();   // содежит hashcode всех рассмотренных позиций
    static List<Pair> goalFieldList = new ArrayList<Pair>();   // список полей, в которых нахядятся цели '.'
    static List<Pair> lockFieldList = new ArrayList<Pair>();   // список полей, из которых не достать ящик

    static boolean stop = false;  // проверка того, найдено ли решение

    // построение решения
    // принимает на вход начальную позицию
    static void solution(Position startPosition) throws IOException {

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
        }
    }


    public static void main(String[] args) throws IOException {

        // чтение входных данных
        String filePath = "input";
        byte[][] a = getInputData(filePath);

        // заполенние lockFieldList
        setLockFieldList(a);

        // начальная позиция
        Position startPosition = new Position(a, "");

        solution(startPosition);

    }


    // построение следующего уровня позиций
    // принимает на вход текущий уровень levelPositionList
    static HashSet<Position> getNextLevelList(HashSet<Position> levelPositionList) {

        HashSet<Position> newLevelPositionList = new HashSet<Position>();
        Object[] posArray = levelPositionList.toArray();
        levelPositionList.clear();
        Arrays.sort(posArray);
        int m = posArray.length / 2;

        for (int j = 0; j < posArray.length; j++) {

            Position position = (Position) posArray[j];


            HashSet<Position> newList = getNewPositionList(position);
            //  построение следующей позиции дважды для позиций с меньшим sumDist
            if (j < m) {// выбор части позиций с меньшим sumDist
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
            if (stop) {
                break;
            }

        }

        return newLevelPositionList;
    }

    // чтение входных данных
    public static byte[][] getInputData(String path) throws IOException {
        int k = 0;
        int l = 0;
        List<String> list = getInputList1(path);
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


    // проверка позиции на содержание deadLock
    public static boolean checkLock(byte[][] a) {
        for (Pair pair : lockFieldList) {
            if (a[pair.y][pair.x] == 2) {
                return false;
            }
        }

        return true;
    }

    // заполнение lockFieldList
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

    // вычисление hashCode для позиции
    public static int hashCode(Position pos) {
        return Arrays.hashCode(pos.a);
    }


    // построение списка позиций, который можно получить из позиции pos сдвинув один из ящиков на соседнюю клетку
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

    // проверяет, не исчезла ли одна из целей '.'
    static void checkGoalFields(byte[][] a) {
        for (Pair pair : goalFieldList) {
            int x = pair.getX();
            int y = pair.getY();
            if (a[y][x] == 0) {
                a[y][x] = 3;
            }
        }
    }

    // проверяет наличие цели '.' в указанном поле
    // принимает координаты поля (p,q)
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
//        print(pos);
//        print(newPos);
    }

    // возвращает копию массива
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

    // чтение входных данных из консоли
    public static List<String> getInputList2() throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        String line;
        List<String> list = new ArrayList<String>();
        while ((line = r.readLine()) != null) {
            list.add(line);
        }

        return list;
    }

    // чтение входных данных из файла
    public static List<String> getInputList1(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        List<String> list = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            list.add(line);
        }

        return list;
    }

    // проверяет стоят ли все ящики на целях
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


    // возвращает координаты поля, в котором находится sokoban
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





