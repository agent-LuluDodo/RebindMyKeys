package de.luludodo.rebindmykeys.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObjectUtil {
    @Contract(value = "null, _ -> fail; _, _ -> !null", pure = true)
    public static <O> @NotNull O require(Object o, Class<O> cl) {
        if (o == null) {
            throw new NullPointerException("Expected " + cl.getSimpleName() + " but found null");
        } else if (cl.isInstance(o)) {
            return cl.cast(o);
        } else {
            throw new ClassCastException("Expected " + cl.getSimpleName() + " but found " + o.getClass().getSimpleName());
        }
    }


    @Contract(value = "null, _, _ -> fail; _, _, _ -> !null", pure = true)
    public static @NotNull Object requireArray(Object o, Class<?> cl, @Range(from = 0, to = Integer.MAX_VALUE) int dimensions) {
        for (int dimension = 0; dimension < dimensions; dimension++) {
            cl = cl.arrayType();
        }
        return require(o, cl);
    }

    @Contract(value = "null, _ -> fail; _, _ -> !null", pure = true)
    @SuppressWarnings("unchecked")
    public static <O> @NotNull O[] requireArray(Object o, Class<O> cl) {
        return (O[]) requireArray(o, cl, 1);
    }

    @Contract(value = "null, _ -> fail; _, _ -> !null", pure = true)
    @SuppressWarnings("unchecked")
    public static <O> @NotNull O[][] require2DArray(Object o, Class<O> cl) {
        return (O[][]) requireArray(o, cl, 2);
    }

    @Contract(value = "null, _ -> fail", pure = true)
    public static void requireOneOf(Object o, Class<?>... cls) {
        if (o == null) {
            throw new NullPointerException("Expected one of " + arrayToString(cls, Class::getSimpleName) + " but found null");
        }
        for (Class<?> cl : cls) {
            if (cl.isInstance(o)) {
                return;
            }
        }
        throw new ClassCastException("Expected one of " + arrayToString(cls, Class::getSimpleName) + " but found " + o.getClass().getSimpleName());
    }

    public static class ClassCase1<T> {
        private final Class<T> cl;
        private final Consumer<T> action;
        private ClassCase1(Class<T> cl, Consumer<T> action) {
            this.cl = cl;
            this.action = action;
        }

        public String getSimpleName() {
            return cl.getSimpleName();
        }

        public boolean isInstance(Object o) {
            return cl.isInstance(o);
        }

        public void accept(Object o) {
            action.accept(cl.cast(o));
        }
    }
    public static class ClassCase2<T, R> {
        private final Class<T> cl;
        private final Function<T, R> action;
        private ClassCase2(Class<T> cl, Function<T, R> action) {
            this.cl = cl;
            this.action = action;
        }

        public String getSimpleName() {
            return cl.getSimpleName();
        }

        public boolean isInstance(Object o) {
            return cl.isInstance(o);
        }

        public R apply(Object o) {
            return action.apply(cl.cast(o));
        }
    }
    @Contract(value = "null, _ -> fail", pure = true)
    public static void requireOneOf(Object o, ClassCase1<?>... cls) {
        if (o == null) {
            throw new NullPointerException("o is null excepted one of " + arrayToString(cls, ClassCase1::getSimpleName));
        }
        for (ClassCase1<?> cl : cls) {
            if (cl.isInstance(o)) {
                cl.accept(o);
                return;
            }
        }
        throw new ClassCastException("o is " + o.getClass().getSimpleName() + " excepted one of " + arrayToString(cls, ClassCase1::getSimpleName));
    }
    @SafeVarargs
    @Contract(value = "null, _ -> fail", pure = true)
    public static <R> R requireOneOf(Object o, ClassCase2<?, R>... cls) {
        if (o == null) {
            throw new NullPointerException("o is null excepted one of " + arrayToString(cls, ClassCase2::getSimpleName));
        }
        for (ClassCase2<?, R> cl : cls) {
            if (cl.isInstance(o)) {
                return cl.apply(o);
            }
        }
        throw new ClassCastException("o is " + o.getClass().getSimpleName() + " excepted one of " + arrayToString(cls, ClassCase2::getSimpleName));
    }
    @Contract(value = "null, _ -> fail", pure = true)
    public static void requireOneOfForEach(Collection<?> collection, ClassCase1<?>... cls) {
        collection.forEach(c -> requireOneOf(c, cls));
    }
    @SafeVarargs
    @Contract(value = "null, _ -> fail", pure = true)
    public static <R> List<R> requireOneOfForEach(Collection<?> collection, ClassCase2<?, R>... cls) {
        List<R> results = new ArrayList<>(collection.size());
        collection.forEach(c -> results.add(requireOneOf(c, cls)));
        return results;
    }
    @Contract(value = "null, _ -> fail", pure = true)
    public static void requireOneOfForEachKey(Map<?, ?> map, ClassCase1<?>... cls) {
        map.forEach((k, v) -> requireOneOf(k, cls));
    }
    @SafeVarargs
    @Contract(value = "null, _ -> fail", pure = true)
    public static <V, R> Map<R, V> requireOneOfForEachKey(Map<?, V> map, ClassCase2<?, R>... cls) {
        Map<R, V> results = new HashMap<>(map.size());
        map.forEach((k, v) -> results.put(requireOneOf(k, cls), v));
        return results;
    }
    @Contract(value = "null, _ -> fail", pure = true)
    public static void requireOneOfForEachValue(Map<?, ?> map, ClassCase1<?>... cls) {
        map.forEach((k, v) -> requireOneOf(v, cls));
    }
    @SafeVarargs
    @Contract(value = "null, _ -> fail", pure = true)
    public static <K, R> Map<K, R> requireOneOfForEachValue(Map<K, ?> map, ClassCase2<?, R>... cls) {
        Map<K, R> results = new HashMap<>(map.size());
        map.forEach((k, v) -> results.put(k, requireOneOf(v, cls)));
        return results;
    }
    public static <T> ClassCase1<T> classCase(Class<T> cl, Consumer<T> action) {
        return new ClassCase1<>(cl, action);
    }
    public static <T, R> ClassCase2<T, R> classCase(Class<T> cl, Function<T, R> action) {
        return new ClassCase2<>(cl, action);
    }

    public static <T> @NotNull String arrayToString(T[] array, Function<T, String> toString) {
        StringBuilder builder = new StringBuilder();
        for (T t : array) {
            builder.append(toString.apply(t));
        }
        return builder.toString();
    }
}
