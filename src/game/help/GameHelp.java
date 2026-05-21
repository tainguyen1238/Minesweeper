package game.help;

import game.logic.GameSession;

public interface GameHelp {
    String getName();
    boolean execute(GameSession session, int r, int c); 
}