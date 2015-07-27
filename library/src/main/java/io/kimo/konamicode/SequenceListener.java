package io.kimo.konamicode;

/**
 * SequenceListener
 *
 * Listeners of sequenced actions are implemented with this contract.
 */
public interface SequenceListener {

    boolean onSequenceAchieved();
    boolean validSequence();

    void resetSequence();
}
