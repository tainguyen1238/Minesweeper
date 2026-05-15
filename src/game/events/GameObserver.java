package game.events;

public interface GameObserver {
    void onBoardUpdated();
    void onGameWon();
    void onGameLost();
}