package com.jooyunghan.my2048;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jooyung.han on 2014. 5. 8..
 */
public class Cell {
    public int value;
    public int y;
    public int x;
    public Position prev;
    public List<Cell> merged = new ArrayList<Cell>();

    public Cell(int value, int x, int y) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

    public void merge(Cell old1, Cell old2) {
        this.merged.add(old1);
        this.merged.add(old2);
    }

    public void prepareMove() {
        prev = new Position(x, y);
        merged.clear();
    }
}
