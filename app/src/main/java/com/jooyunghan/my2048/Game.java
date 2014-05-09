package com.jooyunghan.my2048;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
    private final GameView view;
    private Board board;
    private Random rand = new Random();

    public Game(GameView view) {
        this.view = view;
    }

    public void init() {
        this.board = new Board();
        addNewTile();
        addNewTile();
        view.render(board.getCells());
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
            view.render(board.getCells());
        }
    }

    private int moveCell(Cell cell, Direction direction) {
        Position next = cell.position.next(direction);
        if (board.isEmpty(next)) {
            moveCellTo(cell, next);
            return 1 + moveCell(cell, direction); // move further
        } else if (cell.canMerge(board.get(next))) {
            mergeCellInto(cell, next);
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


    private void mergeCellInto(Cell cell, Position position) {
        board.put(cell.position, null);

        cell.position = position;

        board.put(position, Cell.merge(cell, board.get(position)));
    }




}