package io.kimo.konamicode;

public interface SequenceListener {

    boolean onSequenceAchieved();
    boolean validSequence();

    void resetSequence();
}
