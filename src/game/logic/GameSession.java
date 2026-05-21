package game.logic;

import game.model.Cell;
import game.events.GameObserver;
import util.GameConfig;
import game.help.GameHelp;
import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private Cell[][] board;
    private List<GameObserver> observers;
    
    private HistoryManager historyManager;
    private BoardGenerator boardGenerator;
    
    private game.mode.GameMode gameMode;
    private boolean isGameOver;
    private boolean isFirstClick;
    
    private boolean revealMinesOnLoss;
    private boolean canUndoAfterMine;
    private GameHelp activeHelp;
    private int helpUsesLeft;

    public GameSession() {
        observers = new ArrayList<>();
        historyManager = new HistoryManager();
        boardGenerator = new BoardGenerator();
        startNewGame();
    }

    public void configureGame(game.mode.GameMode mode, boolean revealMinesOnLoss, boolean canUndoAfterMine) {
        this.gameMode = mode;
        this.revealMinesOnLoss = revealMinesOnLoss;
        this.canUndoAfterMine = canUndoAfterMine;
    }

    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    private void notifyBoardUpdated() {
        for (GameObserver obs : observers) obs.onBoardUpdated();
    }

    public void startNewGame() {
        board = new Cell[GameConfig.ROWS][GameConfig.COLS];
        for (int r = 0; r < GameConfig.ROWS; r++) {
            for (int c = 0; c < GameConfig.COLS; c++) {
                board[r][c] = new Cell(r, c);
            }
        }
        isFirstClick = true;
        isGameOver = false;
        helpUsesLeft = GameConfig.INITIAL_GLASS_COUNT;
        activeHelp = null;
        historyManager.clear();
        notifyBoardUpdated();
    }

    public boolean undoLastMove() {
        if (isGameOver && !canUndoAfterMine) return false;
        if (historyManager.isUndoAvailable()) {
            board = historyManager.undo(board);
            isGameOver = false;
            if (!historyManager.isUndoAvailable() && countRevealedCells() == 0) isFirstClick = true;
            notifyBoardUpdated();
            return true;
        }
        return false;
    }
    
    public void toggleHelp(GameHelp help) {
        if (isGameOver) return;
        
        if (activeHelp != null && activeHelp.getClass() == help.getClass()) {
            activeHelp = null;
        } else if (helpUsesLeft > 0) {
            activeHelp = help;
        }
        notifyBoardUpdated();
    }
    
    public void timeRanOut() {
        if (isGameOver) return;
        isGameOver = true;
        if (revealMinesOnLoss) revealAllMines();
        for (GameObserver obs : observers) obs.onGameLost();
    }

    public void handleRightClick(int r, int c) {
        if (isGameOver || board[r][c].isRevealed()) return;
        historyManager.saveState(board);
        board[r][c].toggleFlag();
        notifyBoardUpdated();
    }

    public void handleLeftClick(int r, int c) {
        if (isGameOver) return;
        Cell cell = board[r][c];

        if (cell.isRevealed() && cell.getAdjacentMines() > 0) {
            executeChording(r, c);
            return;
        }
        if (cell.isRevealed()) return;

        historyManager.saveState(board);

        if (cell.isFlagged()) {
            // Cancel snapshot tracking if cell was flagged
            historyManager.undo(board); 
            return;
        }

        if (isFirstClick) {
            boardGenerator.populateBoard(board, r, c);
            isFirstClick = false;
        }

        if (activeHelp != null) {
            boolean success = activeHelp.execute(this, r, c);
            if (success) {
                helpUsesLeft--;
                activeHelp = null; 
                notifyBoardUpdated();
            }
            return; 
        }

        revealCell(r, c);
    }

    private void revealCell(int r, int c) {
        if (!isValid(r, c) || board[r][c].isRevealed() || board[r][c].isFlagged()) return;
        
        Cell cell = board[r][c];
        cell.reveal();

        if (cell.isMine()) {
            isGameOver = true;
            if (revealMinesOnLoss) revealAllMines();
            for (GameObserver obs : observers) obs.onGameLost();
            return;
        }

        if (cell.getAdjacentMines() == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    revealCell(r + i, c + j);
                }
            }
        }

        if (!isGameOver) {
            if (checkWinCondition()) {
                isGameOver = true;
                for (GameObserver obs : observers) obs.onGameWon();
            } else {
                notifyBoardUpdated();
            }
        }
    }

    private void executeChording(int r, int c) {
        Cell cell = board[r][c];
        int flagCount = countAdjacentFlags(r, c);

        if (flagCount == cell.getAdjacentMines()) {
            historyManager.saveState(board);
            boolean hitMine = false;
            
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int nr = r + i, nc = c + j;
                    if (isValid(nr, nc) && !board[nr][nc].isRevealed() && !board[nr][nc].isFlagged()) {
                        board[nr][nc].reveal();
                        if (board[nr][nc].isMine()) {
                            hitMine = true;
                        } else if (board[nr][nc].getAdjacentMines() == 0) {
                            for (int y = -1; y <= 1; y++) {
                                for (int x = -1; x <= 1; x++) {
                                    revealCell(nr + y, nc + x);
                                }
                            }
                        }
                    }
                }
            }
            
            if (hitMine) {
                isGameOver = true;
                if (revealMinesOnLoss) revealAllMines();
                for (GameObserver obs : observers) obs.onGameLost();
            } else if (checkWinCondition()) {
                isGameOver = true;
                for (GameObserver obs : observers) obs.onGameWon();
            } else {
                notifyBoardUpdated();
            }
        }
    }
    
    private void revealAllMines() {
        for (int r = 0; r < GameConfig.ROWS; r++) {
            for (int c = 0; c < GameConfig.COLS; c++) {
                if (board[r][c].isMine()) board[r][c].reveal();
            }
        }
    }

    private int countAdjacentFlags(int r, int c) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nr = r + i, nc = c + j;
                if (isValid(nr, nc)) {
                Cell neighbor = board[nr][nc];

                if (neighbor.isFlagged() || (neighbor.isRevealed() && neighbor.isMine())) {
                    count++;
                }
            }
            }
        }
        return count;
    }

    private boolean checkWinCondition() { return countRevealedCells() == GameConfig.SAFE_CELLS; }
    
    private int countRevealedCells() {
        int count = 0;
        for (int r = 0; r < GameConfig.ROWS; r++) {
            for (int c = 0; c < GameConfig.COLS; c++) {
                if (board[r][c].isRevealed() && !board[r][c].isMine()) count++;
            }
        }
        return count;
    }
    
    private boolean isValid(int r, int c) {
        return r >= 0 && r < GameConfig.ROWS && c >= 0 && c < GameConfig.COLS;
    }

    // Getters
    public Cell getCell(int r, int c) { return board[r][c]; }
    public boolean isGameOver() { return isGameOver; }
    public int getHelpUsesLeft() { return helpUsesLeft; }
    public boolean isAssistActive() { return activeHelp != null; }
    public String getActiveAssistName() { return activeHelp != null ? activeHelp.getName() : ""; }
    public int getRevealedCount() { return countRevealedCells(); }
    public boolean isFirstClick() { return isFirstClick; }
    public boolean isUndoAvailable() { return historyManager.isUndoAvailable(); }
    public game.mode.GameMode getGameMode() { return gameMode; }
    public int getFlagsPlaced() {
        int flags = 0;
        for (int r = 0; r < GameConfig.ROWS; r++) {
            for (int c = 0; c < GameConfig.COLS; c++) if (board[r][c].isFlagged()) flags++;
        }
        return flags;
    }
}