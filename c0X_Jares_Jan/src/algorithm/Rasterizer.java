package algorithm;

import model.Line;
import view.Panel;

public class Rasterizer {

    // Bresenham algorithm

    public static void drawLine(Panel panel, Line line, int color) {
        int x1 = line.getStart().x;
        int y1 = line.getStart().y;
        int x2 = line.getEnd().x;
        int y2 = line.getEnd().y;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;

        int err = dx - dy;

        while (true) {
            panel.drawPixel(x1, y1, color);

            if (x1 == x2 && y1 == y2) {
                break;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }
}
