package com.jooyunghan.my2048;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Callable;


public class MainActivity extends Activity implements GameView {

    private int ROUND_RADIUS = 30;
    private int SIZE = 150;
    private int PADDING = 5;
    private int TEXT_SIZE = 30;
    private final Game game = new Game(this);
    private GestureDetectorCompat mDetector;
    private FrameLayout container;
    private TextView scoreTextView;
    private TimeInterpolator overshotInterpolator = new OvershootInterpolator();
    private TimeInterpolator translateInterpolator = new LinearInterpolator();

    private int[] colors;
    private RoundRectShape roundRectShape;
    private int oldScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        container = (FrameLayout)findViewById(R.id.container);
        scoreTextView = (TextView)findViewById(R.id.score);
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
        recycleViews();

        for (Cell cell : cells) {
            try {
                addCellView(cell);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void recycleViews() {
        viewCacheList.reset();
        container.removeAllViews();
    }

    @Override
    public void renderScore(int score) {
        int diff = score - oldScore;
        oldScore = score;
        scoreTextView.setText(score + "");

        if (diff == 0)
            return;
        final TextView diffText = new TextView(this);
        diffText.setText("+" + diff);

        float textSize = scoreTextView.getTextSize();
        diffText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        final ViewGroup parent = (ViewGroup) scoreTextView.getParent();
        diffText.setY(scoreTextView.getY());
        diffText.setX(scoreTextView.getX());
        diffText.animate().yBy(- textSize * 2).alpha(0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                parent.removeView(diffText);
            }
        });

        parent.addView(diffText, 100, 100);
    }

    private void addCellView(Cell cell) throws Exception {
        for (Cell old : cell.merged) {
            addCellView(old);
        }

        View view = viewFor(cell.value);
        if (cell.prev != null) {
            setTransitionAnimation(view, cell.prev, cell.position);
        } else {
            setShowAnimation(view, cell.position);
        }
        add(view);
    }

    private ViewCacheList viewCacheList = new ViewCacheList();

    private int indexOf(int value) {
        int index = 0;
        while (value > 0 && index < colors.length) {
            value >>= 1;
            index++;
        }
        return index - 1;
    }

    private View viewFor(final int value) throws Exception {
        int index = indexOf(value);

        ViewCache viewCache = viewCacheList.get(index);
        return viewCache.get(new Callable<View>() {
            @Override
            public View call() throws Exception {
                return makeViewFor(value);
            }
        });
    }

    private View makeViewFor(int value) {
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
        view.animate().setStartDelay(0).setDuration(100).setInterpolator(translateInterpolator).x(
                to.x * SIZE + PADDING).y(to.y * SIZE + PADDING);
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
