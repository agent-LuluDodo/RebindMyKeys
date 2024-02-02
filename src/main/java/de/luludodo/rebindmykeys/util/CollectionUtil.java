package de.luludodo.rebindmykeys.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
        if (c1 == null || c2 == null) {
            return c1 == c2;
        }
        List<?> copy = new ArrayList<>(c2);
        for (Object o : c1) {
            if (!copy.remove(o))
                return false;
        }
        return copy.isEmpty();
    }

    @Contract(pure = true)
    public static <K, V> void ifPresent(@NotNull Map<K, V> m, K k, Consumer<V> action) {
        if (m.containsKey(k)) {
            action.accept(m.get(k));
        }
    }
}
