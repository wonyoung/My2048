package com.jooyunghan.my2048;

import android.view.View;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by jooyung.han on 2014. 5. 13..
 */
public class ViewCache {
    private ArrayList<View> cache = new ArrayList<View>();
    private int getIndex = 0;

    public View get(Callable<View> factory) throws Exception {
        if (getIndex >= cache.size()) {
            cache.add(factory.call());
        }
        return cache.get(getIndex++);
    }

    public void reset() {
        getIndex = 0;
    }
}
