package com.jooyunghan.my2048;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jooyung.han on 2014. 5. 8..
 */
public class Cell {
    public final int value;
    public final Position previous;
    public final Cell[] merged;
    public final Position position;

    public Cell(int value, Position position, Position previous, Cell ...merged) {
        this.value = value;
        this.position = position;
        this.previous = previous;
        this.merged = merged;
    }

    public static Cell merge(Cell cell1, Cell cell2) {
        return new Cell(cell1.value * 2, cell2.position, null, cell1.moveTo(cell2.position), cell2);
    }

    public boolean canMerge(Cell other) {
        return value == other.value && other.previous != null;
    }

    public Cell moveTo(Position position) {
        return new Cell(value, position, this.previous);
    }

}
