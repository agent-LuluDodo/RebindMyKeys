package de.luludodo.rebindmykeys.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Some Utility functions for Maps
 */
public class MapUtil {
    /**
     * Iterates through the entire map and removes the current element if {@code shouldRemove} returns {@code true}.
     * @param map The map to iterate over and modify
     * @param shouldRemove If the current element should be removed
     * @param <K> The class of the keys of the map
     * @param <V> The class of the values of the map
     */
    public static <K, V> void removeAll(@NotNull Map<K, V> map, BiFunction<K, V, Boolean> shouldRemove) {
        Set<K> keySet = new HashSet<>(map.keySet());
        for (K key : keySet) {
            if (shouldRemove.apply(key, map.get(key))) {
                map.remove(key);
            }
        }
    }

    /**
     * Iterates through the entire map and removes the current element if {@code shouldRemove} returns {@code true}.
     * @param map The map to iterate over and modify
     * @param shouldRemove If the current element should be removed
     * @param <K> The class of the keys of the map
     * @param <V> The class of the values of the map
     */
    public static <K, V> void removeAll(@NotNull Map<K, V> map, Function<K, Boolean> shouldRemove) {
        Set<K> keySet = new HashSet<>(map.keySet());
        for (K key : keySet) {
            if (shouldRemove.apply(key)) {
                map.remove(key);
            }
        }
    }
}
