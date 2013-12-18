package ru.ifmo;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: Ilya
 * Date: 19.12.13
 * Time: 2:46
 */
public class APath {

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
