package game.mode;

public interface GameMode {
    String getModeName();
    int calculateTimeDisplay(int ticksElapsed);
    boolean isTimeUp(int ticksElapsed);
}