package controller;

import algorithm.CohenSutherlandClipper;
import algorithm.Filler;
import algorithm.SeedFiller;
import model.*;
import view.Panel;

import javax.swing.*;
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

    private enum Mode {LINE, POLYGON, SEED_FILL, SCAN_LINE}
    private SeedFiller.FillMode currentSeedFillMode = SeedFiller.FillMode.BACKGROUND_LIMITED;

    private Mode currentMode = Mode.LINE;   // line is default mode
    private Polygon currentPolygon = null;  // saves currently drawn polygon

    // default colors
    public static final int COLOR_WHITE = 0xFFFFFF;
    private static final int COLOR_YELLOW = 0xFFFF00;
    private static final int COLOR_GREEN = 0x00FF00;    // for curently drawn polygon
    private static final int COLOR_RED = 0xFF0000;   // filler color for seed fill
    private static final int COLOR_BLUE = 0x0000FF;     // filler color for scan-line
    private static final int COLOR_CYAN = 0x00FFFF; // for rectangular clipping window

    // coordinates of clipping rectangle (for Cohen-Sutherland clipping)
    private static final int CLIP_X_MIN = 200;
    private static final int CLIP_Y_MIN = 150;
    private static final int CLIP_X_MAX = 600;
    private static final int CLIP_Y_MAX = 450;

    private boolean showClipWindow = false;

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
            } else if (obj instanceof ScanLineObject) {
                obj.draw(panel, COLOR_BLUE);
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
        if (showClipWindow) {
            Point p1 = new Point(CLIP_X_MIN, CLIP_Y_MIN);
            Point p2 = new Point(CLIP_X_MAX, CLIP_Y_MIN);
            Point p3 = new Point(CLIP_X_MAX, CLIP_Y_MAX);
            Point p4 = new Point(CLIP_X_MIN, CLIP_Y_MAX);

            new Line(p1, p2).draw(panel, COLOR_CYAN);
            new Line(p2, p3).draw(panel, COLOR_CYAN);
            new Line(p3, p4).draw(panel, COLOR_CYAN);
            new Line(p4, p1).draw(panel, COLOR_CYAN);
        }

        // send BufferedImage to screen
        panel.repaint();
    }

    private void clipLines() {
        CohenSutherlandClipper clipper = new CohenSutherlandClipper(
                CLIP_X_MIN, CLIP_Y_MIN, CLIP_X_MAX, CLIP_Y_MAX
        );

        // create new list for objects to draw after clipping
        List<Drawable> newScene = new ArrayList<>();

        for (Drawable obj : scene) {
            if (obj instanceof Line) {
                Line originalLine = (Line) obj;
                Line clippedLine = clipper.clip(originalLine);

                // if Line was not entirely outside the clipping rectangle
                if (clippedLine != null) {
                    newScene.add(clippedLine);
                }
            } else { // leave polygons and fills
                newScene.add(obj);
            }
        }
        // redraw previous scene to new clipped scene
        scene.clear();
        scene.addAll(newScene);
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
                SeedFillObject fillObj = new SeedFillObject(clickedPoint, COLOR_WHITE, Panel.BACKGROUND_COLOR, currentSeedFillMode);
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
            finalizePolygon();

            // options to choose from
            Object[] options = {
                    "Seed Fill (BACKGROUND_LIMITED)",
                    "Seed Fill (BORDER_LIMITED)",
                    "Scan-line"
            };

            int choice = JOptionPane.showOptionDialog(
                    panel,
                    "Choose filling method:",
                    "Method options",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                currentMode = Mode.SEED_FILL;
                currentSeedFillMode = SeedFiller.FillMode.BACKGROUND_LIMITED;
            } else if (choice == 1) {
                currentMode = Mode.SEED_FILL;
                currentSeedFillMode = SeedFiller.FillMode.BORDER_LIMITED;
            } else if (choice == 2) {
                currentMode = Mode.SCAN_LINE;
                // find last polygon made on scene
                Polygon target = null;
                for (int i = scene.size() - 1; i >=0; i--) {
                    if (scene.get(i) instanceof Polygon) {
                        target = (Polygon) scene.get(i);
                        break;
                    }
                }
                if (target != null) scene.add(new ScanLineObject(target));
                else JOptionPane.showMessageDialog(panel, "No finished polygon on the screen");

            }

            panel.grabFocus();
            redraw();
        }

        if (e.getKeyCode() == KeyEvent.VK_X) {
            finalizePolygon();
            // clipping mode, show clipping window
            showClipWindow = true;
            redraw();

            Object[] options = {
                    "clip lines (Cohen-Sutherland)",
                    "clip objects (Sutherland-Hodgman"
            };

            int choice = JOptionPane.showOptionDialog(
                    panel,
                    "Do you want to:",
                    "Clipping options",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                clipLines();
            } else if (choice == 1) {
                // TODO
            }
            showClipWindow = false;

            panel.grabFocus();
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
            showClipWindow = false;
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

