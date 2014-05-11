package com.jooyunghan.my2048;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends Activity implements GameView {

    private int ROUND_RADIUS = 30;
    private int SIZE = 150;
    private int PADDING = 5;
    private int TEXT_SIZE = 30;
    private final Game game = new Game(this);
    private GestureDetectorCompat mDetector;
    private FrameLayout container;
    private TimeInterpolator overshotInterpolator = new OvershootInterpolator();
    private int[] colors;
    private RoundRectShape roundRectShape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        container = (FrameLayout)findViewById(R.id.container);
        container.post(new Runnable() { // after view loaded
            @Override
            public void run() {
                initMetrics();
                loadColors();
                prepareBackground();
                game.init();
            }
        });
    }

    private void initMetrics() {
        SIZE = container.getWidth() / 4;
        PADDING = SIZE / 20;
        TEXT_SIZE = SIZE / 2;
        ROUND_RADIUS = SIZE / 10;
    }

    private void loadColors() {
        TypedArray ta = getResources().obtainTypedArray(R.array.colors);
        colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();
    }

    private void prepareBackground() {
        roundRectShape = new RoundRectShape(
                new float[]{ROUND_RADIUS, ROUND_RADIUS, ROUND_RADIUS, ROUND_RADIUS, ROUND_RADIUS,
                        ROUND_RADIUS, ROUND_RADIUS, ROUND_RADIUS}, null, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX,
                               float velocityY) {
            float dx = event2.getX() - event1.getX();
            float dy = event2.getY() - event1.getY();
            game.process(getDirection(dx, dy));
            return true;
        }

        private Direction getDirection(float dx, float dy) {
            if (dx > Math.abs(dy)) {
                return Direction.X;
            } else if (dx < -Math.abs(dy)) {
                return Direction._X;
            } else if (dy > Math.abs(dx)) {
                return Direction.Y;
            } else {
                return Direction._Y;
            }
        }
    }

    @Override
    public void render(List<Cell> cells) {
        container.removeAllViews();

        for (Cell cell : cells) {
            addCell(cell);
        }
    }

    private void addCell(Cell cell) {
        for (Cell old : cell.merged) {
            addCell(old);
        }

        View view = viewFor(cell.value);
        if (cell.prev != null) {
            setTransitionAnimation(view, cell.prev, cell.position);
        } else {
            setShowAnimation(view, cell.position);
        }
        add(view);
    }

    private View viewFor(int value) {
        TextView view = new TextView(this);

        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE);
        view.setGravity(Gravity.CENTER);

        view.setText(value + "");

        ShapeDrawable bg = new ShapeDrawable(roundRectShape);
        bg.getPaint().setColor(colorFor(value));
        view.setBackgroundDrawable(bg);

        return view;
    }

    private void setTransitionAnimation(View view, Position from, Position to) {
        view.setY(from.y * SIZE + PADDING);
        view.setX(from.x * SIZE + PADDING);
        view.animate().setDuration(100).x(to.x * SIZE + PADDING).y(to.y * SIZE + PADDING);
    }

    private void setShowAnimation(View view, Position position) {
        view.setScaleX(0);
        view.setScaleY(0);
        view.setY(position.y * SIZE + PADDING);
        view.setX(position.x * SIZE + PADDING);
        view.animate().setStartDelay(100).setDuration(100).setInterpolator(overshotInterpolator)
                .scaleX(1).scaleY(1);
    }

    private void add(View view) {
        container.addView(view, SIZE - PADDING * 2, SIZE - PADDING * 2);
    }

    private int colorFor(int value) {
        int index = 0;
        while (value > 0 && index < colors.length) {
            value >>= 1;
            index++;
        }

        return colors[index - 1];
    }
}
