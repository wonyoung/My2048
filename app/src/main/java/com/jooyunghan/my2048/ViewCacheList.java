package com.jooyunghan.my2048;

import java.util.ArrayList;

/**
 * Created by jooyung.han on 2014. 5. 13..
 */
public class ViewCacheList {
    private ArrayList<ViewCache> array = new ArrayList<ViewCache>();

    public ViewCache get(int index) {
        while (array.size() <= index) {
            array.add(new ViewCache());
        }
        return array.get(index);
    }

    public void reset() {
        for (ViewCache viewCache: array) {
            viewCache.reset();
        }
    }
}
