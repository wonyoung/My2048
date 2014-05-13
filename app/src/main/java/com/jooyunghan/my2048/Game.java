package com.jooyunghan.my2048;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
    private final GameView view;
    private Board board;
    private Random rand = new Random();
    private int score;
    private ArrayList<GameState> history = new ArrayList<GameState>();

    public Game(GameView view) {
        this.view = view;
    }

    public void init() {
        board = new Board();
        score = 0;
        history.clear();
        addNewTile();
        addNewTile();
        view.render();
    }

    private void addNewTile() {
        List<Position> positions = board.getEmptyCellPositions();
        Position position = positions.get(rand.nextInt(positions.size()));
        int value = rand.nextInt(10) == 0 ? 4 : 2;

        Cell cell = new Cell(value, position, null);
        board.put(position, cell);
    }

    public void process(Direction direction) {
        GameState state = new GameState(board.copy(), score);

        board.prepareMove(); // change board states

        List<Cell> cells = board.getCells();
        if (direction.positive()) {
            Collections.reverse(cells);
        }

        int moveCount = 0;
        for (Cell cell : cells) {
            moveCount += moveCell(cell, direction);
        }

        if (moveCount > 0) {
            history.add(state);

            addNewTile();
            view.render();
        } else {
            board = state.board; // reset to the state before prepareMove
        }
    }

    /**
     *
     * @param cell
     * @param direction
     * @return the number of moves
     */
    private int moveCell(Cell cell, Direction direction) {
        Position next = cell.position.next(direction);
        Cell nextCell = board.get(next);
        if (board.isEmpty(next)) {
            Cell cellMoved = board.moveCellTo(cell, next);
            return 1 + moveCell(cellMoved, direction); // move further
        } else if (nextCell != null && cell.canMerge(nextCell)) {
            addScore(cell.value * 2);
            board.mergeCellInto(cell, nextCell);
            return 1;
        } else {
            return 0;
        }
    }

    private void addScore(int add) {
        score += add;
    }


    public int getScore() {
        return score;
    }

    public List<Cell> getCells() {
        return board.getCells();
    }

    public void undo() {
        GameState last = history.remove(history.size() - 1);
        view.renderUndo();
        this.score = last.score;
        this.board = last.board;
    }

    public boolean isUndoable() {
        return history.size() > 0;
    }
}