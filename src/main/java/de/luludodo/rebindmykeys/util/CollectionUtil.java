package de.luludodo.rebindmykeys.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class CollectionUtil {
    public static boolean shareOneOrMoreElements(Collection<?> c1, Collection<?> c2) {
        if (c1 == null || c2 == null)
            return false;
        for (Object o : c1) {
            if (c2.contains(o))
                return true;
        }
        return false;
    }

    @Contract(value = "null, null -> true;null, !null -> false;!null, null -> false", pure = true)
    public static boolean equalsIgnoreOrder(Collection<?> c1, Collection<?> c2) {
        if (c1 == c2)
            return true;
        if (c1 == null || c2 == null)
            return false;
        List<?> copy = new ArrayList<>(c2);
        for (Object o : c1) {
            if (!copy.remove(o))
                return false;
        }
        return copy.isEmpty();
    }

    /**
     * @return {@code true} if c1 contains c2 in any order
     */
    @Contract(value = "null, null -> true;null, !null -> false;!null, null -> false", pure = true)
    public static boolean containsAllOf(Collection<?> c1, Collection<?> c2) {
        if (c1 == c2)
            return true;
        if (c1 == null || c2 == null)
            return false;
        List<?> copy = new ArrayList<>(c2);
        for (Object o : c1) {
            copy.remove(o);
        }
        return copy.isEmpty();
    }

    @Contract(pure = true)
    public static <K, V> void ifPresent(@NotNull Map<K, V> m, K k, Consumer<V> action) {
        if (m.containsKey(k)) {
            action.accept(m.get(k));
        }
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T> void reverseForEach(Collection<T> c, Consumer<T> action) {
        Object[] reverse = new Object[c.size()];
        AtomicInteger index = new AtomicInteger(c.size());
        c.forEach(t -> reverse[index.decrementAndGet()] = t);
        for (Object o : reverse) {
            action.accept((T) o);
        }
    }
}
