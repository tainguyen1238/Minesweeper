package game.logic;

import game.model.Cell;
import util.GameConfig;
import java.util.Stack;

public class HistoryManager {
    private Stack<Cell[][]> undoStack;

    public HistoryManager() {
        this.undoStack = new Stack<>();
    }

    public void saveState(Cell[][] board) {
        Cell[][] snapshot = new Cell[GameConfig.ROWS][GameConfig.COLS];
        for (int r = 0; r < GameConfig.ROWS; r++) {
            for (int c = 0; c < GameConfig.COLS; c++) {
                snapshot[r][c] = new Cell(board[r][c]); // Deep copy
            }
        }
        undoStack.push(snapshot);
    }

    public Cell[][] undo(Cell[][] currentBoard) {
        if (!undoStack.isEmpty()) {
            return undoStack.pop();
        }
        return currentBoard;
    }

    public void clear() {
        undoStack.clear();
    }

    public boolean isUndoAvailable() {
        return !undoStack.isEmpty();
    }
}