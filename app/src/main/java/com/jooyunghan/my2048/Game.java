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
        cells[position.x][position.y] = new Cell(value, position.x, position.y);
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
        int moveCount = 0;
        while (true) {
            int nextX = cell.x + direction.x;
            int nextY = cell.y + direction.y;
            if (isEmpty(nextX, nextY)) {
                cells[cell.x][cell.y] = null;
                cell.x = nextX;
                cell.y = nextY;
                cells[nextX][nextY] = cell;
                moveCount++;
            } else if (canMerge(cell, nextX, nextY)) {
                cells[cell.x][cell.y] = null;
                cell.x = nextX;
                cell.y = nextY;
                Cell old = cells[nextX][nextY];
                cells[nextX][nextY] = new Cell(cell.value * 2, nextX, nextY);
                cells[nextX][nextY].merge(cell, old);
                moveCount++;
                break;
            } else {
                break;
            }
        }

        return moveCount;
    }

    private boolean canMerge(Cell cell, int x, int y) {
        return x >= 0 && y >= 0 && x < 4 && y < 4 && cells[x][y] != null &&
                cells[x][y].value == cell.value && cells[x][y].prev != null;
    }

    private boolean isEmpty(int x, int y) {
        return x >= 0 && y >= 0 && x < 4 && y < 4 && cells[x][y] == null;
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