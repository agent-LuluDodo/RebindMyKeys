package de.luludodo.rebindmykeys.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
    public static <K, V> void removeIf(@NotNull Map<K, V> map, BiFunction<K, V, Boolean> shouldRemove) {
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
    public static <K, V> void removeIf(@NotNull Map<K, V> map, Function<K, Boolean> shouldRemove) {
        Set<K> keySet = new HashSet<>(map.keySet());
        for (K key : keySet) {
            if (shouldRemove.apply(key)) {
                map.remove(key);
            }
        }
    }

    @Contract(pure = true)
    public static <K, V> Map<K, V> sort(@NotNull Map<K, V> map, Comparator<Map.Entry<K, V>> compare) {
        LinkedHashMap<K, V> sortedMap = new LinkedHashMap<>();
        map.entrySet().stream().sorted(compare).forEachOrdered(entry -> {
            sortedMap.put(entry.getKey(), entry.getValue());
        });
        return sortedMap;
    }

    @Contract(pure = true)
    public static <K extends Comparable<K>, V> Map<K, V> sortByKey(@NotNull Map<K, V> map) {
        return sort(map, Map.Entry.comparingByKey());
    }

    @Contract(pure = true)
    public static <K, V> Map<K, V> sortByKey(@NotNull Map<K, V> map, Comparator<K> compare) {
        return sort(map, (entry1, entry2) -> compare.compare(entry1.getKey(), entry2.getKey()));
    }

    @Contract(pure = true)
    public static <K, V extends Comparable<V>> Map<K, V> sortByValue(@NotNull Map<K, V> map) {
        return sort(map, Map.Entry.comparingByValue());
    }

    @Contract(pure = true)
    public static <K, V> Map<K, V> sortByValue(@NotNull Map<K, V> map, Comparator<V> compare) {
        return sort(map, (entry1, entry2) -> compare.compare(entry1.getValue(), entry2.getValue()));
    }
}
