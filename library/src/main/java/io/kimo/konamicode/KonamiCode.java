package io.kimo.konamicode;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.kimo.konamicode.listener.ButtonListener;
import io.kimo.konamicode.listener.DirectionListener;

public class KonamiCode {

    private Context mContext;
    private View mView;
    private AlertDialog mButtonDialog;
    private int mSwipeThreshold;
    private int mSwipeVelocityThreshold;
    private DirectionListener mDirectionsListener;
    private ButtonListener mButtonListener;
    private Callback mCallback;

    public AlertDialog getButtonDialog() {
        return mButtonDialog;
    }

    public ButtonListener getButtonListener() {
        return mButtonListener;
    }

    public Callback getCallback() {
        return mCallback;
    }

    public Context getContext() {
        return mContext;
    }

    public DirectionListener getDirectionsListener() {
        return mDirectionsListener;
    }

    public int getSwipeThreshold() {
        return mSwipeThreshold;
    }

    public int getSwipeVelocityThreshold() {
        return mSwipeVelocityThreshold;
    }

    public View getView() {
        return mView;
    }

    private KonamiCode(@NonNull Builder builder) {
        this.mContext = builder.context;
        this.mView = builder.view;
        this.mSwipeThreshold = builder.swipeThreshold;
        this.mSwipeVelocityThreshold = builder.swipeVelocityThreshold;
        this.mDirectionsListener = builder.directionsListener;
        this.mButtonListener = builder.buttonListener;
        this.mButtonDialog = builder.dialog;
        this.mCallback = builder.callback;
    }

    public static class Builder {

        public static final int DEFAULT_THRESHOLD_VALUE = 150;

        private GestureDetectorCompat gestureListener;

        protected Context context;
        protected View view;
        protected DirectionListener directionsListener;
        protected ButtonListener buttonListener;
        protected int swipeThreshold = DEFAULT_THRESHOLD_VALUE;
        protected int swipeVelocityThreshold = DEFAULT_THRESHOLD_VALUE;
        protected AlertDialog dialog;
        protected Callback callback = new Callback() {
            @Override
            public void onFinish() {
                Toast.makeText(context, R.string.default_callback_msg, Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        };

        /**
         * Konami Code's builder
         * @param context
         */
        public Builder(@NonNull Context context) {
            this.context = context;
        }

        /**
         * into - installs into an activity
         * @param activity
         */
        public Builder into(@NonNull Activity activity) {
            view = activity.findViewById(android.R.id.content);
            return this;
        }

        /**
         * into - installs into a fragment
         * @param fragment
         */
        public Builder into(@NonNull Fragment fragment) {
            view = fragment.getView();
            return this;
        }

        /**
         * into - installs into a view
         * @param view
         */
        public Builder into(@NonNull View view) {
            this.view = view;
            return this;
        }

        /**
         * withSwipeThreshold - distance of the swipe movment
         * @param value
         */
        public Builder withSwipeThreshold(int value) {
            swipeThreshold = value;
            return this;
        }

        /**
         * withSwipeVelocityThreshold - speed of the swipe movment
         * @param value
         */
        public Builder withSwipeVelocityThreshold(int value) {
            swipeVelocityThreshold = value;
            return this;
        }

        /**
         * withCallback - interface executed after the whole code is executed
         * @param callback
         */
        public Builder withCallback(@NonNull Callback callback) {
            this.callback = callback;
            return this;
        }

        /**
         * install - installs all Konami Code components into the target
         */
        public KonamiCode install() {

            configureButtonsDialog();
            configureGestureListener();

            return new KonamiCode(this);
        }

        private void configureButtonsDialog() {

            View buttonsView = LayoutInflater.from(context).inflate(R.layout.dialog_buttons, (ViewGroup) view, false);

            View aButton = buttonsView.findViewById(R.id.konami_button_a);
            View bButton = buttonsView.findViewById(R.id.konami_button_b);
            View startButton = buttonsView.findViewById(R.id.konami_button_start);

            dialog = new AlertDialog.Builder(context)
                    .setView(buttonsView)
                    .create();

            buttonListener = new ButtonListener(aButton, bButton, startButton, dialog, callback);
        }

        private void configureGestureListener() {
            directionsListener = new DirectionListener(dialog, swipeThreshold, swipeVelocityThreshold);
            gestureListener  = new GestureDetectorCompat(context, directionsListener);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureListener.onTouchEvent(event);
                    return true;
                }
            });
        }
    }

    /**
     * Callback - Interface that's executed when the code finishes
     */
    public interface Callback {
        void onFinish();
    }
}
