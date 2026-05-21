package ui;

import game.logic.GameSession;
import javax.swing.*;
import java.awt.*;

public class WindowController extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private GameSession session;

    public WindowController() {
        session = new GameSession();
        
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        
        mainContainer.add(new StartScreen(this, session), "StartScreen");
        mainContainer.add(new GameScreen(this, session), "GameScreen");
        
        add(mainContainer);
        showScreen("StartScreen");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void showScreen(String screenName) {
        cardLayout.show(mainContainer, screenName);
        pack();
        setLocationRelativeTo(null);
    }
}