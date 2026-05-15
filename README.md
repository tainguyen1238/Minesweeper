# Minesweeper Game

A classic Minesweeper game implemented in Java using Swing GUI framework to showcase principles of Algorithms and Data Structures. Namely: OOP, arrays, queues, stacks, list and recursion.

## Features

- **Classic Gameplay**: Left-click to reveal cells, right-click to flag suspected mines
- **Configurable Options**:
  - Reveal all mines when you hit one (optional)
  - Allow undo after hitting a mine (optional)
- **Special Features**:
  - **Glass Seer**: Reveal one cell without triggering a mine explosion (limited uses)
  - **Chording**: Click on a numbered cell to reveal adjacent unflagged cells if the correct number of flags are placed
  - **Undo System**: Undo your last move with full state restoration
- **Game Statistics**:
  - Timer
  - Flag counter
  - Cells opened counter
- **UI Controls**:
  - Restart game
  - Return to main menu
  - Quit game

## How to Run

### Prerequisites
- Java 8 or higher installed

### Compilation
```bash
cd path/to/project
javac -cp src src/app/MinesweeperApp.java
```

### Execution
```bash
java -cp src app.MinesweeperApp
```

## Game Rules

1. The board contains hidden mines and numbers indicating nearby mines.
2. Left-click to reveal a cell.
3. Right-click to flag/unflag a cell you suspect contains a mine.
4. If you reveal a mine, the game ends (unless undo is enabled).
5. Win by revealing all non-mine cells.
6. Use the Glass Seer to safely reveal one cell.
7. Use chording on numbered cells to quickly reveal safe areas.

## Controls

- **Left Click**: Reveal cell / Activate chording on numbered cells
- **Right Click**: Toggle flag on cell
- **Glass Seer Button**: Activate special reveal mode
- **Undo Button**: Undo last move (if available)
- **Restart**: Start new game
- **Main Menu**: Return to start screen
- **Quit**: Exit application

## Project Structure

```
src/
├── app/
│   └── MinesweeperApp.java          # Main application entry point
├── ui/
│   ├── WindowController.java        # Main window and screen management
│   ├── StartScreen.java             # Game configuration screen
│   └── GameScreen.java              # Main game interface
├── game/
│   ├── logic/
│   │   └── GameSession.java         # Game logic and state management
│   ├── model/
│   │   └── Cell.java                # Cell data model
│   └── events/
│       └── GameObserver.java        # Observer pattern interface
└── util/
    └── GameConfig.java              # Game configuration constants
```

## Configuration

Game settings can be modified in `src/util/GameConfig.java`:
- `ROWS`: Number of rows (default: 15)
- `COLS`: Number of columns (default: 20)
- `MINES`: Number of mines (default: 45)

## License
- MIT

## Technologies Used

- Java Swing for GUI
- Observer Pattern for UI updates
- Stack-based undo system with deep copy snapshots</content>
