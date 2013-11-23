package com.twopeople.game;

/**
 * Created by Alexey
 * At 5:03 PM on 11/20/13
 */

public class Geom {
    public static double getDistBetweenPoints(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static double getDistFromPointToSegment(int x1, int y1, int x2, int y2, int x3, int y3) {
        int px = x2 - x1;
        int py = y2 - y1;

        int something = px * px + py * py;

        double u = ((x3 - x1) * px + (y3 - y1) * py) / (double) (something);

        if (u > 1) {
            u = 1;
        } else if (u < 0) {
            u = 0;
        }

        double x = x1 + u * px;
        double y = y1 + u * py;

        double dx = x - x3;
        double dy = y - y3;

        double dist = Math.sqrt(dx * dx + dy * dy);

        return dist;
    }
}