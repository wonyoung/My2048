package com.jooyunghan.my2048;

/**
 * Created by jooyung.han on 2014. 5. 9..
 */
public class Position {
    public final int x;
    public final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position next(Direction direction) {
        return new Position(x + direction.x, y + direction.y);
    }

}
