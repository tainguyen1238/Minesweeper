package ui;

import game.logic.GameSession;
import javax.swing.*;
import java.awt.*;

public class StartScreen extends JPanel {
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color colorTop = Color.decode("#7e43e6");    
        Color colorBottom = Color.decode("#0b2965"); 
        
        GradientPaint gradient = new GradientPaint(0, 0, colorTop, 0, getHeight(), colorBottom);
        
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    public StartScreen(WindowController controller, GameSession session) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Minesweeper");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JCheckBox revealMinesToggle = new JCheckBox("Reveal bombs on hit", true);
        JCheckBox canUndoToggle = new JCheckBox("Allow Undo after hitting mine", true);
        styleCheckBox(revealMinesToggle);
        styleCheckBox(canUndoToggle);

        Dimension btnSize = new Dimension(250, 50);

        JButton stdBtn = new JButton("Standard Mode");
        stdBtn.setFont(new Font("Arial", Font.BOLD, 18));
        stdBtn.setPreferredSize(btnSize);
        stdBtn.setMaximumSize(btnSize);
        stdBtn.setFocusPainted(false);
        stdBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        stdBtn.addActionListener(e -> {
            session.configureGame(new game.mode.StandardMode(), revealMinesToggle.isSelected(), canUndoToggle.isSelected());
            controller.showScreen("GameScreen");
        });

        JButton rushBtn = new JButton("Rush Mode");
        rushBtn.setFont(new Font("Arial", Font.BOLD, 18));
        rushBtn.setPreferredSize(btnSize);
        rushBtn.setMaximumSize(btnSize);
        rushBtn.setFocusPainted(false);
        rushBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        rushBtn.addActionListener(e -> {
            session.configureGame(new game.mode.RushMode(), revealMinesToggle.isSelected(), canUndoToggle.isSelected());
            controller.showScreen("GameScreen");
        });

        JButton quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Arial", Font.BOLD, 18));
        quitButton.setPreferredSize(btnSize);
        quitButton.setMaximumSize(btnSize);
        quitButton.setFocusPainted(false);
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.addActionListener(e -> System.exit(0));

        add(Box.createVerticalGlue()); 
        
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        
        add(revealMinesToggle);
        add(canUndoToggle);
        add(Box.createRigidArea(new Dimension(0, 30)));
        
        add(stdBtn);
        add(Box.createRigidArea(new Dimension(0, 15)));
        
        add(rushBtn);
        add(Box.createRigidArea(new Dimension(0, 15)));
        
        add(quitButton);
        
        add(Box.createVerticalGlue()); // Pushes everything up to center
    }

    private void styleCheckBox(JCheckBox cb) {
        cb.setFont(new Font("Arial", Font.ITALIC, 16));
        cb.setForeground(Color.LIGHT_GRAY);
        cb.setOpaque(false);
        cb.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}