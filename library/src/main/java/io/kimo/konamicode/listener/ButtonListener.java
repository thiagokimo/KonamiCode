package io.kimo.konamicode.listener;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.kimo.konamicode.KonamiCode;
import io.kimo.konamicode.R;
import io.kimo.konamicode.SequenceListener;

/**
 * ButtonListener
 *
 * Detects the correct sequence of Konami Code's button execution: B, A and START
 */
public class ButtonListener implements SequenceListener {

    public enum KonamiButton {
        A, B, START
    }

    private List<KonamiButton> mKonamiCodeButtonsOrder = Arrays.asList(new KonamiButton[] {
            KonamiButton.B, KonamiButton.A, KonamiButton.START
    });

    private List<KonamiButton> mPressedButtons = new ArrayList<>();

    private View a, b, start;
    private KonamiCode.Callback callback;
    private AlertDialog dialog;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            if(id == R.id.konami_button_a) {
                addPressedButton(KonamiButton.A);
            } else if(id == R.id.konami_button_b) {
                addPressedButton(KonamiButton.B);
            } else if(id == R.id.konami_button_start) {
                addPressedButton(KonamiButton.START);
            }
        }
    };

    public ButtonListener(@NonNull View a, @NonNull View b, @NonNull View start, @NonNull AlertDialog dialog, @NonNull KonamiCode.Callback callback) {
        this.a = a;
        this.b = b;
        this.start = start;
        this.dialog = dialog;
        this.callback = callback;

        configure();
    }

    @Override
    public boolean onSequenceAchieved() {
        return mPressedButtons.equals(mKonamiCodeButtonsOrder);
    }

    @Override
    public boolean validSequence() {
        int index = mPressedButtons.size()-1;

        KonamiButton currentPressedButton = mPressedButtons.get(index);
        KonamiButton correctPressedButton = mKonamiCodeButtonsOrder.get(index);

        return currentPressedButton.equals(correctPressedButton);
    }

    @Override
    public void resetSequence() {
        mPressedButtons.clear();
    }

    private void addPressedButton(KonamiButton button) {
        mPressedButtons.add(button);

        if(onSequenceAchieved()) {
            dialog.dismiss();
            callback.onFinish();
            resetSequence();
        } else {
            if(!validSequence()) {
                dialog.dismiss();
                resetSequence();
            }
        }
    }

    private void configure() {
        a.setOnClickListener(onClickListener);
        b.setOnClickListener(onClickListener);
        start.setOnClickListener(onClickListener);
    }
}
