package de.luludodo.rebindmykeys.util;

public class BitUtil {
    public static boolean isBitSet(long value, int bit) {
        return (value & (1L << bit)) != 0;
    }
}
