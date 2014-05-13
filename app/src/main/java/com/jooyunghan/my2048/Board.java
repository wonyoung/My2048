package com.jooyunghan.my2048;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private Cell[][] cells;

    public Board() {
        cells = new Cell[4][4];
    }

    public void put(Position position, Cell cell) {
        cells[position.x][position.y] = cell;
    }

    public Cell get(Position p) {
        if (p.x >= 0 && p.y >= 0 && p.x < 4 && p.y < 4)
            return cells[p.x][p.y];
        return null;
    }

    public boolean isEmpty(Position p) {
        return p.x >= 0 && p.y >= 0 && p.x < 4 && p.y < 4 && cells[p.x][p.y] == null;
    }

    private void forEachCells(CellHandler handler) {
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                handler.handle(x, y, cells[x][y]);
            }
        }
    }

    public List<Position> getEmptyCellPositions() {
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

    public List<Cell>  getCells() {
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

    public void prepareMove() {
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                Cell cell = cells[x][y];
                if (cell == null)
                    continue;
                cells[x][y] = new Cell(cell.value, cell.position, cell.position);
            }
        }
    }

    public Board copy() {
        Board copy = new Board();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                copy.cells[x][y] = cells[x][y];
            }
        }
        return copy;
    }
}