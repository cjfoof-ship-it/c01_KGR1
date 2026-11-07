package model;

import algorithm.Filler;
import algorithm.SeedFiller;
import view.Panel;

public class SeedFillObject implements Drawable {
    private final Point seed;
    private final int boundaryColor;
    private final int backgroundColor;

    public SeedFillObject(Point seed, int boundaryColor, int backgroundColor) {
        this.seed = seed;
        this.boundaryColor = boundaryColor;
        this.backgroundColor = backgroundColor;
    }
    @Override

    public void draw(Panel panel, int color) {
        // every time scene redraws inself, on this point seedFill starts again
        Filler filler = new SeedFiller(
                seed.x, seed.y,
                boundaryColor,
                backgroundColor,
                SeedFiller.FillMode.BACKGROUND_LIMITED
        );
        filler.fill(panel, color);
    }
}
