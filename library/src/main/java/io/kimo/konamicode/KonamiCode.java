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
import android.widget.Toast;

import io.kimo.konamicode.listener.ButtonsListener;
import io.kimo.konamicode.listener.DirectionListener;

public class KonamiCode {

    private Context mContext;
    private View mView;
    private AlertDialog mButtonsDialog;
    private int mSwipeThreshold;
    private int mSwipeVelocityThreshold;
    private DirectionListener mDirectionsListener;
    private ButtonsListener mButtonsListener;
    private Callback mCallback;

    public AlertDialog getButtonsDialog() {
        return mButtonsDialog;
    }

    public ButtonsListener getButtonsListener() {
        return mButtonsListener;
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
        this.mButtonsListener = builder.buttonsListener;
        this.mButtonsDialog = builder.dialog;
        this.mCallback = builder.callback;
    }

    public static class Builder {

        public static final int DEFAULT_THRESHOLD_VALUE = 150;

        private GestureDetectorCompat gestureListener;

        protected Context context;
        protected View view;
        protected DirectionListener directionsListener;
        protected ButtonsListener buttonsListener;
        protected int swipeThreshold = DEFAULT_THRESHOLD_VALUE;
        protected int swipeVelocityThreshold = DEFAULT_THRESHOLD_VALUE;
        protected AlertDialog dialog;
        protected Callback callback = new Callback() {
            @Override
            public void onFinish() {
                Toast.makeText(context, "Konami Code!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        };

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Builder into(@NonNull Activity activity) {
            view = activity.findViewById(android.R.id.content);
            return this;
        }

        public Builder into(@NonNull Fragment fragment) {
            view = fragment.getView();
            return this;
        }

        public Builder into(@NonNull View view) {
            this.view = view;
            return this;
        }

        public Builder withSwipeThreshold(int value) {
            swipeThreshold = value;
            return this;
        }

        public Builder withSwipeVelocityThreshold(int value) {
            swipeVelocityThreshold = value;
            return this;
        }

        public Builder withCallback(@NonNull Callback callback) {
            this.callback = callback;
            return this;
        }

        public KonamiCode install() {

            configureButtonsDialog();
            configureGestureListener();

            return new KonamiCode(this);
        }

        private void configureButtonsDialog() {

            View buttonsView = LayoutInflater.from(context).inflate(R.layout.dialog_buttons, null);

            View aButton = buttonsView.findViewById(R.id.konami_button_a);
            View bButton = buttonsView.findViewById(R.id.konami_button_b);
            View startButton = buttonsView.findViewById(R.id.konami_button_start);

            dialog = new AlertDialog.Builder(context)
                    .setView(buttonsView)
                    .create();

            buttonsListener = new ButtonsListener(aButton, bButton, startButton, dialog, callback);
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

    public interface Callback {
        void onFinish();
    }
}
