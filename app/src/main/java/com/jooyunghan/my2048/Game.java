package com.jooyunghan.my2048;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
    private final GameView view;
    private Cell[][] cells;
    private Random rand = new Random();

    public Game(GameView view) {
        this.view = view;
    }

    public void init() {
        this.cells = new Cell[4][4];
        addNewTile();
        addNewTile();
        view.render();
    }

    private void addNewTile() {
        List<Position> positions = getEmptyCellPositions();
        Position position = positions.get(rand.nextInt(positions.size()));
        int value = rand.nextInt(10) == 0 ? 4 : 2;
        cells[position.x][position.y] = new Cell(value, position);
    }

    public void process(Direction direction) {
        int moveCount = 0;

        List<Cell> cells = getNotNullCells();
        if (direction.positive()) {
            Collections.reverse(cells);
        }

        for (Cell cell : cells) {
            cell.prepareMove();
            moveCount += moveCell(cell, direction);
        }

        if (moveCount > 0) {
            addNewTile();
            view.render();
        }
    }

    private int moveCell(Cell cell, Direction direction) {
        Position next = cell.position.next(direction);
        if (isEmpty(next)) {
            moveCellTo(cell, next);
            return 1 + moveCell(cell, direction);
        } else if (canMerge(cell, next)) {
            mergeCell(cell, next);
            return 1;
        } else {
            return 0;
        }
    }

    private void mergeCell(Cell cell, Position next) {
        cells[cell.position.x][cell.position.y] = null;
        cells[next.x][next.y] = cell.mergeInto(cells[next.x][next.y]);
    }

    private void moveCellTo(Cell cell, Position next) {
        cells[cell.position.x][cell.position.y] = null;
        cell.position = next;
        cells[next.x][next.y] = cell;
    }

    private boolean canMerge(Cell cell, Position p) {
        return p.x >= 0 && p.y >= 0 && p.x < 4 && p.y < 4 && cells[p.x][p.y] != null &&
                cells[p.x][p.y].value == cell.value && cells[p.x][p.y].prev != null;
    }

    private boolean isEmpty(Position p) {
        return p.x >= 0 && p.y >= 0 && p.x < 4 && p.y < 4 && cells[p.x][p.y] == null;
    }


    private List<Position> getEmptyCellPositions() {
        final List<Position> positions = new ArrayList<Position>();
        forEachCells(new CellHandler() {
            @Override
            public void handle(int x, int y, Cell cell) {
                if (cell == null) {
                    positions.add(new Position(x, y));
                }
            }
        });
        return positions;
    }

    public List<Cell> getNotNullCells() {
        final List<Cell> cells = new ArrayList<Cell>();
        forEachCells(new CellHandler() {
            @Override
            public void handle(int x, int y, Cell cell) {
                if (cell != null) {
                    cells.add(cell);
                }
            }
        });
        return cells;
    }

    private void forEachCells(CellHandler handler) {
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                handler.handle(x, y, cells[x][y]);
            }
        }
    }

}