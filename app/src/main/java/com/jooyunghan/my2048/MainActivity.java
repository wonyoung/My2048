package com.jooyunghan.my2048;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Paint;
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
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jooyunghan.my2048.opengl.GameRenderer;
import com.jooyunghan.my2048.opengl.GameSurfaceView;

import java.util.List;


public class MainActivity extends Activity implements GameView {

    private int ROUND_RADIUS = 30;
    private int SIZE = 150;
    private int PADDING = 5;
    private int TEXT_SIZE = 30;
    private final Game game = new Game(this);
    private GestureDetectorCompat mDetector;
    private FrameLayout container;
    private TextView scoreTextView;
    private Button undoButton;

    private TimeInterpolator overshotInterpolator = new OvershootInterpolator();
    private TimeInterpolator translateInterpolator = new LinearInterpolator();

    private CellAnimator animator;
    private CellAnimator forwardAnimator = new ForwardAnimator();
    private CellAnimator reverseAnimator = new ReverseAnimator();
    private int[] colors;
    private int[] textColors;
    private RoundRectShape roundRectShape;
    private int oldScore;
    private GameRenderer glGameRenderer;
    private GameSurfaceView gameSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        container = (FrameLayout)findViewById(R.id.container);
        scoreTextView = (TextView)findViewById(R.id.score);
        undoButton = (Button)findViewById(R.id.undo);
        container.post(new Runnable() { // after view loaded
            @Override
            public void run() {
                initMetrics();
                loadColors();
                prepareBackground();
                game.init();
            }
        });

        FrameLayout glContainer = (FrameLayout) findViewById(R.id.glcontainer);
        glGameRenderer = new GameRenderer(this, game);
        gameSurfaceView = new GameSurfaceView(this, glGameRenderer);
        glContainer.addView(gameSurfaceView);
    }


    public void restart(View view) {
        game.init();
    }

    public void undo(View view) {
        game.undo();
    }

    private void initMetrics() {
        SIZE = Math.min(container.getWidth(), container.getHeight()) / 4;
        PADDING = SIZE / 20;
        TEXT_SIZE = SIZE / 2;
        ROUND_RADIUS = SIZE / 10;
    }

    private void loadColors() {
        this.colors = loadColorResource(R.array.colors);
        this.textColors = loadColorResource(R.array.text_colors);
    }

    private int[] loadColorResource(int colorsResource) {
        TypedArray ta = getResources().obtainTypedArray(colorsResource);
        int[] colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();
        return colors;
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
    public void render() {
        animator = forwardAnimator;
        renderCells(game.getCells());
        glGameRenderer.render();
        gameSurfaceView.requestRender();
        renderScore(game.getScore());
        renderControls();
    }

    @Override
    public void renderUndo() {
        animator = reverseAnimator;
        renderCells(game.getCells());
        glGameRenderer.renderUndo();
        gameSurfaceView.requestRender();
        renderScore(game.getScore());
        renderControls();
    }

    private void renderControls() {
        undoButton.setEnabled(game.isUndoable());
    }

    private void renderCells(List<Cell> cells) {
        container.removeAllViews();

        for (Cell cell : cells) {
            addCellView(cell);
        }
    }

    private void renderScore(int score) {
        int diff = score - oldScore;
        oldScore = score;
        scoreTextView.setText(score + "");

        if (diff <= 0)
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
        diffText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        parent.addView(diffText);
    }

    private void addCellView(Cell cell) {
        for (Cell old : cell.merged) {
            addCellView(old);
        }

        View view = viewFor(cell.value);
        if (cell.previous != null) {
            animator.setTransition(view, cell);
        } else {
            animator.setShow(view, cell);
        }
        add(view);
    }

    private int indexOf(int value) {
        int index = 0;
        while (value > 2) {
            value >>= 1;
            index++;
        }
        return index;
    }

    private View viewFor(final int value) {
        int index = indexOf(value);

        TextView view = new TextView(this);
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeFor(value));
        view.setGravity(Gravity.CENTER);
        view.setTextColor(textColorFor(index));
        view.setText(value + "");
        view.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        ShapeDrawable bg = new ShapeDrawable(roundRectShape);
        bg.getPaint().setColor(colorFor(index));
        view.setBackgroundDrawable(bg);

        return view;
    }

    private void add(View view) {
        container.addView(view, SIZE - PADDING * 2, SIZE - PADDING * 2);
    }

    private int textSizeFor(int value) {
        if (value < 100) {
            return TEXT_SIZE;
        } else if (value < 1000) {
            return TEXT_SIZE * 3 / 4;
        } else if (value < 10000) {
            return TEXT_SIZE * 2 / 3;
        } else {
            return TEXT_SIZE / 2;
        }
    }

    private int textColorFor(int index) {
        return textColors[Math.min(index, textColors.length - 1)];
    }

    private int colorFor(int index) {
        return colors[Math.min(index, colors.length - 1)];
    }

    private class ForwardAnimator implements CellAnimator {
        @Override
        public void setTransition(View view, Cell cell) {
            view.setY(cell.previous.y * SIZE + PADDING);
            view.setX(cell.previous.x * SIZE + PADDING);
            view.animate().setStartDelay(0).setDuration(100).setInterpolator(translateInterpolator).x(
                    cell.position.x * SIZE + PADDING).y(cell.position.y * SIZE + PADDING);
        }

        @Override
        public void setShow(View view, Cell cell) {
            view.setScaleX(0);
            view.setScaleY(0);
            view.setY(cell.position.y * SIZE + PADDING);
            view.setX(cell.position.x * SIZE + PADDING);
            view.animate().setStartDelay(100).setDuration(100).setInterpolator(overshotInterpolator)
                    .scaleX(1).scaleY(1);
        }
    }

    private class ReverseAnimator implements CellAnimator {
        @Override
        public void setTransition(View view, Cell cell) {
            view.setY(cell.position.y * SIZE + PADDING);
            view.setX(cell.position.x * SIZE + PADDING);
            view.animate().setStartDelay(100).setDuration(100).setInterpolator(translateInterpolator).x(
                    cell.previous.x * SIZE + PADDING).y(cell.previous.y * SIZE + PADDING);
        }

        @Override
        public void setShow(View view, Cell cell) {
            view.setScaleX(1);
            view.setScaleY(1);
            view.setY(cell.position.y * SIZE + PADDING);
            view.setX(cell.position.x * SIZE + PADDING);
            view.animate().setStartDelay(0).setDuration(100).setInterpolator(translateInterpolator)
                    .scaleX(0).scaleY(0);
        }
    }
}
