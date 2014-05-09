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

    /**
     * mergeInto into `other` Cell's position
     * @return new merged cell
     */
    public Cell mergeInto(Cell other) {
        position = other.position;
        Cell mergedCell = new Cell(value * 2, position);
        mergedCell.merged.add(this);
        mergedCell.merged.add(other);
        return mergedCell;
    }

    public void prepareMove() {
        prev = position;
        merged.clear();
    }
}
