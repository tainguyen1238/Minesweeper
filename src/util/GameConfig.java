package util;

public class GameConfig {
    private GameConfig() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final int ROWS = 15;
    public static final int COLS = 20;
    public static final int MINES = 45;
    public static final int INITIAL_GLASS_COUNT = 10;
    public static final int SAFE_CELLS = (ROWS * COLS) - MINES;

    public static final int TIME_LIMIT = 120;
}