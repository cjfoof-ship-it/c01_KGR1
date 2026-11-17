package algorithm;

import model.Line;
import model.Point;

public class CohenSutherlandClipper {
    // 4 bit codes of areas
    private static final int INSIDE = 0;    // 0000
    private static final int LEFT = 1;  // 0001
    private static final int RIGHT = 2; // 0010
    private static final int DOWN = 4;  // 0100
    private static final int UP = 8;    // 1000

    // borders of clipping rectangle
    private final int xMin;
    private final int xMax;
    private final int yMin;
    private final int yMax;

    public CohenSutherlandClipper(int xMin, int yMin, int xMax, int yMax) {
        // make sure that "min" is always smaller than "max"
        this.xMin = Math.min(xMin, xMax);
        this.yMin = Math.min(yMin, yMax);
        this.xMax = Math.max(xMin, xMax);
        this.yMax = Math.max(yMin, yMax);
    }

    // count 4 bit code of point [x, y]
    private int computeCode(double x, double y) {
        int code = INSIDE;

        if (x < xMin) {
            code |= LEFT;
        } else if (x > xMax) {
            code |= RIGHT;
        }

        if (y < yMin) {
            code |= UP;
        } else if (y > yMax) {
            code |= DOWN;
        }

        return code;
    }

    // to clip line with rectangle (returns new clipped line, or null if rejected)
    public Line clip(Line line) {
        double x1 = line.getStart().x;
        double y1 = line.getStart().y;
        double x2 = line.getEnd().x;
        double y2 = line.getEnd().y;

        int code1 = computeCode(x1, y1);
        int code2 = computeCode(x2, y2);

        while (true) {
            if (((code1 | code2) == 0)) {
                // trivial acceptance, both points are inside the rectangle
                return new Line(
                        new Point((int) Math.round(x1), (int) Math.round(y1)),
                        new Point((int) Math.round(x2), (int) Math.round(y2))
                );
            } else if ((code1 & code2) != 0) {
                // trivial rejection, both points are outside the rectangle in the same area
                return null;
            } else {
                // line crosses the border(s) of rectangle
                double x = 0;
                double y = 0;

                // choose point that lays outside the rectangle
                int codeOut = (code1 != INSIDE) ? code1 : code2;

                // calculate intersections
                if ((codeOut & UP) != 0) {
                    x = x1 + (x2 - x1) * (yMin - y1) / (y2 - y1);
                    y = yMin;
                } else if ((codeOut & DOWN) != 0) {
                    x = x1 + (x2 - x1) * (yMax - y1) / (y2 - y1);
                    y = yMax;
                } else if ((codeOut & RIGHT) != 0) {
                    y = y1 + (y2 - y1) * (xMax - x1) / (x2 - x1);
                    x = xMax;
                } else if ((codeOut & LEFT) != 0) {
                    y = y1 + (y2 - y1) * (xMin - x1) / (x2 - x1);
                    x = xMin;
                }
                // outer point gets new value and its code of area gets recalculated
                if (codeOut == code1) {
                    x1 = x;
                    y1 = y;
                    code1 = computeCode(x1, y1);
                } else {
                    x2 = x;
                    y2 = y;
                    code2 = computeCode(x2, y2);
                }
            }
        }
    }
}
