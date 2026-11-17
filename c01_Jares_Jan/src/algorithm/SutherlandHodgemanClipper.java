package algorithm;

import model.Point;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class SutherlandHodgemanClipper {

    private final Polygon clipPolygon;

    public SutherlandHodgemanClipper(Polygon clipPolygon) {
        this.clipPolygon = clipPolygon;
    }

    // clips enclosed polygon with convex clipping polygon
    public Polygon clip(Polygon subjectPolygon) {
        List<Point> clipVertices = clipPolygon.getVertices();
        List<Point> outputList = new ArrayList<>(subjectPolygon.getVertices());

        if (clipVertices.size() < 3 || outputList.size() < 3) {
            return new Polygon();
        }

        // check the orientation of clipping polygon (positive (CCW) vs negative (CW))
        double orientation = calculateOrientation(clipVertices);

        // clip step by step, using each side of clipping polygon
        for (int i = 0; i < clipVertices.size(); i++) {
            Point clipEdgeStart = clipVertices.get(i);
            Point clipEdgeEnd = clipVertices.get((i + 1) % clipVertices.size());

            List<Point> inputList = new ArrayList<>(outputList);
            outputList.clear();

            if (inputList.isEmpty()) {
                break;
            }

            Point s = inputList.get(inputList.size() - 1);
            for (Point e : inputList) {
                if (isInside(e, clipEdgeStart, clipEdgeEnd, orientation)) {
                    if (!isInside(s, clipEdgeStart, clipEdgeEnd, orientation)) {
                        outputList.add(computeIntersection(s, e, clipEdgeStart, clipEdgeEnd));
                    }
                    outputList.add(e);
                } else if (isInside(s, clipEdgeStart, clipEdgeEnd, orientation)) {
                    outputList.add(computeIntersection(s, e, clipEdgeStart, clipEdgeEnd));
                }
                s = e;
            }
        }

        Polygon result = new Polygon();
        for (Point p : outputList) {
            result.addVertex(p);
        }
        return result;
    }

    // compute orientation of polygon (CW vs CCW)
    private double calculateOrientation(List<Point> vertices) {
        double area = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Point p1 = vertices.get(i);
            Point p2 = vertices.get((i + 1) % vertices.size());
            area += (double) (p2.x - p1.x) * (p2.y + p1.y);
        }
        return area;
    }

    // check if point "p" lies on inner side of clipping edge
    private boolean isInside(Point p, Point edgeStart, Point edgeEnd, double orientation) {
        double crossProduct = (double) (edgeEnd.x - edgeStart.x) * (p.y - edgeStart.y)
                - (double) (edgeEnd.y - edgeStart.y) * (p.x - edgeStart.x);

        if (orientation >= 0) {
            return crossProduct <= 0;
        } else {
            return crossProduct >= 0;
        }
    }

    // compute intersections of two lines (edges of polygon (S-E) and clipping edges (A-B))
    private Point computeIntersection(Point s, Point e, Point a, Point b) {
        double x1 = s.x, y1 = s.y;
        double x2 = e.x, y2 = e.y;
        double x3 = a.x, y3 = a.y;
        double x4 = b.x, y4 = b.y;

        double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        // prevention of parallel lines
        if (Math.abs(denominator) < 1e-9) {
            return e;
        }

        double px = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / denominator;
        double py = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / denominator;

        return new Point((int) Math.round(px), (int) Math.round(py));
    }
}
