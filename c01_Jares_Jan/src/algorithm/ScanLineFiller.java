package algorithm;

import view.Panel;
import model.Polygon;
import model.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static controller.MainController.COLOR_WHITE;

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

            // scan through all vertices of polygon
            for (int i = 0; i < vertices. size(); i++) {
                Point p1 = vertices. get(i);
                // second point (if we get to last vertex, second point is the first vertex
                Point p2 = vertices. get((i + 1) % vertices.size());

                // skip horizontal lines
                if (p1.y == p2.y) continue;

                // sort points from lower Y to higher (start point is upper on the screen)
                Point start = (p1.y < p2.y) ?  p1 : p2;
                Point end = (p1.y < p2.y) ? p2 : p1;

                // check if current line crosses this border (use "<" instead of "<=" to cut lower line)
                if (y >= start.y && y < end.y) {
                    // calculate intersection x
                    double x = start.x + (double) (y - start.y) * (end.x - start.x) / (end.y - start.y);
                    intersections.add((int) Math.round(x));
                }
            }

            // sort intersections from left to right
            Collections.sort(intersections);

            // color pixels between intersections (0-1, 2-3, ...)
            for (int i = 0; i < intersections.size() - 1; i += 2) {
                int xStart = intersections.get(i);
                int xEnd = intersections.get(i + 1);

                for (int x = xStart; x <= xEnd; x++) {
                    panel.drawPixel(x, y, fillColor);
                }
            }
        }
        // draw again the border lines of polygon
        polygon.draw(panel, COLOR_WHITE);
    }
}
