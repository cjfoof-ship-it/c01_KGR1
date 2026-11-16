package model;

import algorithm.Filler;
import algorithm.ScanLineFiller;
import view.Panel;

public class ScanLineObject implements Drawable {
    private final Polygon polygon;

    public ScanLineObject(Polygon polygon) {
        this.polygon = polygon;
    }

    @Override
    public void draw(Panel panel, int color) {
        Filler filler = new ScanLineFiller(polygon);
        filler.fill(panel, color);
    }
}
