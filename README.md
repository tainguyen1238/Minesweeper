# Minesweeper Game

A classic Minesweeper game implemented in Java with a Swing-based GUI. This project demonstrates object-oriented design and common data structure techniques such as arrays, stacks, queues, lists, and recursion.

## Features

- **Classic Minesweeper gameplay** with reveal and flag mechanics
- **Glass Seer**: safely reveal one hidden cell without triggering a mine
- **Chording**: open adjacent cells when the correct number of flags is placed
- **Undo support**: roll back the last move and restore game state
- **Optional mine reveal** on explosion and undo after hitting a mine
- **Game statistics** including timer, flag count, and opened cell count
- **UI controls** for restart, return to main menu, and quit

## Getting Started

### Prerequisites
- Java 8 or higher installed

### Compile
From the project root directory:

```bash
cd c:\Users\nhung\Downloads\DSA\Project\Minesweeper-Project-test-changes
javac -cp src src/app/MinesweeperApp.java
```

### Run

```bash
java -cp src app.MinesweeperApp
```

> If you build from a different folder, make sure the `src` directory is included on the classpath.

## How to Play

1. Left-click a cell to reveal it.
2. Right-click a cell to toggle a flag.
3. If you reveal a mine, the game ends unless undo is enabled.
4. Win by revealing all non-mine cells.
5. Use the Glass Seer button to reveal one safe cell.
6. Use chording on a revealed numbered cell to open neighboring unflagged cells.

## Controls

- **Left Click**: reveal a cell or chord when clicking a number
- **Right Click**: flag / unflag a cell
- **Glass Seer**: activate the special safe reveal mode
- **Undo**: revert the previous move, if available
- **Restart**: start a new game
- **Main Menu**: return to the start screen
- **Quit**: close the application

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
│   ├── events/
│   │   └── GameObserver.java        # Observer pattern interface
│   ├── logic/
│   │   ├── BoardGenerator.java      # Board and mine placement logic
│   │   ├── GameSession.java         # Game logic and state management
│   │   └── HistoryManager.java      # Undo and history support
│   ├── mode/
│   │   ├── GameMode.java            # Base game mode definition
│   │   ├── RushMode.java            # Rush mode rules
│   │   └── StandardMode.java        # Standard mode rules
│   └── model/
│       └── Cell.java                # Cell data model
└── util/
    ├── GameConfig.java              # Game configuration constants
    └── GameTimer.java               # Timer for game statistics
```

## Configuration

Adjust game settings in `src/util/GameConfig.java`:

- `ROWS`: number of rows (default: 15)
- `COLS`: number of columns (default: 20)
- `MINES`: number of mines (default: 45)
- Additional configuration values may be defined in `GameConfig` for mode and UI behavior

## License

MIT

## Technologies Used

- Java Swing for the graphical user interface
- Observer pattern for UI updates
- Stack-based history manager for undo functionality
- Recursive and iterative board generation logic</content>
