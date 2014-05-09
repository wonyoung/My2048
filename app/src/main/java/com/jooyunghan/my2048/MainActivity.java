package com.jooyunghan.my2048;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Interpolator;
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
import java.util.Iterator;
import java.util.Random;


public class MainActivity extends Activity  {

    private GestureDetectorCompat mDetector;
    int position = 0;
    private Cell[][] cells = new Cell[4][4];
    private FrameLayout container;
    private Random rand = new Random();
    private TimeInterpolator overshotInterpolator = new OvershootInterpolator();
    static final int padding = 5;

    enum Direction {
        X(1,0), _X(-1,0), Y(0,1), _Y(0,-1);
        private final int x;
        private final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean positive() {
            return x + y > 0;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        container = (FrameLayout)findViewById(R.id.container);
        addNewTile();
        addNewTile();
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
            process(direction);

            return true;
        }
    }

    private void process(Direction direction) {
        prepareMove();
        boolean move = false;

        if (direction.positive()) {
            for (int x=3; x>=0; x--) {
                for (int y=3; y>=0; y--) {
                    Cell cell = cells[x][y];
                    if (cell != null)
                        move |= moveCell(cell, direction);
                }
            }
        } else {

            for (int x=0; x<4; x++) {
                for (int y=0; y<4; y++) {
                    Cell cell = cells[x][y];
                    if (cell != null)
                        move |= moveCell(cell, direction);
                }
            }
        }


        if (move) {
            addNewTile();
            render();
        }

    }

    private boolean moveCell(Cell cell, Direction direction) {
        int moveCount = 0;
        while (true) {
            int nextX = cell.x + direction.x;
            int nextY = cell.y + direction.y;
            if (isEmpty(nextX, nextY)) {
                cells[cell.x][cell.y] = null;
                cell.x = nextX;
                cell.y = nextY;
                cells[nextX][nextY] = cell;
                moveCount++;
            } else if (canMerge(cell, nextX, nextY)) {
                cells[cell.x][cell.y] = null;
                cell.x = nextX;
                cell.y = nextY;
                Cell old = cells[nextX][nextY];
                cells[nextX][nextY] = new Cell(cell.value * 2, nextX, nextY);
                cells[nextX][nextY].merge(cell, old);
                moveCount++;
                break;
            } else {
                break;
            }
        }

        return moveCount > 0;
    }

    private boolean canMerge(Cell cell, int nextX, int nextY) {
        return nextX >= 0 && nextY >= 0 && nextX < 4 && nextY < 4 && cells[nextX][nextY] != null && cells[nextX][nextY].value == cell.value && cells[nextX][nextY].prev != null;
    }

    private boolean isEmpty(int nextX, int nextY) {
        return  nextX >= 0 && nextY >= 0 && nextX < 4 && nextY < 4 && cells[nextX][nextY] == null;
    }

    private void prepareMove() {
        forEachCells(new CellHandler() {
            @Override
            public void handle(int x, int y, Cell cell) {
                if (cell != null) {
                    cell.prev = new Position(x, y);
                    cell.merged.clear();
                }
            }
        });
    }

    private void addNewTile() {
        ArrayList<Position> positions = gatherEmptyCells();
        Position position = positions.get(rand.nextInt(positions.size()));
        int value = rand.nextInt(10) == 0 ? 4 : 2;
        cells[position.x][position.y] = new Cell(value, position.x, position.y);
    }

    private ArrayList<Position> gatherEmptyCells() {
        final ArrayList<Position> positions = new ArrayList<Position>();
        forEachCells(new CellHandler() {
            @Override
            public void handle(int x, int y, Cell cell) {
                if (cell == null)
                    positions.add(new Position(x, y));
            }
        });
        return positions;
    }

    private void forEachCells(CellHandler handler) {
        for (int x = 0; x<4; x++) {
            for (int y=0; y<4; y++) {
                handler.handle(x, y, cells[x][y]);
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
        view.setText(cell.value + "");
        view.setBackgroundColor(Color.BLUE);
        if (cell.prev != null) {
            view.setY(cell.prev.y * 100 + padding);
            view.setX(cell.prev.x * 100 + padding);
            container.addView(view, 100 - padding*2, 100 - padding*2);
            view.animate().x(cell.x * 100 + padding).y(cell.y * 100 + padding);
        } else {
            for (Cell old: cell.merged) {
                addCell(old);
            }
            view.setScaleX(0);
            view.setScaleY(0);
            view.setY(cell.y * 100 + padding);
            view.setX(cell.x * 100 + padding);
            container.addView(view,  100 - padding*2, 100 - padding*2);
            view.animate().setStartDelay(200).setInterpolator(overshotInterpolator).scaleX(1).scaleY(1);
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
