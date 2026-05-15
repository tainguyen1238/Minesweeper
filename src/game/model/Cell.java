package game.model;

public class Cell {
    private boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int adjacentMines;
    private final int r;
    private final int c;

    public Cell(int r, int c) {
        this.r = r;
        this.c = c;
        this.isMine = false;
        this.isRevealed = false;
        this.isFlagged = false;
        this.adjacentMines = 0;
    }

    // Copy constructor required for the Undo Snapshot feature
    public Cell(Cell other) {
        this.r = other.r;
        this.c = other.c;
        this.isMine = other.isMine;
        this.isRevealed = other.isRevealed;
        this.isFlagged = other.isFlagged;
        this.adjacentMines = other.adjacentMines;
    }

    public int getR() { return r; }
    public int getC() { return c; }
    
    public boolean isMine() { return isMine; }
    public void setMine(boolean mine) { isMine = mine; }
    
    public boolean isRevealed() { return isRevealed; }
    public void reveal() { this.isRevealed = true; }
    
    public boolean isFlagged() { return isFlagged; }
    public void toggleFlag() { this.isFlagged = !this.isFlagged; }
    
    public int getAdjacentMines() { return adjacentMines; }
    public void setAdjacentMines(int count) { this.adjacentMines = count; }
}