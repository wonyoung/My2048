package com.jooyunghan.my2048;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
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
    private RoundRectShape rect;

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

        view.setTextSize(TEXT_SIZE);
        view.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        view.setText(value + "");

        if (rect == null) {
            rect = new RoundRectShape(
                    new float[]{ROUND_RADIUS, ROUND_RADIUS, ROUND_RADIUS, ROUND_RADIUS, ROUND_RADIUS,
                            ROUND_RADIUS, ROUND_RADIUS, ROUND_RADIUS}, null, null);
        }
        ShapeDrawable bg = new ShapeDrawable(rect);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
