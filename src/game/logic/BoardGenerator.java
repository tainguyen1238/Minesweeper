package game.logic;

import game.model.Cell;
import util.GameConfig;
import java.util.Random;

public class BoardGenerator {

    public void populateBoard(Cell[][] board, int firstR, int firstC) {
        placeMines(board, firstR, firstC);
        calculateNumbers(board);
    }

    private void placeMines(Cell[][] board, int firstR, int firstC) {
        Random rand = new Random();
        int minesPlaced = 0;
        while (minesPlaced < GameConfig.MINES) {
            int r = rand.nextInt(GameConfig.ROWS);
            int c = rand.nextInt(GameConfig.COLS);
            
            // Prevent mine generation within 1 cell of the initial click
            if (Math.abs(r - firstR) <= 1 && Math.abs(c - firstC) <= 1) continue;
            
            if (!board[r][c].isMine()) {
                board[r][c].setMine(true);
                minesPlaced++;
            }
        }
    }

    private void calculateNumbers(Cell[][] board) {
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

    private boolean isValid(int r, int c) {
        return r >= 0 && r < GameConfig.ROWS && c >= 0 && c < GameConfig.COLS;
    }
}