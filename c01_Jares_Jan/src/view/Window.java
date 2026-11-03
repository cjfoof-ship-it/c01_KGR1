package view;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private final Panel panel;

    public Window(int width, int height) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("PROJECT1");
        setVisible(true);

        panel = new Panel(width, height);
        add(panel);
        pack();

        panel.setFocusable(true);   // panel becomes focusable
        panel.grabFocus();          // reacts immediately when program is run. No need to click first on it
    }

    public Panel getPanel() {
        return panel;
    }
}
