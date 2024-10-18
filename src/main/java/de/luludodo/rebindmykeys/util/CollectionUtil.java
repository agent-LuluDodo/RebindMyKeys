package de.luludodo.rebindmykeys.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Various utilities for {@link Collection Collections}.
 */
@SuppressWarnings("unused")
public class CollectionUtil {
    @SuppressWarnings("unused")
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
         * @return The moved element.
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
    public static <T> boolean all(Collection<T> collection, Function<T, Boolean> condition) {
        boolean valid = true;
        for (T element : collection) {
            if (!condition.apply(element)) valid = false;
        }
        return valid;
    }

    /**
     * Checks if the {@code condition} is valid for at least one element in the {@code collection}.
     * @param collection The collection to be checked.
     * @param condition The condition.
     * @return {@code true} if at least one condition returned true otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code collection}.
     */
    @Contract(pure = true)
    public static <T> boolean any(Collection<T> collection, Function<T, Boolean> condition) {
        return !all(collection, element -> !condition.apply(element)); // Checks if not all conditions are invalid -> means at least one was valid
    }

    /**
     * Checks if the {@code condition} is valid for no element in the {@code collection}.
     * @param collection The collection to be checked.
     * @param condition The condition.
     * @return {@code true} if no condition returned true otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code collection}.
     */
    @Contract(pure = true)
    public static <T> boolean no(Collection<T> collection, Function<T, Boolean> condition) {
        return all(collection, element -> !condition.apply(element)); // Checks if all conditions are invalid -> means none were valid
    }

    /**
     * Checks if the {@code condition} is valid for all elements in the {@code collection}.
     * @param collection The collection to be checked.
     * @param condition The condition.
     * @return {@code true} if all conditions returned true otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code collection}.
     */
    @Contract(pure = true)
    public static <T> boolean all(Collection<?> collection, Class<T> targetCl, Function<T, Boolean> condition) {
        boolean valid = true;
        for (Object element : collection) {
            if (targetCl.isInstance(element) && !condition.apply(targetCl.cast(element))) valid = false;
        }
        return valid;
    }

    /**
     * Checks if the {@code condition} is valid for at least one element in the {@code collection}.
     * @param collection The collection to be checked.
     * @param condition The condition.
     * @return {@code true} if at least one condition returned true otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code collection}.
     */
    @Contract(pure = true)
    public static <T> boolean any(Collection<?> collection, Class<T> targetCl, Function<T, Boolean> condition) {
        return !all(collection, targetCl, element -> !condition.apply(element)); // Checks if not all conditions are invalid -> means at least one was valid
    }

    /**
     * Checks if the {@code condition} is valid for no element in the {@code collection}.
     * @param collection The collection to be checked.
     * @param condition The condition.
     * @return {@code true} if no condition returned true otherwise {@code false}.
     * @param <T> The {@link Class} of the elements of the {@code collection}.
     */
    @Contract(pure = true)
    public static <T> boolean no(Collection<?> collection, Class<T> targetCl, Function<T, Boolean> condition) {
        return all(collection, targetCl, element -> !condition.apply(element)); // Checks if all conditions are invalid -> means none were valid
    }

    /**
     *
     */
    @Contract(pure = true)
    public static <T, V extends Comparable<V>> V max(Collection<T> collection, Function<T, V> value) {
        return max(collection, value, Comparable::compareTo);
    }

    /**
     *
     */
    @Contract(pure = true)
    public static <T, V> V max(Collection<T> collection, Function<T, V> value, Comparator<V> compare) {
        V max = null;
        for (T element : collection) {
            V current = value.apply(element);
            if (max == null || compare.compare(max, current) < 0) {
                max = current;
            }
        }
        return max;
    }

    @Contract(pure = true)
    public static <T> boolean shareOneOrMoreElements(Collection<T> collection1, Collection<T> collection2) {
        for (T element : collection1) {
            if (collection2.contains(element)) return true;
        }
        return false;
    }

    @SafeVarargs
    public static <C extends Collection<? super E>, E> C add(C collection, E... entries) {
        collection.addAll(Arrays.asList(entries));
        return collection;
    }

    @SafeVarargs
    public static <E, E1 extends E, E2 extends E> List<E> merge(Collection<E1> collection1, E2... collection2) {
        List<E> merged = new ArrayList<>();
        merged.addAll(collection1);
        merged.addAll(Arrays.asList(collection2));
        return merged;
    }

    /**
     * @throws ClassCastException if an object from objects cannot be cast to the targetCl (and isn't a collection or array)
     */
    public static <E> List<E> mergeAndUnwrapAs(Class<E> targetCl, Object... objects) {
        if (targetCl.isArray() || Collection.class.isAssignableFrom(targetCl))
            throw new IllegalArgumentException("Unsupported target class: " + targetCl);

        List<E> merged = new ArrayList<>();
        for (Object object : objects) {
            merged.addAll(unwrapToList(targetCl, object));
        }
        return merged;
    }

    private static <E> List<E> unwrapToList(Class<E> targetCl, Object o) {
        if (o == null)
            return List.of();

        List<E> unwrapped = new ArrayList<>();
        if (o instanceof Collection<?> collection) {
            for (Object element : collection) {
                unwrapped.addAll(unwrapToList(targetCl, element));
            }
        } else if (o.getClass().isArray()) {
            for (Object element : (Object[]) o) {
                unwrapped.addAll(unwrapToList(targetCl, element));
            }
        } else if (targetCl.isInstance(o)) {
            unwrapped.add(targetCl.cast(o));
        } else {
            throw new ClassCastException("Cannot cast " + o.getClass().getName() + " to " + targetCl.getName());
        }
        return unwrapped;
    }

    public static <E, E1 extends E, E2 extends E> List<E> merge(Collection<E1> collection1, Collection<E2> collection2) {
        List<E> merged = new ArrayList<>();
        merged.addAll(collection1);
        merged.addAll(collection2);
        return merged;
    }

    public static <T> void forEach(Collection<?> collection, Class<T> targetCl, Consumer<T> consumer) {
        for (Object entry : collection) {
            if (targetCl.isInstance(entry)) {
                consumer.accept(targetCl.cast(entry));
            }
        }
        collection.iterator();
    }

    public static <E, C extends Collection<E>> C copy(Collection<E> original, Supplier<C> newCollection, Function<E, E> copy) {
        C collection = newCollection.get();
        for (E element : original) {
            collection.add(copy.apply(element));
        }
        return collection;
    }

    @SafeVarargs
    @Contract(pure = true)
    public static <E> Set<E> join(Set<E>... sets) {
        return join(HashSet::new, sets);
    }

    @SafeVarargs
    @Contract(pure = true)
    public static <E> List<E> join(List<E>... lists) {
        return join(ArrayList::new, lists);
    }

    @SafeVarargs
    @Contract(pure = true)
    public static <C extends Collection<E>, E> C join(Supplier<C> createNew, Collection<E>... collections) {
        C joined = createNew.get();
        for (Collection<E> collection : collections) {
            joined.addAll(collection);
        }
        return joined;
    }

    @Contract(pure = true)
    public static <C extends Collection<E>, E> C joinCollection(Supplier<C> createNew, Collection<Collection<E>> collections) {
        C joined = createNew.get();
        for (Collection<E> collection : collections) {
            joined.addAll(collection);
        }
        return joined;
    }

    /**
     * TODO: better explanation
     * Use this if the merged collection has a wildcard (?) <p>
     * Example: <br>
     * {@code List<? extends String> joined = joinCollectionWildcard(...)}
     */
    @Contract(pure = true)
    public static <C extends Collection<E>, E> C joinCollectionWildcard(Supplier<C> createNew, Collection<Collection<? extends E>> collections) {
        C joined = createNew.get();
        for (Collection<? extends E> collection : collections) {
            joined.addAll(collection);
        }
        return joined;
    }

    public static <R> List<R> run(Collection<Supplier<R>> suppliers) {
        List<R> result = new ArrayList<>();
        for (Supplier<R> supplier : suppliers) {
            result.add(supplier.get());
        }
        return result;
    }

    public static <E, R> List<R> run(Collection<E> elements, Function<E, R> function) {
        List<R> result = new ArrayList<>();
        for (E element : elements) {
            result.add(function.apply(element));
        }
        return result;
    }

    public static <E> String toString(Collection<E> collection, Function<E, String> toString, String start, String delimiter, String end) {
        StringBuilder builder = new StringBuilder(start);
        boolean first = true;
        for (E element : collection) {
            if (first) {
                first = false;
            } else {
                builder.append(delimiter);
            }
            builder.append(toString.apply(element));
        }
        builder.append(end);
        return builder.toString();
    }

    public static String toString(Collection<?> collection, String start, String delimiter, String end) {
        return toString(collection, String::valueOf, start, delimiter, end);
    }

    public static String toString(Collection<?> collection) {
        return toString(collection, "[", ", ", "]");
    }

    public static <E> MutableText toText(Collection<E> collection, Function<E, Text> toText, Text start, Text delimiter, Text end) {
        MutableText text = start.copy();
        boolean first = true;
        for (E element : collection) {
            if (first) {
                first = false;
            } else {
                text.append(delimiter);
            }
            text.append(toText.apply(element));
        }
        text.append(end);
        return text;
    }

    public static <E> MutableText toText(Collection<E> collection, Function<E, Text> toText, String start, String delimiter, String end) {
        return toText(collection, toText, Text.literal(start), Text.literal(delimiter), Text.literal(end));
    }

    public static <E, R> R combine(Collection<E> collection, R initial, BiFunction<E, R, R> combine) {
        R result = initial;
        for (E element : collection) {
            result = combine.apply(element, result);
        }
        return result;
    }

    public static <E> int combineInt(Collection<E> collection, Function<E, Integer> toInt) {
        return combine(collection, 0, (element, value) -> value + toInt.apply(element));
    }
}
