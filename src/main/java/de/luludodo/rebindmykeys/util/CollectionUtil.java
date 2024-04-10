package de.luludodo.rebindmykeys.util;

import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Various utilities for {@link Collection Collections}.
 */
public class CollectionUtil {
    public static class CList {
        /**
         * Moves an element from one index of a {@link List} to another. <br>
         * <b>Examples:</b><br>
         * <table>
         *     <tr>
         *         <th><center>{@link List<T> List&lt;T&gt;} list</center></th>
         *         <th><center>{@link Integer int} startPos</center></th>
         *         <th><center>{@link Integer int} endPos</center></th>
         *         <th><center>Result</center></th>
         *     </tr>
         *     <tr>
         *         <td><center>["foo", "bar", "baz", "qux"]</center></td>
         *         <td><center>0</center></td>
         *         <td><center>1</center></td>
         *         <td><center>[<strong>"bar"</strong>, <b>"foo"</b>, "baz", "qux"]</center></td>
         *     </tr>
         *     <tr>
         *         <td><center>["foo", "bar", "baz", "qux"]</center></td>
         *         <td><center>2</center></td>
         *         <td><center>2</center></td>
         *         <td><center>["foo", "bar", <b>"baz"</b>, "qux"]</center></td>
         *     </tr>
         *     <tr>
         *         <td><center>["foo", "bar", "baz", "qux"]</center></td>
         *         <td><center>3</center></td>
         *         <td><center>1</center></td>
         *         <td><center>["foo", <b>"qux"</b>, <strong>"bar"</strong>, <strong>"baz"</strong>]</center></td>
         *     </tr>
         * </table>
         * @param list The {@link List} to modify.
         * @param startPos The original position of the element.
         * @param endPos The target position of the element.
         * @return The modified {@link List}.
         * @param <T> The {@link Class} of the elements of the {@link List}.
         * @throws IllegalArgumentException If {@code startPos} < 0 or {@code startPos} > ({@link List#size() list.size()} - 1)<br>
         * If {@code endPos} < 0 or {@code endPos} > ({@link List#size() list.size()} - 1)<br>
         */
        public static <T> T move(List<T> list, int startPos, int endPos) {
            ObjectUtil.ONumber.requireRange(startPos, 0, list.size() - 1);
            ObjectUtil.ONumber.requireRange(endPos, 0, list.size() - 1);
            T element = list.remove(startPos);
            list.add(endPos, element);
            return element;
        }

        /**
         * Moves an element of a {@link List} to the specified index. <br>
         * <b>Examples:</b><br>
         * <table>
         *     <tr>
         *         <th><center>{@link List<T> List&lt;T&gt;} list</center></th>
         *         <th><center>{@link T} element</center></th>
         *         <th><center>{@link Integer int} endPos</center></th>
         *         <th><center>Result</center></th>
         *     </tr>
         *     <tr>
         *         <td><center>["foo", "bar", "baz", "qux"]</center></td>
         *         <td><center>"foo"</center></td>
         *         <td><center>3</center></td>
         *         <td><center>[<strong>"bar"</strong>, <strong>"baz"</strong>, <strong>"qux"</strong>, <b>"foo"</b>]</center></td>
         *     </tr>
         *     <tr>
         *         <td><center>["foo", "bar", "baz", "qux"]</center></td>
         *         <td><center>"qux"</center></td>
         *         <td><center>1</center></td>
         *         <td><center>["foo", <b>"qux"</b>, <strong>"bar"</strong>, <strong>"baz"</strong>]</center></td>
         *     </tr>
         *     <tr>
         *         <td><center>["foo", "bar", "baz", "qux"]</center></td>
         *         <td><center>"baz"</center></td>
         *         <td><center>2</center></td>
         *         <td><center>["foo", "bar", <b>"baz"</b>, "qux"]</center></td>
         *     </tr>
         * </table>
         * @param list The {@link List} to modify.
         * @param element The element to move.
         * @param endPos The target position of the element.
         * @return The modified {@link List}.
         * @param <T> The {@link Class} of the elements of the {@link List}.
         * @throws IllegalArgumentException If {@code endPos} < 0 or {@code endPos} > ({@link List#size() list.size()} - 1)<br>
         */
        public static <T> boolean move(List<T> list, T element, int endPos) {
            ObjectUtil.ONumber.requireRange(endPos, 0, list.size() - 1);
            if (!list.remove(element)) return false;
            list.add(endPos, element);
            return true;
        }
    }

    /**
     * Checks if the {@code condition} is valid for all elements in the {@code collection}.
     * @param collection The collection to be checked.
     * @param condition The condition.
     * @return {@code true} if all conditions returned true otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code collection}.
     */
    @Contract(pure = true)
    public static <T> boolean allConditions(Collection<T> collection, Function<T, Boolean> condition) {
        for (T element : collection) {
            if (!condition.apply(element)) return false;
        }
        return true;
    }

    /**
     * Checks if the {@code condition} is valid for at least one element in the {@code collection}.
     * @param collection The collection to be checked.
     * @param condition The condition.
     * @return {@code true} if at least one condition returned true otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code collection}.
     */
    @Contract(pure = true)
    public static <T> boolean oneCondition(Collection<T> collection, Function<T, Boolean> condition) {
        return !allConditions(collection, element -> !condition.apply(element)); // Checks if not all conditions are invalid -> means at least one was valid
    }

    /**
     * Checks if the {@code condition} is valid for no element in the {@code collection}.
     * @param collection The collection to be checked.
     * @param condition The condition.
     * @return {@code true} if no condition returned true otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code collection}.
     */
    @Contract(pure = true)
    public static <T> boolean noCondition(Collection<T> collection, Function<T, Boolean> condition) {
        return allConditions(collection, element -> !condition.apply(element)); // Checks if all conditions are invalid -> means none were valid
    }

    @Contract(pure = true)
    public static <T> boolean shareOneOrMoreElements(Collection<T> collection1, Collection<T> collection2) {
        for (T element : collection1) {
            if (collection2.contains(element)) return true;
        }
        return false;
    }
}
