package controller;

import algorithm.Filler;
import algorithm.SeedFiller;
import model.*;
import view.Panel;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MainController implements MouseListener, MouseMotionListener, KeyListener {

    private final Panel panel;

    // List of all already made and saved objects on the scene
    private final List<Drawable> scene = new ArrayList<>();

    // points for currently drawn line
    private Point startPoint = null;
    private Point currentPoint = null;

    // state of Shift button
    private boolean isShiftPressed = false;

    private enum Mode {LINE, POLYGON, SEED_FILL}

    private Mode currentMode = Mode.LINE;   // line is default mode
    private Polygon currentPolygon = null;  // saves currently drawn polygon

    // default colors
    private static final int COLOR_WHITE = 0xFFFFFF;
    private static final int COLOR_YELLOW = 0xFFFF00;
    private static final int COLOR_GREEN = 0x00FF00;    // for curently drawn polygon
    private static final int COLOR_RED = 0xFF0000;   // filler color

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
        for (Drawable obj : scene) {
            if (obj instanceof SeedFillObject) {
                obj.draw(panel, COLOR_RED);
            } else obj.draw(panel, COLOR_WHITE);
        }

        if (currentMode == Mode.LINE) {
            // if new line is currently being drawn, add it as well, but in YELLOW
            if (startPoint != null && currentPoint != null) {
                Point end = currentPoint;

                // if Shift is pressed, adjust end point to closest direction of 0/45/90 degrees
                if (isShiftPressed) {
                    end = constrainPoint(startPoint, currentPoint);
                }

                Line previewLine = new Line(startPoint, end);
                previewLine.draw(panel, COLOR_YELLOW);
            }
        } else if (currentMode == Mode.POLYGON) {
            // if there is any base of currently drawn polygon, draw it GREEN
            if (currentPolygon != null) {
                currentPolygon.draw(panel, COLOR_GREEN);

                // currently drawn lines (user is just dragging the mouse)
                if (startPoint != null && currentPoint != null) {
                    List<Point> vertices = currentPolygon.getVertices();
                    if (!vertices.isEmpty()) {
                        Point first = vertices.get(0);
                        Point last = vertices.get(vertices.size() - 1);

                        // draw line from firts point to mouse and from last point to mouse
                        new Line(first, currentPoint).draw(panel, COLOR_YELLOW);
                        new Line(last, currentPoint).draw(panel, COLOR_YELLOW);
                    }
                }
            }
        }

        // send BufferedImage to screen
        panel.repaint();
    }

    // lock the direction for the Shift button. Counts closest horizontal/vertical/diagonal point

    private Point constrainPoint(Point start, Point current) {
        int dx = current.x - start.x;
        int dy = current.y - start.y;

        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        // decision of what is closer to our current position
        if (absDx > 2 * absDy) {
            // horizontal line
            return new Point(current.x, start.y);
        } else if (absDy > 2 * absDx) {
            // vertical line
            return new Point(start.x, current.y);
        } else {
            // diagonal
            int size = (absDx + absDy) / 2;
            int signX = (dx > 0) ? 1 : -1;
            int signY = (dy > 0) ? 1 : -1;
            return new Point(start.x + signX * size, start.y + signY * size);
        }
    }

    private void finalizePolygon() {
        if (currentPolygon != null) {
            // save it only if it has at least 3 vertices
            if (currentPolygon.getVertices().size() >= 3) {
                scene.add(currentPolygon);
            }
            currentPolygon = null;
        }
    }

    // mouse listeners

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Point clickedPoint = new Point(e.getX(), e.getY());

            if (currentMode == Mode.SEED_FILL) {
                // boundary color is white (color of lines in this project)
                // create an object of SeedFiller
                // 1. Vytvoříme objekt výplně a uložíme ho do seznamu scény
                SeedFillObject fillObj = new SeedFillObject(clickedPoint, COLOR_WHITE, Panel.BACKGROUND_COLOR);
                scene.add(fillObj);

                // 2. Zavoláme překreslení scény, které vykreslí čáry a rovnou i tuto novou výplň
                redraw();
                return;
            }

            if (currentMode == Mode.LINE) {
                startPoint = new Point(e.getX(), e.getY());
                currentPoint = startPoint;
            } else if (currentMode == Mode.POLYGON) {
                if (currentPolygon == null) {
                    currentPolygon = new Polygon();
                }
                if (currentPolygon.getVertices().isEmpty()) {
                    currentPolygon.addVertex(clickedPoint);
                }
                List<Point> vertices = currentPolygon.getVertices();
                startPoint = vertices.get(vertices.size() - 1);
                currentPoint = clickedPoint;
            }
            redraw();

        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (currentMode == Mode.SEED_FILL) return;
        if (startPoint != null) {
            currentPoint = new Point(e.getX(), e.getY());
            redraw();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && currentPoint != null) {
            Point endPoint = new Point(e.getX(), e.getY());

            if (currentMode == Mode.LINE) {
                if (isShiftPressed) {
                    endPoint = constrainPoint(startPoint, endPoint);
                }

                // save new line into screne
                scene.add(new Line(startPoint, endPoint));

                // refresh used points
                startPoint = null;
                currentPoint = null;
            } else if (currentMode == Mode.POLYGON) {
                // add point do polygon
                currentPolygon.addVertex(endPoint);
                startPoint = null;
                currentPoint = null;
            }
            redraw();
        }
    }

    // key listeners

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            isShiftPressed = true;
            redraw();
        }
        if (e.getKeyCode() == KeyEvent.VK_L) {
            // switch to LINE mode
            currentMode = Mode.LINE;
            finalizePolygon();   // in case polygon was not terminated
            redraw();
        }
        if (e.getKeyCode() == KeyEvent.VK_P) {
            // switch to POLYGON mode
            currentMode = Mode.POLYGON;
            redraw();
        }

        if (e.getKeyCode() == KeyEvent.VK_F) {
            currentMode = Mode.SEED_FILL;
            finalizePolygon();
            redraw();
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // pressing ENTER closes and saves currently drawn polygon
            finalizePolygon();
            redraw();
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
    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}

