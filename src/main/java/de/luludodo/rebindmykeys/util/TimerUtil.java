package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.RebindMyKeys;

public class TimerUtil {
    private static long startNanos = -1;
    public static void start() {
        if (startNanos != -1) throw new IllegalStateException("Timer already started");
        startNanos = System.nanoTime();
    }

    public static void stop() {
        if (startNanos == -1) throw new IllegalStateException("Timer not running");
        RebindMyKeys.DEBUG.info("Timer took {}ms", (System.nanoTime() - startNanos) / 1000000d);
        startNanos = -1;
    }
}
