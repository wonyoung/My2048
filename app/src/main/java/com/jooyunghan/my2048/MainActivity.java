package com.jooyunghan.my2048;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;


public class MainActivity extends Activity  {

    private GestureDetectorCompat mDetector;
    int position = 0;
    private Cell[][] cells = new Cell[4][4];
    private FrameLayout container;
    enum Direction {
        X(1,0), _X(-1,0), Y(0,1), _Y(0,-1);
        private final int x;
        private final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        container = (FrameLayout)findViewById(R.id.container);
        cells[0][0] = new Cell();
        render();
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
            move(direction);
            render();
            return true;
        }
    }

    private void move(Direction direction) {
        for(Cell[] row: cells) {
            for (Cell cell: row) {
                if (cell != null) {
                    cell.prev = new Position(cell.x, cell.y);
                    cell.x += direction.x;
                    cell.y += direction.y;
                }
            }
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

    private void render() {
        container.removeAllViews();

        for(Cell[] row: cells) {
            for (Cell cell: row) {
                if (cell != null)
                    addCell(cell);
            }
        }
    }

    private void addCell(Cell cell) {
        TextView view = new TextView(this);
        view.setText("2048");
        view.setBackgroundColor(Color.BLUE);
        if (cell.prev != null) {
            view.setY(cell.prev.y * 100);
            view.setX(cell.prev.x * 100);
            container.addView(view, 100, 100);
            view.animate().x(cell.x * 100).y(cell.y * 100);
        } else {
            view.setY(cell.y * 100);
            view.setX(cell.x * 100);
            container.addView(view, 100, 100);
        }

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
