package ui;

import game.logic.GameSession;
import game.events.GameObserver;
import game.model.Cell;
import util.GameConfig;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameScreen extends JPanel implements GameObserver {
    private WindowController controller;
    private GameSession session;
    private JButton[][] buttons;
    
    private Timer gameTimer;
    private int secondsElapsed;
    
    private JLabel timerLabel, totalMinesLabel, flagsPlacedLabel, cellsOpenedLabel;
    private JButton glassButton, undoButton;
    private ImageIcon flagIcon;

    public GameScreen(WindowController controller, GameSession session) {
        this.controller = controller;
        this.session = session;
        this.session.addObserver(this); 
        
        java.net.URL flagUrl = getClass().getResource("/img/Minesweeper_flag.png");
        if (flagUrl != null) {
            flagIcon = new ImageIcon(flagUrl);
        } else {
            flagIcon = new ImageIcon("src/img/Minesweeper_flag.png");
        }
        flagIcon = getScaledIcon(flagIcon, 32, 32);
        
        setLayout(new BorderLayout());

        gameTimer = new Timer(1000, e -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed + "s");
        });

        add(createRightPanel(), BorderLayout.EAST);
        add(createGridPanel(), BorderLayout.CENTER);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                session.startNewGame(); 
                gameTimer.stop();
                secondsElapsed = 0;
                timerLabel.setText("Time: 0s");
            }
        });
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        rightPanel.setPreferredSize(new Dimension(220, 0));
        
        timerLabel = new JLabel("Time: 0s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        totalMinesLabel = new JLabel("Total Mines: " + GameConfig.MINES);
        
        flagsPlacedLabel = new JLabel("Flags: 0");
        flagsPlacedLabel.setForeground(new Color(200, 50, 50));
        
        cellsOpenedLabel = new JLabel("Opened: 0/" + GameConfig.SAFE_CELLS);
        
        undoButton = new JButton("Undo Move");
        undoButton.addActionListener(e -> {
            if (!session.undoLastMove()) JOptionPane.showMessageDialog(this, "Cannot undo!");
        });

        JButton restartButton = new JButton("Restart Game");
        restartButton.addActionListener(e -> {
            session.startNewGame();
            gameTimer.stop();
            secondsElapsed = 0;
            timerLabel.setText("Time: 0s");
        });

        JButton menuButton = new JButton("Main Menu");
        menuButton.addActionListener(e -> {
            gameTimer.stop();
            controller.showScreen("StartScreen");
        });

        glassButton = new JButton("🔍 Glass Seer");
        glassButton.addActionListener(e -> session.activateGlassSeer());

        Component[] labels = {timerLabel, totalMinesLabel, flagsPlacedLabel, cellsOpenedLabel, undoButton, restartButton, menuButton, glassButton};
        for (Component c : labels) {
            if (c instanceof JComponent) ((JComponent) c).setAlignmentX(Component.CENTER_ALIGNMENT);
            rightPanel.add(c);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        return rightPanel;
    }

    private JPanel createGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(GameConfig.ROWS, GameConfig.COLS));
        buttons = new JButton[GameConfig.ROWS][GameConfig.COLS];

        for (int r = 0; r < GameConfig.ROWS; r++) {
            for (int c = 0; c < GameConfig.COLS; c++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(45, 45));
                btn.setMargin(new Insets(0,0,0,0));

                btn.setFocusPainted(false);
                final int finalR = r;
                final int finalC = c;
                
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) { 
                        if (SwingUtilities.isRightMouseButton(e)) {
                            session.handleRightClick(finalR, finalC);
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            boolean wasFirstClick = session.isFirstClick();
                            session.handleLeftClick(finalR, finalC);
                            if (wasFirstClick && !session.isFirstClick() && !session.isGameOver()) {
                                gameTimer.start();
                            }
                        }
                    }
                });
                buttons[r][c] = btn;
                gridPanel.add(btn);
            }
        }
        return gridPanel;
    }

    private ImageIcon getScaledIcon(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    @Override
    public void onBoardUpdated() {
        flagsPlacedLabel.setText("Flags: " + session.getFlagsPlaced());
        cellsOpenedLabel.setText("Opened: " + session.getRevealedCount() + "/" + GameConfig.SAFE_CELLS);
        
        if (session.isGlassActive()) glassButton.setText("Select a cell...");
        else glassButton.setText("🔍 Glass Seer (" + session.getGlassCount() + ")");
        glassButton.setEnabled(session.getGlassCount() > 0 && !session.isGlassActive() && !session.isGameOver());
        
        undoButton.setEnabled(session.isUndoAvailable());
        if (session.isFirstClick()) {
            gameTimer.stop();
            secondsElapsed = 0;
            timerLabel.setText("Time: 0s");
        }

        // Repaint Board
        for (int r = 0; r < GameConfig.ROWS; r++) {
            for (int c = 0; c < GameConfig.COLS; c++) {
                Cell cell = session.getCell(r, c);
                JButton btn = buttons[r][c];
                
                btn.setEnabled(!session.isGameOver() && !cell.isRevealed());
                
                if (cell.isRevealed()) {
                    btn.setBackground(Color.LIGHT_GRAY);
                    btn.setIcon(null);
                    if (cell.isMine()) {
                        btn.setText("X");
                        btn.setBackground(Color.RED);
                    } else if (cell.getAdjacentMines() > 0) {
                        btn.setText(String.valueOf(cell.getAdjacentMines()));
                        btn.setForeground(Color.BLUE);
                    } else {
                        btn.setText("");
                    }
                } else if (cell.isFlagged()) {
                    btn.setIcon(flagIcon);
                    btn.setText("");
                    btn.setForeground(Color.RED);
                    btn.setBackground(null);
                } else {
                    btn.setIcon(null);
                    btn.setText("");
                    btn.setBackground(null);
                }
            }
        }
    }

    @Override
    public void onGameWon() {
        gameTimer.stop();
        onBoardUpdated(); 
        JOptionPane.showMessageDialog(this, "You Win! Time: " + secondsElapsed + "s");
    }

    @Override
    public void onGameLost() {
        gameTimer.stop();
        onBoardUpdated(); 
        JOptionPane.showMessageDialog(this, "Boom! You hit a mine.");
    }
}