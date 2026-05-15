package game.logic;

import game.model.Cell;
import game.events.GameObserver;
import util.GameConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class GameSession {
    private Cell[][] board;
    private List<GameObserver> observers;
    
    private Stack<Cell[][]> undoStack;
    private boolean isGameOver;
    private boolean isFirstClick;
    
    private boolean revealMinesOnLoss;
    private boolean canUndoAfterMine;
    private int glassCount;
    private boolean isGlassActive;

    public GameSession() {
        observers = new ArrayList<>();
        undoStack = new Stack<>();
        startNewGame();
    }

    public void configureGame(boolean revealMinesOnLoss, boolean canUndoAfterMine) {
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
        glassCount = 1;
        isGlassActive = false;
        undoStack.clear();
        notifyBoardUpdated();
    }

    private void saveState() {
        Cell[][] snapshot = new Cell[GameConfig.ROWS][GameConfig.COLS];
        for (int r = 0; r < GameConfig.ROWS; r++) {
            for (int c = 0; c < GameConfig.COLS; c++) {
                snapshot[r][c] = new Cell(board[r][c]); // Using deep copy
            }
        }
        undoStack.push(snapshot);
    }

    public boolean undoLastMove() {
        if (isGameOver && !canUndoAfterMine) return false;
        if (!undoStack.isEmpty()) {
            board = undoStack.pop();
            isGameOver = false;
            if (undoStack.isEmpty() && countRevealedCells() == 0) isFirstClick = true;
            notifyBoardUpdated();
            return true;
        }
        return false;
    }
    
    public void activateGlassSeer() {
        if (glassCount > 0 && !isGameOver) {
            isGlassActive = true;
            notifyBoardUpdated();
        }
    }

    public void handleRightClick(int r, int c) {
        if (isGameOver || board[r][c].isRevealed()) return;
        saveState();
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

        saveState();

        if (cell.isFlagged()) {
            undoStack.pop();
            return;
        }

        if (isFirstClick) {
            placeMines(r, c);
            calculateNumbers();
            isFirstClick = false;
        }

        if (isGlassActive) {
            isGlassActive = false;
            glassCount--;
            cell.reveal();
            notifyBoardUpdated();
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
            saveState();
            boolean hitMine = false;
            
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int nr = r + i, nc = c + j;
                    if (isValid(nr, nc) && !board[nr][nc].isRevealed() && !board[nr][nc].isFlagged()) {
                        board[nr][nc].reveal();
                        if (board[nr][nc].isMine()) {
                            hitMine = true;
                        } else if (board[nr][nc].getAdjacentMines() == 0) {
                            // Recursively reveal neighbors of the newly opened blank cell
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

    private void placeMines(int firstR, int firstC) {
        Random rand = new Random();
        int minesPlaced = 0;
        while (minesPlaced < GameConfig.MINES) {
            int r = rand.nextInt(GameConfig.ROWS);
            int c = rand.nextInt(GameConfig.COLS);
            if (Math.abs(r - firstR) <= 1 && Math.abs(c - firstC) <= 1) continue;
            if (!board[r][c].isMine()) {
                board[r][c].setMine(true);
                minesPlaced++;
            }
        }
    }

    private void calculateNumbers() {
        for (int r = 0; r < GameConfig.ROWS; r++) {
            for (int c = 0; c < GameConfig.COLS; c++) {
                if (board[r][c].isMine()) continue;
                int count = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int nr = r + i, nc = c + j;
                        if (isValid(nr, nc) && board[nr][nc].isMine()) count++;
                    }
                }
                board[r][c].setAdjacentMines(count);
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
                if (isValid(nr, nc) && board[nr][nc].isFlagged()) count++;
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

    // Getters for UI
    public Cell getCell(int r, int c) { return board[r][c]; }
    public boolean isGameOver() { return isGameOver; }
    public int getGlassCount() { return glassCount; }
    public boolean isGlassActive() { return isGlassActive; }
    public int getRevealedCount() { return countRevealedCells(); }
    public boolean isFirstClick() { return isFirstClick; }
    public boolean isUndoAvailable() { return !undoStack.isEmpty(); }
    public int getFlagsPlaced() {
        int flags = 0;
        for (int r = 0; r < GameConfig.ROWS; r++) {
            for (int c = 0; c < GameConfig.COLS; c++) if (board[r][c].isFlagged()) flags++;
        }
        return flags;
    }
}