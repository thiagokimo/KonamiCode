package io.kimo.konamicode;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class KonamiCode {

    private Context mContext;
    private ViewGroup mRootView;
    private KonamiCodeLayout.Callback mCallback;

    public KonamiCodeLayout.Callback getCallback() {
        return mCallback;
    }
    public Context getContext() {
        return mContext;
    }
    public ViewGroup getRootView() {
        return mRootView;
    }

    private KonamiCode(@NonNull Builder builder) {
        this.mContext = builder.context;
        this.mRootView = builder.rootView;
        this.mCallback = builder.callback;
    }

    public static class Builder {
        protected Context context;
        protected ViewGroup rootView;
        protected KonamiCodeLayout.Callback callback;

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
            rootView = (ViewGroup) activity.findViewById(android.R.id.content);
            return this;
        }

        /**
         * into - installs into a fragment
         * @param fragment
         */
        public Builder into(@NonNull Fragment fragment) {
            rootView = (ViewGroup) fragment.getView().getRootView();
            return this;
        }

        /**
         * into - installs into a view
         * @param view
         */
        public Builder into(@NonNull View view) {
            rootView = (ViewGroup) view.getRootView();
            return this;
        }

        /**
         * withCallback - interface executed after the whole code is executed
         * @param callback
         */
        public Builder withCallback(@NonNull KonamiCodeLayout.Callback callback) {
            this.callback = callback;
            return this;
        }

        /**
         * install - installs all Konami Code components into the target
         */
        public KonamiCode install() {

            View currentView = rootView.getChildAt(0);
            rootView.removeView(currentView);

            //match parent params
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );

            FrameLayout gestureDelegate = new FrameLayout(context);
            gestureDelegate.addView(currentView, layoutParams);

            //necessary view that passes all touch events up to the parent viewgroup
            gestureDelegate.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            KonamiCodeLayout konamiCodeLayout = new KonamiCodeLayout(context);
            konamiCodeLayout.addView(gestureDelegate);

            rootView.addView(konamiCodeLayout, layoutParams);

            konamiCodeLayout.setCallback(callback);

            return new KonamiCode(this);
        }

    }
}
