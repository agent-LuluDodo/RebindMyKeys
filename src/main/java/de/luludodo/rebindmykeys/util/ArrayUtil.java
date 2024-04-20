package de.luludodo.rebindmykeys.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Some utilities for working with arrays.
 */
public class ArrayUtil {
    /**
     * Checks if the {@code condition} is valid for all elements in the {@code array}.
     * @param array The array to be checked.
     * @param condition The condition.
     * @return {@code true} if all conditions returned true otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code array}.
     */
    @Contract(pure = true)
    public static <T> boolean allConditions(T[] array, Function<T, Boolean> condition) {
        for (T element : array) {
            if (!condition.apply(element)) return false;
        }
        return true;
    }

    /**
     * Checks if the {@code condition} is valid for at least one element in the {@code array}.
     * @param array The array to be checked.
     * @param condition The condition.
     * @return {@code true} if at least one condition returned true otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code array}.
     */
    @Contract(pure = true)
    public static <T> boolean oneCondition(T[] array, Function<T, Boolean> condition) {
        return !allConditions(array, element -> !condition.apply(element)); // Checks if not all conditions are invalid -> means at least one was valid
    }

    /**
     * Checks if the {@code condition} is valid for no element in the {@code array}.
     * @param array The array to be checked.
     * @param condition The condition.
     * @return {@code true} if no condition returned true otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code array}.
     */
    @Contract(pure = true)
    public static <T> boolean noCondition(T[] array, Function<T, Boolean> condition) {
        return allConditions(array, element -> !condition.apply(element)); // Checks if all conditions are invalid -> means none were valid
    }

    /**
     * Checks if the {@code array} contains the element {@code find}.
     * @param array The array which should be searched.
     * @param find The element to search for.
     * @return {@code true} if the {@code array} contains {@code find} otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code array}.
     */
    public static <T> boolean contains(T[] array, T find) {
        for (T t : array) {
            if (t.equals(find)) return true;
        }
        return false;
    }

    /**
     * Checks if the {@code array} and {@code find} contain at least one element.
     * @param array The array which should be searched.
     * @param find The element to search for.
     * @return {@code true} if the {@code array} contains {@code find} otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code array}.
     */
    @SafeVarargs
    public static <T> boolean shareOneOrMoreElements(T[] array, T... find) {
        for (T t : array) {
            if (contains(find, t)) return true;
        }
        return false;
    }

    /**
     * Converts an array into a {@link String}. The resulting {@link String} will look like this: {@code [toString.apply(element1), toString.apply(element2)]}
     * @param array The array to be converted.
     * @param toString The function used to convert the elements of the {@code array} into {@link String}.
     * @return The converted {@code array}.
     * @param <T> The {@link Class} of the elements of the {@code array}.
     */
    public static <T> @NotNull String arrayToString(T[] array, Function<T, String> toString) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (T t : array) {
            if (first) {
                first = false;
                builder.append('[');
            } else {
                builder.append(", ");
            }
            builder.append(toString.apply(t));
        }
        return builder.append(']').toString();
    }
}
