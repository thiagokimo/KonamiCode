package io.kimo.konamicode.listener;

import android.support.v7.app.AlertDialog;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.kimo.konamicode.SequenceListener;

public class DirectionListener extends GestureDetector.SimpleOnGestureListener implements SequenceListener {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private AlertDialog mDialog;
    private int mSwipeThreshold = 100;
    private int mSwipeVelocityThreshold = 100;

    private List<Direction> mKonamiCodeDirections = Arrays.asList(new Direction[] {
            Direction.UP, Direction.UP,
            Direction.DOWN, Direction.DOWN,
            Direction.LEFT, Direction.RIGHT,
            Direction.LEFT, Direction.RIGHT
    });

    private List<Direction> mSwipes = new ArrayList<>();

    public DirectionListener(AlertDialog dialog, int swipeThreshold, int swipeVelocityThreshold) {
        this.mDialog = dialog;
        this.mSwipeThreshold = swipeThreshold;
        this.mSwipeVelocityThreshold = swipeVelocityThreshold;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > mSwipeThreshold && Math.abs(velocityX) > mSwipeVelocityThreshold) {
                    if (diffX > 0) {
                        addSwipe(Direction.RIGHT);
                    } else {
                        addSwipe(Direction.LEFT);
                    }
                }
                result = true;
            }
            else if (Math.abs(diffY) > mSwipeThreshold && Math.abs(velocityY) > mSwipeVelocityThreshold) {
                if (diffY > 0) {
                    addSwipe(Direction.DOWN);
                } else {
                    addSwipe(Direction.UP);
                }
            }
            result = true;

        } catch (Exception exception) {
            resetSequence();
        }
        return result;
    }

    @Override
    public boolean onSequenceAchieved() {
        return mSwipes.equals(mKonamiCodeDirections);
    }

    @Override
    public boolean validSequence() {
        int index = mSwipes.size()-1;
        Direction correctDirection = mKonamiCodeDirections.get(index);
        Direction lastDirection = mSwipes.get(index);

        return correctDirection.equals(lastDirection);
    }

    @Override
    public void resetSequence() {
        mSwipes.clear();
    }

    private void addSwipe(Direction direction) {
        mSwipes.add(direction);

        if(!validSequence()) {
            resetSequence();
        } else {
            if(onSequenceAchieved()) {
                mDialog.show();
                resetSequence();
            }
        }
    }
}
