package com.jooyunghan.my2048;

/**
* Created by jooyung.han on 2014. 5. 9..
*/
enum Direction {
    X(1,0), _X(-1,0), Y(0,1), _Y(0,-1);
    public final int x;
    public final int y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean positive() {
        return x + y > 0;
    }
}
