package com.jooyunghan.my2048;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jooyung.han on 2014. 5. 8..
 */
public class Cell {
    public int value;
    public Position prev;
    public List<Cell> merged = new ArrayList<Cell>();
    public Position position;

    public Cell(int value, Position pos) {
        this.value = value;
        this.position = pos;
    }

    public static Cell merge(Cell cell1, Cell cell2) {
        Cell mergedCell = new Cell(cell1.value * 2, cell1.position);
        mergedCell.merged.add(cell1);
        mergedCell.merged.add(cell2);
        return mergedCell;
    }

    public void prepareMove() {
        prev = position;
        merged.clear();
    }

    public boolean canMerge(Cell other) {
        return other != null && value == other.value && other.prev != null;
    }
}
