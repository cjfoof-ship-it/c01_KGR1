package model;

import view.Panel;

import java.util.ArrayList;
import java.util.List;

public class Polygon implements Drawable {

    // list of vertices of polygon
    private final List<Point> vertices;

    public Polygon() {
        this.vertices = new ArrayList<>();
    }

    // add new vertex in polygon
    public void addVertex(Point p) {
        this.vertices.add(p);
    }

    public List<Point> getVertices() {
        return vertices;
    }

    // clear the list of vertices
    public void clear() {
        vertices.clear();
    }

    // draw polygon, connect last point, current point and first point
    @Override
    public void draw(Panel panel, int color) {
        if (vertices.size() < 2) return;

        // connect all points in order (draw lines from xn -> xn+1 until reaching the last point)
        for (int i = 0; i < vertices.size() - 1; i++) {
            Line edge = new Line(vertices.get(i), vertices.get(i + 1));
            edge.draw(panel, color);
        }

        // close the polygon: connect last point with first
        Line closingEdge = new Line(vertices.get(vertices.size() - 1), vertices.get(0));
        closingEdge.draw(panel, color);
    }
}
