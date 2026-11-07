package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Panel extends JPanel {

    private final BufferedImage raster;
    private final int width;
    private final int height;
    public static final int BACKGROUND_COLOR = 0X222222; //  dark grey
    public static final int INVALID_COLOR = -1;

    public Panel(int width, int height) {
        this.width = width;
        this.height = height;

        // set prefered size of component for pack() method to make sure our panel will have desired number of pixels
        setPreferredSize(new Dimension(width, height));

        // create raster (canvas) in memory, INT_RGB, each pixel is one INT
        raster = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // to clear the canvas at the beggining
        clear();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // draw BufferedImage from the top left corner [0, 0]
        g.drawImage(raster, 0, 0, null);
    }

    public void drawPixel(int x,  int y, int color) {
        // prevention from drawing out of frame
        if (x >= 0 && x < width && y >= 0 && y < height) {
            raster.setRGB(x, y, color);
        }
    }

    public int getPixelColor(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return raster.getRGB(x, y);
        }
        return INVALID_COLOR;
    }

    public void clear() {
        Graphics g = raster.getGraphics();      // create g tool
        g.setColor(new Color(BACKGROUND_COLOR));
        g.fillRect(0, 0, width, height);
        g.dispose();
    }
}
