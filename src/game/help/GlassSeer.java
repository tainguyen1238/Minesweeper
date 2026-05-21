package game.help;

import game.logic.GameSession;
import game.model.Cell;

public class GlassSeer implements GameHelp {
    
    @Override
    public String getName() {
        return "Glass Seer";
    }

    @Override
    public boolean execute(GameSession session, int r, int c) {
        Cell cell = session.getCell(r, c);
        
        if (!cell.isRevealed() && !cell.isFlagged()) {
            cell.reveal();
            return true; 
        }
        return false;
    }
}