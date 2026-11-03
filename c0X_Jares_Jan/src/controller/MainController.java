package controller;

import model.Drawable;
import model.Line;
import model.Point;
import view.Panel;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MainController implements MouseListener, MouseMotionListener, KeyListener{

    private final Panel panel;

    // List of all already made and saved objects on the scene
    private final List<Drawable> scene = new  ArrayList<>();

    // points for currently drawn line
    private Point startPoint = null;
    private Point currentPoint = null;

    // state of Shift button
    private boolean isShiftPressed = false;

    // default colors
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_YELLOW = 0xFFFF00;

    public MainController(Panel panel) {
        this.panel = panel;

        // add listeners
        this.panel.addMouseListener(this);
        this.panel.addMouseMotionListener(this);
        this.panel.addKeyListener(this);
    }

    // draw whole scene: clean, draw saved lines and add currently drawn line

    private void redraw() {
        panel.clear();

        // draw saved lines
        for(Drawable obj : scene){
            obj.draw(panel, COLOR_WHITE);
        }

        // if new line is currently being drawn, add it as well, but in YELLOW
        if (startPoint != null && currentPoint != null) {
            Point end = currentPoint;

            // if Shift is pressed, adjust end point to closest direction of 0/45/90 degrees
            if (isShiftPressed) {
                //end = constrainPoint(startPoint, currentPoint);
            }

            Line previewLine = new Line(startPoint, end);
            previewLine.draw(panel, COLOR_YELLOW);
        }

        // send BufferedImage to screen
        panel.repaint();
    }

    // lock the direction for the Shift button. Counts closest horizontal/vertical/diagonal point
    // TODO

    // mouse listeners

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            startPoint = new Point(e.getX(), e.getY());
            currentPoint = startPoint;
            redraw();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (startPoint != null) {
            currentPoint = new Point(e.getX(), e.getY());
            redraw();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && startPoint != null) {
            Point endPoint = new Point(e.getX(), e.getY());
            if (isShiftPressed) {
                //endPoint = constrainPoint(startPoint, endPoint);
            }

            // save new line into screne
            scene.add(new Line(startPoint, endPoint));

            // refresh used points
            startPoint = null;
            currentPoint = null;
            redraw();
        }
    }

    // key listeners

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            isShiftPressed = true;
            redraw();   // line gets locked
        }
        if (e.getKeyCode() == KeyEvent.VK_C) {
            // clean the scene and restarts the data
            scene.clear();
            startPoint = null;
            currentPoint = null;
            redraw();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            isShiftPressed = false;
            redraw();       // undlock the line from Shift mode
        }
    }

    // unused methods from interface
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}

