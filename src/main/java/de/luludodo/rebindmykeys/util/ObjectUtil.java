package de.luludodo.rebindmykeys.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static de.luludodo.rebindmykeys.util.ArrayUtil.arrayToString;

/**
 * Various Utilities for dealing with {@link Object Objects}.
 */
public class ObjectUtil {
    /**
     * Requires the {@link Object} {@code o} to be an instance of the {@link Class} {@code cl}.
     * Otherwise, throws an Exception.
     * @param o The {@link Object} to check.
     * @param cl The target {@link Class}
     * @return The {@link Object} {@code o} cast to the target {@link Class} {@code cl}.
     * @param <O> The target {@link Class} as a generic.
     * @throws NullPointerException If the {@link Object} {@code o} is {@code null}.
     * @throws ClassCastException If the {@link Object} {@code o} isn't an instance of the {@link Class} {@code cl}.
     */
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

    public static class ONumber {
        /**
         * Compares two {@link Number Numbers} of the same {@link Class}.
         * @return <strong>0</strong> if {@code number1} = {@code number2}. <br>
         * <strong><0</strong> if {@code number1} < {@code number2}. <br>
         * <strong>>0</strong> if {@code number1} > {@code number2}.
         */
        public static <N extends Number> int compare(N number1, N number2) {
            return requireOneOf(number1,
                    classCase(
                            Byte.class,
                            byte1 -> {
                                return byte1.compareTo((Byte) number2);
                            }
                    ),
                    classCase(
                            Short.class,
                            short1 -> {
                                return short1.compareTo((Short) number2);
                            }
                    ),
                    classCase(
                            Integer.class,
                            int1 -> {
                                return int1.compareTo((Integer) number2);
                            }
                    ),
                    classCase(
                            Long.class,
                            long1 -> {
                                return long1.compareTo((Long) number2);
                            }
                    ),
                    classCase(
                            Float.class,
                            float1 -> {
                                return float1.compareTo((Float) number2);
                            }
                    ),
                    classCase(
                            Double.class,
                            double1 -> {
                                return double1.compareTo((Double) number2);
                            }
                    )
            );
        }

        public static <N extends Number> @NotNull N requireRange(N number, N min, N max) {
            if (compare(number, min) > 0 && compare(number, max) < 0)
                throw new IllegalArgumentException(number + " is not in the range " + min + " to " + max);
            return number;
        }
    }

    /**
     * Requires the {@link Object} {@code o} to be a {@code n-dimensional} Array of the {@link Class} {@code cl}.
     * Otherwise, throws an Exception.
     * <br>
     * <b>What is a dimension?</b> A dimension is one more parameter an Array takes.
     * For example, an 1-dimensional Array is O[] a 2-dimensional one is O[][], etc.
     * @param o The {@link Object} to check.
     * @param cl The target {@link Class}
     * @param dimensions The amount of {@code dimensions} the Array should have.
     * @return The {@link Object} {@code o} in its original form (can't cast because of technical limitations).
     * @throws NullPointerException If the {@link Object} {@code o} is {@code null}.
     * @throws ClassCastException If the {@link Object} {@code o} isn't an instance of the {@link Class} {@code cl}
     * or has the incorrect amount of dimensions.
     * @see ObjectUtil#require1DArray(Object, Class)
     * @see ObjectUtil#require2DArray(Object, Class)
     */
    @Contract(value = "null, _, _ -> fail; _, _, _ -> !null", pure = true)
    public static @NotNull Object requireArray(Object o, Class<?> cl, @Range(from = 0, to = Integer.MAX_VALUE) int dimensions) {
        for (int dimension = 0; dimension < dimensions; dimension++) {
            cl = cl.arrayType();
        }
        return require(o, cl);
    }

    /**
     * Requires the {@link Object} {@code o} to be a {@code 1-dimensional} ({@code O[]}) Array of the {@link Class} {@code cl}.
     * Otherwise, throws an Exception.
     * @param o The {@link Object} to check.
     * @param cl The target {@link Class}
     * @return The {@link Object} {@code o} cast to {@code O[]}.
     * @throws NullPointerException If the {@link Object} {@code o} is {@code null}.
     * @throws ClassCastException If the {@link Object} {@code o} isn't an instance of the {@link Class} {@code cl}
     * or has the incorrect amount of dimensions.
     * @see ObjectUtil#requireArray(Object, Class, int)
     * @see ObjectUtil#require2DArray(Object, Class)
     */
    @Contract(value = "null, _ -> fail; _, _ -> !null", pure = true)
    @SuppressWarnings("unchecked")
    public static <O> @NotNull O[] require1DArray(Object o, Class<O> cl) {
        return (O[]) requireArray(o, cl, 1);
    }

    /**
     * Requires the {@link Object} {@code o} to be a {@code 2-dimensional} ({@code O[][]}) Array of the {@link Class} {@code cl}.
     * Otherwise, throws an Exception.
     * @param o The {@link Object} to check.
     * @param cl The target {@link Class}
     * @return The {@link Object} {@code o} cast to {@code O[][]}.
     * @throws NullPointerException If the {@link Object} {@code o} is {@code null}.
     * @throws ClassCastException If the {@link Object} {@code o} isn't an instance of the {@link Class} {@code cl}
     * or has the incorrect amount of dimensions.
     * @see ObjectUtil#requireArray(Object, Class, int)
     * @see ObjectUtil#require1DArray(Object, Class)
     */
    @Contract(value = "null, _ -> fail; _, _ -> !null", pure = true)
    @SuppressWarnings("unchecked")
    public static <O> @NotNull O[][] require2DArray(Object o, Class<O> cl) {
        return (O[][]) requireArray(o, cl, 2);
    }

    /**
     * Requires the {@link Object} {@code o} to be an instance of one of the {@link Class Classes} {@code cls}.
     * Otherwise, throws an Exception.
     * @param o The {@link Object} to check.
     * @param cls The target {@link Class Classes}.
     * @return The {@link Object} {@code o} in its original form (can't cast because of technical limitations).
     * @throws NullPointerException If the {@link Object} {@code o} is {@code null}.
     * @throws ClassCastException If the {@link Object} {@code o} isn't an instance of any {@link Class} from {@code cls}.
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static Object requireOneOf(Object o, Class<?>... cls) {
        if (o == null) {
            throw new NullPointerException("Expected one of " + arrayToString(cls, Class::getSimpleName) + " but found null");
        }
        for (Class<?> cl : cls) {
            if (cl.isInstance(o)) {
                return o;
            }
        }
        throw new ClassCastException("Expected one of " + arrayToString(cls, Class::getSimpleName) + " but found " + o.getClass().getSimpleName());
    }

    /**
     * Case part of the {@link ObjectUtil#requireOneOf(Object, ClassCase1[])} {@code switch} statement.
     * @param <T> {@link Class} of the case.
     * @see ObjectUtil#requireOneOf(Object, ClassCase1[])
     * @see ClassCase2
     */
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

    /**
     * Case part of the {@link ObjectUtil#requireOneOf(Object, ClassCase2[])} {@code switch} statement.
     * @param <T> {@link Class} of the case.
     * @see ObjectUtil#requireOneOf(Object, ClassCase2[])
     * @see ClassCase1
     */
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

    /**
     * Executes the {@link ClassCase1} which corresponds to the {@link Class} of the {@link Object} {@code o}.
     * <p>
     *     <b>How do I use this?</b> This is basically a switch statement.
     *     {@code o} is the argument and {@code cls} are the cases. <br>
     *     Only the <i>first</i> match will be executed.
     *     Here is an example of how to use this:
     * <pre>
     * {@link ObjectUtil#requireOneOf(Object, ClassCase1[]) requireOneOf}{@code (o,}
     *     {@link ObjectUtil#classCase(Class, Consumer) classCase}{@code ( // functions like case in a switch statement}
     *         {@link Class<String> String.class}{@code , // this case will be executed if o is an instance of String
     *          s} {@link Consumer<String> ->}{@code {
     *              // do whatever you want with the String cast from o here
     *          }
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Consumer) classCase}{@code (}
     *         {@link Class<String> String.class}{@code , // this case will never be executed
     *          s} {@link Consumer<String> ->}{@code {}
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Consumer) classCase}{@code ( // functions like default in a switch statement since everything is an Object}
     *         {@link Class<Object> Object.class}{@code , // this case will be executed if o isn't an instance of String
     *          o} {@link Consumer<Object> ->}{@code {
     *              // do whatever you want with o here
     *          }
     *      )
     *  )}
     * </pre>
     * </p>
     * @param o The argument for the 'switch statement'
     * @param cls The cases for the 'switch statement'
     * @see ObjectUtil#classCase(Class, Consumer)
     * @see ObjectUtil#requireOneOf(Object, ClassCase2[])
     */
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

    /**
     * Executes the {@link ClassCase2} which corresponds to the {@link Class} of the {@link Object} {@code o}.
     * <p>
     *     <b>How do I use this?</b> This is basically a return switch statement.
     *     {@code o} is the argument and {@code cls} are the cases.
     *     Every case needs to return something of {@link Class} {@code <R>}.<br>
     *     Only the <i>first</i> match will be executed.
     *     Here is an example of how to use this:
     * <pre>
     * {@link ObjectUtil#requireOneOf(Object, ClassCase1[]) requireOneOf}{@code (o,}
     *     {@link ObjectUtil#classCase(Class, Function) classCase}{@code ( // functions like case in a switch statement}
     *         {@link Class<String> String.class}{@code , // this case will be executed if o is an instance of String
     *          s} {@link Function<String,R> ->}{@code {
     *              // do whatever you want with the String cast from o here
     *              return <R>;
     *          }
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Function) classCase}{@code (}
     *         {@link Class<String> String.class}{@code , // this case will never be executed
     *          s} {@link Function<String,R> ->}{@code {
     *              return <R>;
     *          }
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Function) classCase}{@code ( // functions like default in a switch statement since everything is an Object}
     *         {@link Class<Object> Object.class}{@code , // this case will be executed if o isn't an instance of String
     *          o} {@link Function<Object,R> ->}{@code {
     *              // do whatever you want with o here
     *              return <R>;
     *          }
     *      )
     *  )}
     * </pre>
     * </p>
     * @param o The argument for the 'return switch statement'
     * @param cls The cases for the 'return switch statement'
     * @return The result of the {@link ClassCase2} which was executed.
     * @param <R> The class of the results of the {@link ClassCase2 ObjectUtil.ClassCase2s}
     * @see ObjectUtil#classCase(Class, Function)
     * @see ObjectUtil#requireOneOf(Object, ClassCase1[])
     */
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

    /**
     * Executes the {@link ClassCase1} which corresponds to the {@link Class} of an element of the {@link Collection} {@code collection}.
     * <p>
     *     <b>How do I use this?</b> This is basically a switch statement which is executed for each element of the {@code collection}.
     *     Every element of {@code collection} is the argument and {@code cls} are the cases. <br>
     *     Only the <i>first</i> match for the current element will be executed.
     *     Here is an example of how to use this:
     * <pre>
     * {@link ObjectUtil#requireOneOfForEach(Collection, ClassCase1[]) requireOneOfForEach}{@code (collection,}
     *     {@link ObjectUtil#classCase(Class, Consumer) classCase}{@code ( // functions like case in a switch statement}
     *         {@link Class<String> String.class}{@code , // this case will be executed for every element which is an instance of String
     *          s} {@link Consumer<String> ->}{@code {
     *              // do whatever you want with the String cast from the element here
     *          }
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Consumer) classCase}{@code (}
     *         {@link Class<String> String.class}{@code , // this case will never be executed
     *          s} {@link Consumer<String> ->}{@code {}
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Consumer) classCase}{@code ( // functions like default in a switch statement since everything is an Object}
     *         {@link Class<Object> Object.class}{@code , // this case will be executed for every element which isn't an instance of String
     *          o} {@link Consumer<Object> ->}{@code {
     *              // do whatever you want with the element here
     *          }
     *      )
     *  )}
     * </pre>
     * </p>
     * @param collection The {@link Collection} which contains elements which function as arguments for the 'switch statement'
     * @param cls The cases for the 'switch statement'
     * @see ObjectUtil#classCase(Class, Consumer)
     * @see ObjectUtil#requireOneOfForEach(Collection, ClassCase2[])
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static void requireOneOfForEach(Collection<?> collection, ClassCase1<?>... cls) {
        collection.forEach(c -> requireOneOf(c, cls));
    }

    /**
     * Executes the {@link ClassCase2} which corresponds to the {@link Class} of an element of the {@link Collection} {@code collection}.
     * <p>
     *     <b>How do I use this?</b> This is basically a return switch statement which is executed for each element of the {@code collection}.
     *     Every element of {@code collection} is the argument and {@code cls} are the cases.
     *     Every case needs to return something of {@link Class} {@code <R>}.<br>
     *     Only the <i>first</i> match for the current element will be executed.
     *     Here is an example of how to use this:
     * <pre>
     * {@link ObjectUtil#requireOneOfForEach(Collection, ClassCase2[]) requireOneOfForEach}{@code (collection,}
     *     {@link ObjectUtil#classCase(Class, Function) classCase}{@code ( // functions like case in a switch statement}
     *         {@link Class<String> String.class}{@code , // this case will be executed for every element which is an instance of String
     *          s} {@link Function<String,R> ->}{@code {
     *              // do whatever you want with the String cast from the element here
     *              return <R>;
     *          }
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Function) classCase}{@code (}
     *         {@link Class<String> String.class}{@code , // this case will never be executed
     *          s} {@link Function<String,R> ->}{@code {
     *              return <R>;
     *          }
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Function) classCase}{@code ( // functions like default in a switch statement since everything is an Object}
     *         {@link Class<Object> Object.class}{@code , // this case will be executed for every element which isn't an instance of String
     *          o} {@link Function<Object,R> ->}{@code {
     *              // do whatever you want with the element here
     *              return <R>;
     *          }
     *      )
     *  )}
     * </pre>
     * </p>
     * @param collection The {@link Collection} which contains elements which function as arguments for the 'return switch statement'
     * @param cls The cases for the 'return switch statement'
     * @return The result of all the {@link ClassCase2} which were executed as a {@link List<R>}.
     * @param <R> The class of the results of the {@link ClassCase2 ObjectUtil.ClassCase2s}
     * @see ObjectUtil#classCase(Class, Function)
     * @see ObjectUtil#requireOneOfForEach(Collection, ClassCase1[])
     */
    @SafeVarargs
    @Contract(value = "null, _ -> fail", pure = true)
    public static <R> List<R> requireOneOfForEach(Collection<?> collection, ClassCase2<?, R>... cls) {
        List<R> results = new ArrayList<>(collection.size());
        collection.forEach(c -> results.add(requireOneOf(c, cls)));
        return results;
    }

    /**
     * Executes the {@link ClassCase1} which corresponds to the {@link Class} of a key of the {@link Map} {@code map}.
     * <p>
     *     <b>How do I use this?</b> This is basically a switch statement which is executed for each key of the {@code map}.
     *     Every key of {@code map} is the argument and {@code cls} are the cases. <br>
     *     Only the <i>first</i> match for the current key will be executed.
     *     Here is an example of how to use this:
     * <pre>
     * {@link ObjectUtil#requireOneOfForEachKey(Map, ClassCase1[]) requireOneOfForEachKey}{@code (map,}
     *     {@link ObjectUtil#classCase(Class, Consumer) classCase}{@code ( // functions like case in a switch statement}
     *         {@link Class<String> String.class}{@code , // this case will be executed for every key which is an instance of String
     *          s} {@link Consumer<String> ->}{@code {
     *              // do whatever you want with the String cast from the key here
     *          }
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Consumer) classCase}{@code (}
     *         {@link Class<String> String.class}{@code , // this case will never be executed
     *          s} {@link Consumer<String> ->}{@code {}
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Consumer) classCase}{@code ( // functions like default in a switch statement since everything is an Object}
     *         {@link Class<Object> Object.class}{@code , // this case will be executed for every key which isn't an instance of String
     *          o} {@link Consumer<Object> ->}{@code {
     *              // do whatever you want with the key here
     *          }
     *      )
     *  )}
     * </pre>
     * </p>
     * @param map The {@link Map} which contains keys which function as arguments for the 'switch statement'
     * @param cls The cases for the 'switch statement'
     * @see ObjectUtil#classCase(Class, Consumer)
     * @see ObjectUtil#requireOneOfForEachKey(Map, ClassCase2[])
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static void requireOneOfForEachKey(Map<?, ?> map, ClassCase1<?>... cls) {
        map.forEach((k, v) -> requireOneOf(k, cls));
    }

    /**
     * Executes the {@link ClassCase2} which corresponds to the {@link Class} of a key of the {@link Map} {@code map}.
     * <p>
     *     <b>How do I use this?</b> This is basically a return switch statement which is executed for each key of the {@code map}.
     *     Every key of {@code map} is the argument and {@code cls} are the cases.
     *     Every case needs to return something of {@link Class} {@code <R>}.<br>
     *     Only the <i>first</i> match for the current key will be executed.
     *     Here is an example of how to use this:
     * <pre>
     * {@link ObjectUtil#requireOneOfForEachKey(Map, ClassCase2[]) requireOneOfForEachKey}{@code (map,}
     *     {@link ObjectUtil#classCase(Class, Function) classCase}{@code ( // functions like case in a switch statement}
     *         {@link Class<String> String.class}{@code , // this case will be executed for every key which is an instance of String
     *          s} {@link Function<String,R> ->}{@code {
     *              // do whatever you want with the String cast from the key here
     *              return <R>;
     *          }
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Function) classCase}{@code (}
     *         {@link Class<String> String.class}{@code , // this case will never be executed
     *          s} {@link Function<String,R> ->}{@code {
     *              return <R>;
     *          }
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Function) classCase}{@code ( // functions like default in a switch statement since everything is an Object}
     *         {@link Class<Object> Object.class}{@code , // this case will be executed for every key which isn't an instance of String
     *          o} {@link Function<Object,R> ->}{@code {
     *              // do whatever you want with the key here
     *              return <R>;
     *          }
     *      )
     *  )}
     * </pre>
     * </p>
     * @param map The {@link Map} which contains keys which function as arguments for the 'return switch statement'
     * @param cls The cases for the 'return switch statement'
     * @return The result of all the {@link ClassCase2} which were executed as a {@link Map<R,V>}
     * where the keys are the results and the values the original values.
     * @param <R> The class of the results of the {@link ClassCase2 ObjectUtil.ClassCase2s}
     * @see ObjectUtil#classCase(Class, Function)
     * @see ObjectUtil#requireOneOfForEachKey(Map, ClassCase1[])
     */
    @SafeVarargs
    @Contract(value = "null, _ -> fail", pure = true)
    public static <V, R> Map<R, V> requireOneOfForEachKey(Map<?, V> map, ClassCase2<?, R>... cls) {
        Map<R, V> results = new HashMap<>(map.size());
        map.forEach((k, v) -> results.put(requireOneOf(k, cls), v));
        return results;
    }

    /**
     * Executes the {@link ClassCase1} which corresponds to the {@link Class} of a value of the {@link Map} {@code map}.
     * <p>
     *     <b>How do I use this?</b> This is basically a switch statement which is executed for each value of the {@code map}.
     *     Every value of {@code map} is the argument and {@code cls} are the cases. <br>
     *     Only the <i>first</i> match for the current value will be executed.
     *     Here is an example of how to use this:
     * <pre>
     * {@link ObjectUtil#requireOneOfForEachValue(Map, ClassCase1[]) requireOneOfForEachValue}{@code (map,}
     *     {@link ObjectUtil#classCase(Class, Consumer) classCase}{@code ( // functions like case in a switch statement}
     *         {@link Class<String> String.class}{@code , // this case will be executed for every value which is an instance of String
     *          s} {@link Consumer<String> ->}{@code {
     *              // do whatever you want with the String cast from the value here
     *          }
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Consumer) classCase}{@code (}
     *         {@link Class<String> String.class}{@code , // this case will never be executed
     *          s} {@link Consumer<String> ->}{@code {}
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Consumer) classCase}{@code ( // functions like default in a switch statement since everything is an Object}
     *         {@link Class<Object> Object.class}{@code , // this case will be executed for every value which isn't an instance of String
     *          o} {@link Consumer<Object> ->}{@code {
     *              // do whatever you want with the value here
     *          }
     *      )
     *  )}
     * </pre>
     * </p>
     * @param map The {@link Map} which contains values which function as arguments for the 'switch statement'
     * @param cls The cases for the 'switch statement'
     * @see ObjectUtil#classCase(Class, Consumer)
     * @see ObjectUtil#requireOneOfForEachValue(Map, ClassCase2[])
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static void requireOneOfForEachValue(Map<?, ?> map, ClassCase1<?>... cls) {
        map.forEach((k, v) -> requireOneOf(v, cls));
    }

    /**
     * Executes the {@link ClassCase2} which corresponds to the {@link Class} of a value of the {@link Map} {@code map}.
     * <p>
     *     <b>How do I use this?</b> This is basically a return switch statement which is executed for each value of the {@code map}.
     *     Every value of {@code map} is the argument and {@code cls} are the cases.
     *     Every case needs to return something of {@link Class} {@code <R>}.<br>
     *     Only the <i>first</i> match for the current key will be executed.
     *     Here is an example of how to use this:
     * <pre>
     * {@link ObjectUtil#requireOneOfForEachValue(Map, ClassCase2[]) requireOneOfForEachValue}{@code (map,}
     *     {@link ObjectUtil#classCase(Class, Function) classCase}{@code ( // functions like case in a switch statement}
     *         {@link Class<String> String.class}{@code , // this case will be executed for every value which is an instance of String
     *          s} {@link Function<String,R> ->}{@code {
     *              // do whatever you want with the String cast from the value here
     *              return <R>;
     *          }
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Function) classCase}{@code (}
     *         {@link Class<String> String.class}{@code , // this case will never be executed
     *          s} {@link Function<String,R> ->}{@code {
     *              return <R>;
     *          }
     *      ),}
     *     {@link ObjectUtil#classCase(Class, Function) classCase}{@code ( // functions like default in a switch statement since everything is an Object}
     *         {@link Class<Object> Object.class}{@code , // this case will be executed for every value which isn't an instance of String
     *          o} {@link Function<Object,R> ->}{@code {
     *              // do whatever you want with the value here
     *              return <R>;
     *          }
     *      )
     *  )}
     * </pre>
     * </p>
     * @param map The {@link Map} which contains values which function as arguments for the 'return switch statement'
     * @param cls The cases for the 'return switch statement'
     * @return The result of all the {@link ClassCase2} which were executed as a {@link Map<K,R>}
     * where the keys are the original keys and the keys the results.
     * @param <R> The class of the results of the {@link ClassCase2 ObjectUtil.ClassCase2s}
     * @see ObjectUtil#classCase(Class, Function)
     * @see ObjectUtil#requireOneOfForEachValue(Map, ClassCase1[])
     */
    @SafeVarargs
    @Contract(value = "null, _ -> fail", pure = true)
    public static <K, R> Map<K, R> requireOneOfForEachValue(Map<K, ?> map, ClassCase2<?, R>... cls) {
        Map<K, R> results = new HashMap<>(map.size());
        map.forEach((k, v) -> results.put(k, requireOneOf(v, cls)));
        return results;
    }

    /**
     * The case part of the 'switch statements'.
     * @param cl The target {@link Class}.
     * @param action The {@link Consumer<T>} executed the element was of the target class.
     * @return The {@link ClassCase1}.
     * @param <T> The target {@link Class} as a generic.
     * @see ObjectUtil#classCase(Class, Function)
     * @see ObjectUtil#requireOneOf(Object, ClassCase1[])
     * @see ObjectUtil#requireOneOfForEach(Collection, ClassCase1[])
     * @see ObjectUtil#requireOneOfForEachKey(Map, ClassCase1[])
     * @see ObjectUtil#requireOneOfForEachValue(Map, ClassCase1[])
     */
    public static <T> ClassCase1<T> classCase(Class<T> cl, Consumer<T> action) {
        return new ClassCase1<>(cl, action);
    }

    /**
     * The case part of the 'return switch statements'.
     * @param cl The target {@link Class}.
     * @param action The {@link Function<T,R>} executed the element was of the target class which returns the result.
     * @return The {@link ClassCase2}.
     * @param <T> The target {@link Class} as a generic.
     * @see ObjectUtil#classCase(Class, Consumer)
     * @see ObjectUtil#requireOneOf(Object, ClassCase2[])
     * @see ObjectUtil#requireOneOfForEach(Collection, ClassCase2[])
     * @see ObjectUtil#requireOneOfForEachKey(Map, ClassCase2[])
     * @see ObjectUtil#requireOneOfForEachValue(Map, ClassCase2[])
     */
    public static <T, R> ClassCase2<T, R> classCase(Class<T> cl, Function<T, R> action) {
        return new ClassCase2<>(cl, action);
    }
}