package com.jooyunghan.my2048.opengl;

import com.jooyunghan.my2048.Cell;

/**
 * Created by wonyoung.jang on 2014-05-21.
 */
public interface CubeAnimator {
    void setTransition(Cube cube, Cell cell);

    void setShow(Cube cube, Cell cell);

    void setShowAndTransition(Cube cube, Cell cell);
}
