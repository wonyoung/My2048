package com.jooyunghan.my2048;

import java.util.ArrayList;

/**
 * Created by jooyung.han on 2014. 5. 13..
 */
public class GameState {
    public final Board board;
    public final int score;

    public GameState(Board copy, int score) {
        this.board = copy;
        this.score = score;
    }

}
