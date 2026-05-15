package app;

import ui.WindowController;
import javax.swing.SwingUtilities;

public class MinesweeperApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WindowController();
        });
    }
}