package com.jooyunghan.my2048;

import android.view.View;

/**
 * Created by jooyung.han on 2014. 5. 13..
 */
public interface CellAnimator {
    void setTransition(View view, Cell cell);

    void setShow(View view, Cell cell);
}
