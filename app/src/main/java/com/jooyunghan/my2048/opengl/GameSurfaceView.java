package com.jooyunghan.my2048.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by wonyoung.jang on 2014-05-16.
 */
public class GameSurfaceView extends GLSurfaceView {

    private GameRenderer renderer;

    public GameSurfaceView(Context context, GameRenderer renderer) {
        super(context);
        this.renderer = renderer;
        setRenderer(renderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
