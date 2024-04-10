package de.luludodo.rebindmykeys.util.interfaces;

/**
 * {@link Runnable}, but for singe-threaded execution
 */
public interface Action {
    /**
     * Executes the {@link Action}.
     */
    void run();
}