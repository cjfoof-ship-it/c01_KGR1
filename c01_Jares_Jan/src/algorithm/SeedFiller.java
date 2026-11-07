package algorithm;

import view.Panel;
import model.Point;
import java.util.LinkedList;
import java.util.Queue;

public class SeedFiller implements Filler {

    public enum FillMode {
        BACKGROUND_LIMITED,
        BORDER_LIMITED
    }

    private final int startX;
    private final int startY;
    private final int boundaryColor;
    private final int backgroundColor;
    private final FillMode mode;

    public SeedFiller(int startX, int startY, int boundaryColor, int backgroundColor, FillMode mode) {
        this.startX = startX;
        this.startY = startY;
        this.boundaryColor = boundaryColor;
        this.backgroundColor = backgroundColor;
        this.mode = mode;
    }

    @Override
    public void fill(Panel panel, int fillColor) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(startX, startY));

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int rawColor = panel.getPixelColor(p.x, p.y);
            int currentPointColor  = rawColor & 0xFFFFFF;
            // check if the point is still in raster or has background color
            if (rawColor == Panel.INVALID_COLOR || currentPointColor == fillColor) {
                continue;
            }

            boolean shouldFill = false;
            if (mode == FillMode.BACKGROUND_LIMITED) {
                if (currentPointColor == backgroundColor) {
                    shouldFill = true;
                }
            } else if (mode == FillMode.BORDER_LIMITED) {
                    if (currentPointColor != boundaryColor) {
                        shouldFill = true;
                    }
                }

            if (shouldFill) {
                    panel.drawPixel(p.x, p.y, fillColor);

                    queue.add(new Point(p.x + 1, p.y));
                    queue.add(new Point(p.x - 1, p.y));
                    queue.add(new Point(p.x, p.y + 1));
                    queue.add(new Point(p.x, p.y - 1));

            }
        }
    }
}
