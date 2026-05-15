package ui;

import game.logic.GameSession;
import javax.swing.*;
import java.awt.*;

public class StartScreen extends JPanel {
    
    public StartScreen(WindowController controller, GameSession session) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(40, 44, 52));

        JLabel titleLabel = new JLabel("Minesweeper");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JCheckBox revealMinesToggle = new JCheckBox("Reveal bombs on hit", true);
        JCheckBox canUndoToggle = new JCheckBox("Allow Undo after hitting mine", true);
        styleCheckBox(revealMinesToggle);
        styleCheckBox(canUndoToggle);

        JButton playButton = new JButton("START GAME");
        playButton.setFont(new Font("Arial", Font.BOLD, 20));
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.addActionListener(e -> {
            session.configureGame(revealMinesToggle.isSelected(), canUndoToggle.isSelected());
            controller.showScreen("GameScreen");
        });

        JButton quitButton = new JButton("QUIT");
        quitButton.setFont(new Font("Arial", Font.BOLD, 20));
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.addActionListener(e -> System.exit(0));

        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(revealMinesToggle);
        add(canUndoToggle);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(playButton);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(quitButton);
        add(Box.createVerticalGlue());
    }

    private void styleCheckBox(JCheckBox cb) {
        cb.setFont(new Font("Arial", Font.ITALIC, 16));
        cb.setForeground(Color.LIGHT_GRAY);
        cb.setOpaque(false);
        cb.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}