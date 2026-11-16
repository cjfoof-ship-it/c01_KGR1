package algorithm;

import view.Panel;
import model.Polygon;
import model.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanLineFiller implements Filler {

    private final Polygon polygon;

    public ScanLineFiller(Polygon polygon) {
        this.polygon = polygon;
    }

    @Override
    public void fill (Panel panel, int fillColor) {
        List<Point> vertices = polygon.getVertices();

        // check if polygon has at least 3 vertices (otherwise there's no area to fill)
        if (vertices.size() < 3) return;

        // find the range of polygon on y axis (yMin and yMax)
        int yMin = Integer.MAX_VALUE;
        int yMax = Integer.MIN_VALUE;

        for (Point p : vertices) {
            if (p.y < yMin) yMin = p.y;
            if (p.y > yMax) yMax = p.y;
        }

        // scan polygon line by line from yMin to yMax
        for (int y = yMin; y <= yMax; y++) {
            List<Integer> intersections = new ArrayList<>();

            
        }
    }
}
