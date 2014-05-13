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
    private boolean end = false;
    private ArrayList<GameState> history = new ArrayList<GameState>();

    public Game(GameView view) {
        this.view = view;
    }

    public void init() {
        board = new Board();
        score = 0;
        end = false;
        history.clear();
        addNewTile();
        addNewTile();
        view.render();
    }

    private void addNewTile() {
        List<Position> positions = board.getEmptyCellPositions();
        Position position = positions.get(rand.nextInt(positions.size()));
        int value = rand.nextInt(10) == 0 ? 4 : 2;

        Cell cell = new Cell(value, position);
        board.put(position, cell);
    }

    public void process(Direction direction) {
        int moveCount = 0;

        List<Cell> cells = board.getCells();
        if (direction.positive()) {
            Collections.reverse(cells);
        }

        for (Cell cell : cells) {
            cell.prepareMove();
            moveCount += moveCell(cell, direction);
        }

        if (moveCount > 0) {
            addNewTile();
            end = !canMove();
            view.render();
        }
    }

    private boolean canMove() {
        return canMoveTo(Direction._X) || canMoveTo(Direction._Y);
    }

    private boolean canMoveTo(Direction direction) {
        List<Cell> cells = board.getCells();
        if (direction.positive()) {
            Collections.reverse(cells);
        }

        for (Cell cell : cells) {
            Position next = cell.position.next(direction);
            Cell nextCell = board.get(next);
            if (board.isEmpty(next) || (nextCell != null && nextCell.value == cell.value))
                return true;
        }
        return false;
    }

    private int moveCell(Cell cell, Direction direction) {
        Position next = cell.position.next(direction);
        Cell nextCell = board.get(next);
        if (board.isEmpty(next)) {
            moveCellTo(cell, next);
            return 1 + moveCell(cell, direction); // move further
        } else if (nextCell != null && cell.canMerge(nextCell)) {
            mergeCellInto(cell, nextCell);
            return 1;
        } else {
            return 0;
        }
    }


    private void moveCellTo(Cell cell, Position position) {
        board.put(cell.position, null);
        cell.position = position;

        board.put(position, cell);
    }


    private void mergeCellInto(Cell cell, Cell nextCell) {
        board.put(cell.position, null);
        cell.position = nextCell.position;

        board.put(cell.position, Cell.merge(cell, nextCell));

        score += cell.value * 2;
    }

    public int getScore() {
        return score;
    }

    public List<Cell> getCells() {
        return board.getCells();
    }

    public boolean isEnd() {
        return end;
    }

    public void undo() {
    }

    public boolean isUndoable() {
        return history.size() > 0;
    }
}