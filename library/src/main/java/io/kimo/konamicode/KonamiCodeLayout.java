package io.kimo.konamicode;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
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

    public static final String TAG = KonamiCodeLayout.class.getSimpleName();

    /**
     * Callback - Interface that's executed when the code finishes
     */
    public interface Callback {
        void onFinish();
    }

    public static final int NONE = -1;

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    public static final int A = 4;
    public static final int B = 5;
    public static final int START = 6;

    private Callback mCallback;

    private AlertDialog buttonDialog;
    private View aButton;
    private View bButton;
    private View startButton;
    private OnClickListener buttonsClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            if(id == R.id.konami_button_a) {
                mLastPressedButton = A;
            } else if(id == R.id.konami_button_b) {
                mLastPressedButton = B;
            } else if(id == R.id.konami_button_start) {
                mLastPressedButton = START;
            }

            registerPress();
        }
    };

    private int mLastSwipedDirection = NONE;
    private int mLastPressedButton = NONE;
    private int mSwipeThreshold;

    private float mLastX;
    private float mLastY;
    private float mStartX;
    private float mStartY;

    private List<Integer> mKonamiCodeDirectionsOrder = Arrays.asList(
            UP, UP, DOWN, DOWN, LEFT, RIGHT, LEFT, RIGHT
    );
    private List<Integer> mKonamiCodeButtonsOrder = Arrays.asList(
            B, A, START
    );

    private List<Integer> mSwipes = new ArrayList<>();
    private List<Integer> mPressedButtons = new ArrayList<>();

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

        aButton = buttonsView.findViewById(R.id.konami_button_a);
        bButton = buttonsView.findViewById(R.id.konami_button_b);
        startButton = buttonsView.findViewById(R.id.konami_button_start);

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
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                mLastX = ev.getX();
                mLastY = ev.getY();

                mStartX = mLastX;
                mStartY = mLastY;
                break;
            case MotionEvent.ACTION_MOVE:

                float diffY = ev.getY() - mLastY;
                float diffX = ev.getX() - mLastX;

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > mSwipeThreshold) {
                        if (diffX > 0) {
                            mLastSwipedDirection = RIGHT;
                        } else {
                            mLastSwipedDirection = LEFT;
                        }
                    }
                }
                else if (Math.abs(diffY) > mSwipeThreshold) {
                    if (diffY > 0) {
                        mLastSwipedDirection = DOWN;
                    } else {
                        mLastSwipedDirection = UP;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                registerSwipe();
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        View child = getChildAt(0);
        if(child == null) {
            return false;
        } else {
            return child.dispatchTouchEvent(event);
        }
    }

    @Override
    public boolean onSwipeSequenceAchieved() {
        return mSwipes.equals(mKonamiCodeDirectionsOrder);
    }

    @Override
    public boolean validSwipeSequence() {
        int index = mSwipes.size()-1;
        int correctDirection = mKonamiCodeDirectionsOrder.get(index);
        int lastDirection = mSwipes.get(index);

        return correctDirection == lastDirection;
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

        int correctPressedButton = mKonamiCodeButtonsOrder.get(index);
        int lastPressedButton = mPressedButtons.get(index);

        return lastPressedButton == correctPressedButton;
    }

    @Override
    public void resetPressedSequence() {
        mPressedButtons.clear();
    }

    private void showDialog() {
        buttonDialog.show();
    }

    private void registerSwipe() {
        if(mLastSwipedDirection != NONE) {
            mSwipes.add(mLastSwipedDirection);

            if(!validSwipeSequence()) {
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
        if(mLastPressedButton != NONE) {
            mPressedButtons.add(mLastPressedButton);

            if(!validPressedSequence()) {
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
