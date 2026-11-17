package model;

import algorithm.Filler;
import algorithm.SeedFiller;
import view.Panel;

public class SeedFillObject implements Drawable {
    private final Point seed;
    private final int boundaryColor;
    private final int backgroundColor;
    private final SeedFiller.FillMode mode;

    public SeedFillObject(Point seed, int boundaryColor, int backgroundColor, SeedFiller.FillMode mode) {
        this.seed = seed;
        this.boundaryColor = boundaryColor;
        this.backgroundColor = backgroundColor;
        this.mode = mode;
    }

    @Override

    public void draw(Panel panel, int color) {
        // every time scene redraws itself, on this point seedFill starts again
        Filler filler = new SeedFiller(
                seed.x, seed.y,
                boundaryColor,
                backgroundColor,
                mode     // can be switched to BACKGROUND_LIMITED
        );
        filler.fill(panel, color);
    }
}
