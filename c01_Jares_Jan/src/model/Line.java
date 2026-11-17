package model;

import view.Panel;
import algorithm.Rasterizer;

public class Line implements Drawable {
    private final Point start;
    private final Point end;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    @Override
    public void draw(Panel panel, int color) {
        // use the algorithm
        Rasterizer.drawLine(panel, this, color);
    }
}
