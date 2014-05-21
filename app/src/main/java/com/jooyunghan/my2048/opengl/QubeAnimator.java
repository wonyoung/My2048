package com.jooyunghan.my2048.opengl;

import com.jooyunghan.my2048.Cell;

/**
 * Created by wonyoung.jang on 2014-05-21.
 */
public interface QubeAnimator {
    void setTransition(Qube qube, Cell cell);

    void setShow(Qube qube, Cell cell);

    void setShowAndTransition(Qube qube, Cell cell);
}
