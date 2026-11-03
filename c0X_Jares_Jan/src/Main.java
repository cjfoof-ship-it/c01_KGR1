import controller.MainController;
import view.*;

public class Main {
    public static void main(String[] args) {
        // set the resolution and run the window
        Window window = new Window(800, 600);

        // connect panel with mouse and keyboard controller
        MainController controller = new MainController(window.getPanel());
    }
}
