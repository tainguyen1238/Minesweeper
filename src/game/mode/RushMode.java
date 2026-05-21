package game.mode;

import util.GameConfig;

public class RushMode implements GameMode {
    private final int TIME_LIMIT = GameConfig.TIME_LIMIT; 

    @Override
    public String getModeName() { return "Rush Mode"; }
    
    @Override
    public int calculateTimeDisplay(int ticks) { 
        return Math.max(0, TIME_LIMIT - ticks); 
    }
    
    @Override
    public boolean isTimeUp(int ticks) { 
        return ticks >= TIME_LIMIT; 
    }
}