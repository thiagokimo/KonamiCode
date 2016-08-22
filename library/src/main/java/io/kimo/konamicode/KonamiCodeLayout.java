package io.kimo.konamicode;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KonamiCodeLayout extends FrameLayout implements KonamiSequenceListener {

    /**
     * Callback - Interface that's executed when the code finishes
     */
    public interface Callback {
        void onFinish();
    }

    /**
     * Enumeration of swipe directions
     */
    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

    /**
     * Enumeration of the buttons
     */
    public enum Button {
        A, B, START, NONE
    }

    public static final String TAG = KonamiCodeLayout.class.getSimpleName();

    private Callback mCallback;

    private AlertDialog buttonDialog;
    private OnClickListener buttonsClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            if(id == R.id.konami_button_a) {
                mLastPressedButton = Button.A;
            } else if(id == R.id.konami_button_b) {
                mLastPressedButton = Button.B;
            } else if(id == R.id.konami_button_start) {
                mLastPressedButton = Button.START;
            }

            registerPress();
        }
    };

    private Direction mLastSwipedDirection = Direction.NONE;
    private Button mLastPressedButton = Button.NONE;
    private int mSwipeThreshold;

    private float mLastX;
    private float mLastY;

    private List<Direction> mKonamiCodeDirectionsOrder = Arrays.asList(Direction.UP, Direction.UP,
            Direction.DOWN, Direction.DOWN,
            Direction.LEFT, Direction.RIGHT,
            Direction.LEFT, Direction.RIGHT);


    private List<Button> mKonamiCodeButtonsOrder = Arrays.asList(Button.B, Button.A, Button.START);

    private List<Direction> mSwipes = new ArrayList<>();
    private List<Button> mPressedButtons = new ArrayList<>();

    public KonamiCodeLayout(Context context) {
        super(context);
        init();
    }

    public KonamiCodeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KonamiCodeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mSwipeThreshold = viewConfiguration.getScaledTouchSlop();

        View buttonsView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_buttons, this, false);

        View aButton = buttonsView.findViewById(R.id.konami_button_a);
        View bButton = buttonsView.findViewById(R.id.konami_button_b);
        View startButton = buttonsView.findViewById(R.id.konami_button_start);

        aButton.setOnClickListener(buttonsClickListener);
        bButton.setOnClickListener(buttonsClickListener);
        startButton.setOnClickListener(buttonsClickListener);

        buttonDialog = new AlertDialog.Builder(getContext())
                .setView(buttonsView)
                .create();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        View child = getChildAt(0);
        return child != null && child.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        processTouches(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void processTouches(@NonNull MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                mLastX = ev.getX();
                mLastY = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:

                float diffY = ev.getY() - mLastY;
                float diffX = ev.getX() - mLastX;

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > mSwipeThreshold) {
                        if (diffX > 0) {
                            mLastSwipedDirection = Direction.RIGHT;
                        } else {
                            mLastSwipedDirection = Direction.LEFT;
                        }
                    }
                }
                else if (Math.abs(diffY) > mSwipeThreshold) {
                    if (diffY > 0) {
                        mLastSwipedDirection = Direction.DOWN;
                    } else {
                        mLastSwipedDirection = Direction.UP;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                registerSwipe();
                break;
        }
    }

    @Override
    public boolean onSwipeSequenceAchieved() {
        return mSwipes.equals(mKonamiCodeDirectionsOrder);
    }

    @Override
    public boolean validSwipeSequence() {
        int index = mSwipes.size()-1;
        Direction correctDirection = mKonamiCodeDirectionsOrder.get(index);
        Direction lastDirection = mSwipes.get(index);

        return correctDirection.equals(lastDirection);
    }

    @Override
    public void resetSwipeSequence() {
        mSwipes.clear();
    }

    @Override
    public boolean onPressedSequenceAchieved() {
        return mPressedButtons.equals(mKonamiCodeButtonsOrder);
    }

    @Override
    public boolean validPressedSequence() {
        int index = mPressedButtons.size()-1;

        Button currentPressedButton = mPressedButtons.get(index);
        Button correctPressedButton = mKonamiCodeButtonsOrder.get(index);

        return currentPressedButton.equals(correctPressedButton);
    }

    @Override
    public void resetPressedSequence() {
        mPressedButtons.clear();
    }

    private void showDialog() {
        buttonDialog.show();
    }

    private void registerSwipe() {
        if(mLastSwipedDirection != Direction.NONE) {
            Log.d(TAG, "registerSwipe: "+mLastSwipedDirection);
            mSwipes.add(mLastSwipedDirection);

            if(!validSwipeSequence()) {
                Log.d(TAG, "Invalid swipe sequence");
                resetSwipeSequence();
            } else {
                if(onSwipeSequenceAchieved()) {
                    showDialog();
                    resetSwipeSequence();
                }
            }
        }
    }

    private void registerPress() {
        if(mLastPressedButton != Button.NONE) {
            mPressedButtons.add(mLastPressedButton);

            if(!validPressedSequence()) {
                Log.d(TAG, "Invalid button sequence!");
                resetPressedSequence();
                buttonDialog.dismiss();
            } else {
                if(onPressedSequenceAchieved()) {
                    triggerFinalCallback();
                    resetPressedSequence();
                }
            }
        }
    }

    private void triggerFinalCallback() {
        buttonDialog.dismiss();
        if(mCallback == null) {
            Toast.makeText(getContext(), "Konami Code", Toast.LENGTH_LONG).show();
        } else {
            mCallback.onFinish();
        }
    }
}
