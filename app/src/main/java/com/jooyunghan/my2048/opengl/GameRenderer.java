package com.jooyunghan.my2048.opengl;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RoundRectShape;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.jooyunghan.my2048.Cell;
import com.jooyunghan.my2048.Game;
import com.jooyunghan.my2048.GameView;
import com.jooyunghan.my2048.R;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wonyoung.jang on 2014-05-16.
 */
public class GameRenderer implements GLSurfaceView.Renderer, GameView {
    private static final int BLANK = 10;
    private float xAngle = 45;
    private float yAngle = 45;

    private static final int SIZE = 256;
    private static final int PADDING = SIZE / 20;
    private static final int ROUND_RADIUS = SIZE / 10;

    private int height;

    private int[] colors;
    private int[] textColors;
    private ArrayList<Qube> qubes = new ArrayList<Qube>();
    private int[] mTextures = new int[20];

    private RoundRectShape roundRectShape;
    private Game game;

    private QubeAnimator animator;
    private QubeAnimator forwardAnimator = new ForwardAnimator();
    private QubeAnimator backwardAnimator = new BackwardAnimator();

    public GameRenderer(Context context, Game game) {
        this.game = game;
        loadColors(context);
    }

    private void loadColors(Context context) {
        this.colors = loadColorResource(context, R.array.colors);
        this.textColors = loadColorResource(context, R.array.text_colors);
    }

    private int[] loadColorResource(Context context, int colorsResource) {
        TypedArray ta = context.getResources().obtainTypedArray(colorsResource);
        int[] colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();
        return colors;
    }

    private void initMetrics() {
    }

    private int indexOf(int value) {
        int index = 0;
        while (value > 2) {
            value >>= 1;
            index++;
        }
        return index;
    }

    private void createTexture(GL10 gl, int number) {
        int index = indexOf(number);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[index]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);
//        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT );
//        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT );
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_REPLACE);

        Bitmap mBitmap = createBitmapNumber(number);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
        mBitmap.recycle();
    }

    private Bitmap createBitmapNumber(int number) {
        Bitmap mBitmap;
        int index = indexOf(number);

        Bitmap.Config config =   Bitmap.Config.ARGB_8888;

        mBitmap = Bitmap.createBitmap(SIZE, SIZE, config);
        Canvas mCanvas = new Canvas(mBitmap);

        mCanvas.drawColor(Color.WHITE);

        Paint bgPaint = new Paint();
        bgPaint.setColor(colorFor(index));
        mCanvas.drawRoundRect(new RectF(PADDING, PADDING, SIZE - PADDING, SIZE - PADDING), ROUND_RADIUS, ROUND_RADIUS, bgPaint);

        Paint Pnt = new Paint();
        Pnt.setColor(textColorFor(index));
        Pnt.setTextSize(textSizeFor(number));
        Pnt.setAntiAlias(true);
        Pnt.setTextAlign(Paint.Align.CENTER);
        Pnt.setTextScaleX(1);
        mCanvas.drawText(String.valueOf(number), SIZE/2, SIZE/2+textSizeFor(number)/4, Pnt);

        return mBitmap;
    }

    private int textSizeFor(int value) {
        final int defaultFontSize = 128;

        if (value < 100) {
            return defaultFontSize;
        } else if (value < 1000) {
            return defaultFontSize * 3 / 4;
        } else if (value < 10000) {
            return defaultFontSize * 2 / 3;
        } else {
            return defaultFontSize / 2;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        gl.glClearColor(1.0f,1.0f,1.0f,1);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnable(GL10.GL_DEPTH_TEST);

        initTexture(gl);
    }

    private void initTexture(GL10 gl) {

        gl.glGenTextures(mTextures.length, mTextures, 0);

        for (int value = 2; value <=8192; value*=2) {
            createTexture(gl, value);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        int xStart;
        int yStart;
        int viewSize;
        if (width > height) {
            xStart = (width - height)/2;
            yStart = 0;
            viewSize = height;
        }
        else {
            xStart = 0;
            yStart = (height - width)/2;
            viewSize = width;
        }
        gl.glViewport(xStart, yStart, viewSize, viewSize);

        initMetrics();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glRotatef(10.0f, 1, 0, 0);
        gl.glRotatef(10.0f, 0, 1, 0);
        gl.glScalef(0.8f,0.8f,0.8f);

        for (Qube qube : qubes) {
            qube.draw(gl);
        }
    }

    @Override
    public void render() {
        animator = forwardAnimator;
        renderCells(game.getCells());
    }

    @Override
    public void renderUndo() {
        animator = backwardAnimator;
        renderCells(game.getCells());
    }

    public void renderCells(List<Cell> cells) {
        removeAll();
        for (Cell cell : cells) {
            addCellQube(cell);
        }
    }

    private void removeAll() {
        qubes.clear();
    }

    private void addCellQube(Cell cell) {
        for (Cell old : cell.merged) {
            addMergedCellQube(old);
        }

        Qube qube = new Qube(mTextures[indexOf(cell.value)]);
        if (cell.previous != null) {
            animator.setTransition(qube, cell);
        } else {
            animator.setShow(qube, cell);
        }
        add(qube);
    }

    private void addMergedCellQube(Cell cell) {
        for (Cell old : cell.merged) {
            addMergedCellQube(old);
        }

        Qube qube = new Qube(mTextures[indexOf(cell.value)]);
        if (cell.previous != null) {
            animator.setShowAndTransition(qube, cell);
        } else {
            animator.setShow(qube, cell);
        }
        add(qube);
    }

    private void add(Qube qube) {
        qubes.add(qube);
    }

    private int textColorFor(int index) {
        return textColors[Math.min(index, textColors.length - 1)];
    }

    private int colorFor(int index) {
        return colors[Math.min(index, colors.length - 1)];
    }

    private class ForwardAnimator implements QubeAnimator {
        @Override
        public void setTransition(Qube qube, Cell cell) {
            qube.move(cell.previous.x, cell.previous.y, cell.position.x, cell.position.y);
        }

        @Override
        public void setShow(Qube qube, Cell cell) {
            qube.show(cell.position.x, cell.position.y);
        }

        @Override
        public void setShowAndTransition(Qube qube, Cell cell) {
            qube.hide(cell.position.x, cell.position.y);
        }
    }

    private class BackwardAnimator implements QubeAnimator {
        @Override
        public void setTransition(Qube qube, Cell cell) {
            qube.move(cell.position.x, cell.position.y, cell.previous.x, cell.previous.y);
        }

        @Override
        public void setShow(Qube qube, Cell cell) {
            qube.hide(cell.position.x, cell.position.y);
        }

        @Override
        public void setShowAndTransition(Qube qube, Cell cell) {
            qube.move(cell.position.x, cell.position.y, cell.previous.x, cell.previous.y);
        }
    }
}
