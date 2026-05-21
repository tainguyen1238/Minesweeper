package game.mode;

public class StandardMode implements GameMode {
    @Override
    public String getModeName() { return "Standard Mode"; }
    
    @Override
    public int calculateTimeDisplay(int ticks) { return ticks; } 
    
    @Override
    public boolean isTimeUp(int ticks) { return false; } 
}