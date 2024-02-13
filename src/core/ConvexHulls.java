package core;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ConvexHulls {
    //Divide and conquer
    public static List<int[]> convexHull(ArrayList<Point> p){
        int size = p.size();
        int[][] points = new int[size][2];
        for (int i = 0; i < size; i++) {
            points[i][0] = p.get(i).x;
            points[i][1] = p.get(i).y;
        }
        if (points.length < 3) {
            return Arrays.asList(points);
        }

        Arrays.sort(points,
                (a, b)
                        -> a[0] != b[0] ? a[0] - b[0]
                        : a[1] - b[1]);
        List<int[]> upper = new ArrayList<>();
        List<int[]> lower = new ArrayList<>();

        for (int[] point : points) {
            while (upper.size() >= 2
                    && isNotRightTurn(
                    upper.get(upper.size() - 2),
                    upper.get(upper.size() - 1),
                    point)) {
                upper.remove(upper.size() - 1);
            }
            upper.add(point);
        }

        for (int i = points.length - 1; i >= 0; i--) {
            int[] point = points[i];
            while (lower.size() >= 2
                    && isNotRightTurn(
                    lower.get(lower.size() - 2),
                    lower.get(lower.size() - 1),
                    point)) {
                lower.remove(lower.size() - 1);
            }
            lower.add(point);
        }

        HashSet<int[]> hull = new HashSet<>(upper);
        hull.addAll(lower);
        return new ArrayList<>(hull);
    }
    // to check correct direction
    private static boolean isNotRightTurn(int[] a, int[] b, int[] c) {
        return (b[0] - a[0]) * (c[1] - a[1])
                - (b[1] - a[1]) * (c[0] - a[0])
                <= 0;
    }

}

