package com.jooyunghan.my2048;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements GameView {

    private final Game game = new Game(this);
    private GestureDetectorCompat mDetector;
    private FrameLayout container;
    private TimeInterpolator overshotInterpolator = new OvershootInterpolator();
    static final int padding = 5;
    int[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        container = (FrameLayout)findViewById(R.id.container);
        loadColors();
        game.init();
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
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());

            final Direction direction = getDirection(velocityX, velocityY);
            game.process(direction);

            return true;
        }
    }


    private Direction getDirection(float velocityX, float velocityY) {
        final Direction direction;
        if (velocityX > Math.abs(velocityY)) {
            direction = Direction.X;
        } else if (velocityX < -Math.abs(velocityY)) {
            direction = Direction._X;
        } else if (velocityY > Math.abs(velocityX)) {
            direction = Direction.Y;
        } else {
            direction = Direction._Y;
        }
        return direction;
    }

    public void render() {
        container.removeAllViews();

        for(Cell cell: game.getNotNullCells()) {
             addCell(cell);
        }
    }

    private void addCell(Cell cell) {
        TextView view = new TextView(this);
        view.setTextSize(30);
        view.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        view.setText(cell.value + "");
        view.setBackgroundColor(colorFor(cell.value));
        if (cell.prev != null) {
            view.setY(cell.prev.y * 100 + padding);
            view.setX(cell.prev.x * 100 + padding);
            container.addView(view, 100 - padding*2, 100 - padding*2);
            view.animate().setDuration(100).x(cell.x * 100 + padding).y(cell.y * 100 + padding);
        } else {
            for (Cell old: cell.merged) {
                addCell(old);
            }
            view.setScaleX(0);
            view.setScaleY(0);
            view.setY(cell.y * 100 + padding);
            view.setX(cell.x * 100 + padding);
            container.addView(view,  100 - padding*2, 100 - padding*2);
            view.animate().setStartDelay(100).setDuration(100).setInterpolator(overshotInterpolator).scaleX(1).scaleY(1);
        }

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
