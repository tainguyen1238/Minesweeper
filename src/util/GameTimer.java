package util;

import javax.swing.Timer;
import java.util.function.Consumer;

public class GameTimer {
    private Timer timer;
    private int secondsElapsed;

    public GameTimer(Consumer<Integer> onTick) {
        this.secondsElapsed = 0;
        this.timer = new Timer(1000, e -> {
            secondsElapsed++;
            onTick.accept(secondsElapsed);
        });
    }

    public void start() { if (!timer.isRunning()) timer.start(); }
    public void stop() { timer.stop(); }
    public void reset() {
        stop();
        secondsElapsed = 0;
    }
    public boolean isRunning() { return timer.isRunning(); }
    public int getSecondsElapsed() { return secondsElapsed; }
}