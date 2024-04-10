package de.luludodo.rebindmykeys.util;

import com.google.gson.*;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import de.luludodo.rebindmykeys.util.interfaces.JsonLoadable;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A lot of utility functions all about json handling, checking, writing, loading, saving, etc.
 */
public class JsonUtil {
    /**
     * {@link Gson} instance used throughout {@link JsonUtil}.
     */
    private static final Gson GSON = new Gson();

    /**
     * Builder to easily create {@link JsonObject}'s.
     */
    public static class ObjectBuilder {
        private final JsonObject build = new JsonObject();
        private ObjectBuilder() {}

        /**
         * <p>
         *     Assigns the specified {@link Object} to the property {@code name} of the {@link JsonObject}.
         *     Follows the specifications of {@link JsonUtil#toJson(Object)}.
         * </p>
         * <p>
         *     <b>Specifications of {@link JsonUtil#toJson(Object)}:</b><br>
         *     If the {@code object} is equal to {@code null} {@link JsonNull#INSTANCE} will be returned. <br>
         *     If the {@code object} is an instance of {@link JsonSavable} the result of {@link JsonSavable#save()} will be returned. <br>
         *     If the {@code object} is an instance of {@link JsonElement} the unmodified {@code object} will be returned. <br>
         *     If the {@code object} is an instance of {@link Enum} the {@code object} parsed using {@link JEnum#toJson(Enum)} will be returned. <br>
         *     If the {@code object} is an instance of {@link Collection} the {@code object} parsed using {@link ArrayBuilder#addAll(Collection)} will be returned. <br>
         *     If the {@code object} is an Array the {@code object} parsed using {@link ArrayBuilder#addAll(Object[])} will be returned. <br>
         *     Otherwise the {@code object} parsed using {@link Gson#toJsonTree(Object)} will be returned.
         * </p>
         * @param name <code>{ <b>name</b>: value }</code>
         * @param value <code>{ name: <b>value</b> }</code>
         * @return {@code this}
         * @see JsonUtil#toJson(Object)
         */
        public ObjectBuilder add(String name, Object value) {
            build.add(name, toJson(value));
            return this;
        }

        /**
         * Adds all elements contained in the {@link Map} as specified by {@link ObjectBuilder#add(String, Object)}.
         * @param map The {@link Map}.
         * @return {@code this}
         * @see ObjectBuilder#add(String, Object)
         * @see ObjectBuilder#addAll(Map, Function)
         */
        public ObjectBuilder addAll(Map<?, Object> map) {
            return addAll(map, Object::toString);
        }

        /**
         * Adds all elements contained in the {@link Map} as specified by {@link ObjectBuilder#add(String, Object)}.
         * Converts the keys into {@link String Strings} using {@code keyToString}.
         * @param map The {@link Map}.
         * @param keyToString The converter.
         * @return {@code this}
         * @param <K> The {@link Class} of the keys.
         * @see ObjectBuilder#add(String, Object)
         * @see ObjectBuilder#addAll(Map)
         */
        public <K> ObjectBuilder addAll(Map<K, Object> map, Function<K, String> keyToString) {
            map.forEach((key, value) -> add(keyToString.apply(key), value));
            return this;
        }

        /**
         * Returns the created {@link JsonObject} with all properties defined previously.
         * @return the created {@link JsonObject}.
         */
        public JsonObject build() {
            return build;
        }
    }

    public static class ArrayBuilder {
        private final JsonArray build;
        private ArrayBuilder() {
            build = new JsonArray();
        }
        private ArrayBuilder(int capacity) {
            build = new JsonArray(capacity);
        }

        /**
         * <p>
         *     Adds the specified {@link Object} to the {@link JsonArray}.
         *     Follows the specifications of {@link JsonUtil#toJson(Object)}.
         * </p>
         * <p>
         *     <b>Specifications of {@link JsonUtil#toJson(Object)}:</b><br>
         *     If the {@code object} is equal to {@code null} {@link JsonNull#INSTANCE} will be returned. <br>
         *     If the {@code object} is an instance of {@link JsonSavable} the result of {@link JsonSavable#save()} will be returned. <br>
         *     If the {@code object} is an instance of {@link JsonElement} the unmodified {@code object} will be returned. <br>
         *     If the {@code object} is an instance of {@link Enum} the {@code object} parsed using {@link JEnum#toJson(Enum)} will be returned. <br>
         *     If the {@code object} is an instance of {@link Collection} the {@code object} parsed using {@link ArrayBuilder#addAll(Collection)} will be returned. <br>
         *     If the {@code object} is an Array the {@code object} parsed using {@link ArrayBuilder#addAll(Object[])} will be returned. <br>
         *     Otherwise the {@code object} parsed using {@link Gson#toJsonTree(Object)} will be returned.
         * </p>
         * @param value <code>[ <b>value</b> ]</code>
         * @return {@code this}
         * @see JsonUtil#toJson(Object)
         */
        public ArrayBuilder add(Object value) {
            build.add(toJson(value));
            return this;
        }

        /**
         * Adds all elements contained in the Array as specified by {@link ArrayBuilder#add(Object)}.
         * @param array The Array.
         * @return {@code this}
         * @see ArrayBuilder#add(Object)
         */
        public ArrayBuilder addAll(boolean[] array) {
            for (boolean b : array) {
                add(b);
            }
            return this;
        }

        /**
         * Adds all elements contained in the Array as specified by {@link ArrayBuilder#add(Object)}.
         * @param array The Array.
         * @return {@code this}
         * @see ArrayBuilder#add(Object)
         */
        public ArrayBuilder addAll(byte[] array) {
            for (byte b : array) {
                add(b);
            }
            return this;
        }

        /**
         * Adds all elements contained in the Array as specified by {@link ArrayBuilder#add(Object)}.
         * @param array The Array.
         * @return {@code this}
         * @see ArrayBuilder#add(Object)
         */
        public ArrayBuilder addAll(short[] array) {
            for (short s : array) {
                add(s);
            }
            return this;
        }

        /**
         * Adds all elements contained in the Array as specified by {@link ArrayBuilder#add(Object)}.
         * @param array The Array.
         * @return {@code this}
         * @see ArrayBuilder#add(Object)
         */
        public ArrayBuilder addAll(int[] array) {
            for (int i : array) {
                add(i);
            }
            return this;
        }

        /**
         * Adds all elements contained in the Array as specified by {@link ArrayBuilder#add(Object)}.
         * @param array The Array.
         * @return {@code this}
         * @see ArrayBuilder#add(Object)
         */
        public ArrayBuilder addAll(long[] array) {
            for (long l : array) {
                add(l);
            }
            return this;
        }

        /**
         * Adds all elements contained in the Array as specified by {@link ArrayBuilder#add(Object)}.
         * @param array The Array.
         * @return {@code this}
         * @see ArrayBuilder#add(Object)
         */
        public ArrayBuilder addAll(float[] array) {
            for (float f : array) {
                add(f);
            }
            return this;
        }

        /**
         * Adds all elements contained in the Array as specified by {@link ArrayBuilder#add(Object)}.
         * @param array The Array.
         * @return {@code this}
         * @see ArrayBuilder#add(Object)
         */
        public ArrayBuilder addAll(double[] array) {
            for (double d : array) {
                add(d);
            }
            return this;
        }

        /**
         * Adds all elements contained in the Array as specified by {@link ArrayBuilder#add(Object)}.
         * @param array The Array.
         * @return {@code this}
         * @see ArrayBuilder#add(Object)
         */
        public ArrayBuilder addAll(char[] array) {
            for (char c : array) {
                add(c);
            }
            return this;
        }

        /**
         * Adds all elements contained in the Array as specified by {@link ArrayBuilder#add(Object)}.
         * @param array The Array.
         * @return {@code this}
         * @see ArrayBuilder#add(Object)
         */
        public <T> ArrayBuilder addAll(T[] array) {
            for (T t : array) {
                add(t);
            }
            return this;
        }

        /**
         * Adds all elements contained in the {@link Collection} as specified by {@link ArrayBuilder#add(Object)}.
         * @param collection The {@link Collection}.
         * @return {@code this}
         * @see ArrayBuilder#add(Object)
         */
        public ArrayBuilder addAll(Collection<?> collection) {
            collection.forEach(this::add);
            return this;
        }

        /**
         * Returns the created {@link JsonArray} with all values defined previously.
         * @return the created {@link JsonArray}.
         */
        public JsonArray build() {
            return build;
        }
    }

    /**
     * Loader to easily load {@link JsonObject}'s.
     */
    public static class ObjectLoader {
        private final JsonObject json;
        private ObjectLoader(JsonObject json) {
            this.json = json;
        }

        /**
         * Gets the property of the json and converts it to the target class if possible
         * otherwise throws a {@link JsonParseException} or {@link JsonSyntaxException}.
         * @param name <code>{ <b>name</b>: value }</code>
         * @param cl <code>{ name: <b>value</b> }</code> The {@link Class} of the {@code value}.
         * @return The result
         * @see JsonUtil#fromJson(JsonElement, Class)
         */
        public <R> R get(String name, Class<R> cl) {
            return JsonUtil.fromJson(json.get(name), cl);
        }

        /**
         * Gets the property of the json and converts it to the target class if possible
         * otherwise throws a {@link JsonParseException} or {@link JsonSyntaxException}.
         * @param name <code>{ <b>name</b>: value }</code>
         * @param converter <code>{ name: <b>value</b> }</code> The converter for the {@code value}.
         * @return The result
         */
        public <R> R get(String name, Function<JsonElement, R> converter) {
            return converter.apply(json.get(name));
        }

        /**
         * Gets the property of the json and loads it into the {@link JsonLoadable}.
         * @param name <code>{ <b>name</b>: value }</code>
         * @param jsonLoadable <code>{ name: <b>value</b> }</code> The {@link JsonLoadable} for the {@code value}.
         * @return {@code this}
         * @see JsonLoadable#load(JsonElement)
         */
        public <R extends JsonLoadable> ObjectLoader load(String name, R jsonLoadable) {
            jsonLoadable.load(json.get(name));
            return this;
        }

        /**
         * Converts the {@link JsonObject} into a {@link Map}.
         * @param valueCl The {@link Class} of the values of the {@link Map}.
         * @return The {@link Map}{@code <}{@link String}{@code , }{@link V}{@code >}.
         * @param <V> The {@link Class} of the values of the {@link Map} as a generic.
         * @see JsonUtil#fromJson(JsonElement, Class)
         * @see ObjectLoader#toMap(Function, Class)
         * @see ObjectLoader#toMap(Function)
         * @see ObjectLoader#toMap(Function, Function)
         */
        public <V> Map<String, V> toMap(Class<V> valueCl) {
            return toMap(json -> JsonUtil.fromJson(json, valueCl));
        }

        /**
         * Converts the {@link JsonObject} into a {@link Map}.
         * @param keyConverter The converter for the keys of the {@link Map}.
         * @param valueCl The {@link Class} of the values of the {@link Map}.
         * @return The {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}.
         * @param <K> The {@link Class} of the keys of the {@link Map} as a generic.
         * @param <V> The {@link Class} of the values of the {@link Map} as a generic.
         * @see JsonUtil#fromJson(JsonElement, Class)
         * @see ObjectLoader#toMap(Class)
         * @see ObjectLoader#toMap(Function)
         * @see ObjectLoader#toMap(Function, Function)
         */
        public <K, V> Map<K, V> toMap(Function<String, K> keyConverter, Class<V> valueCl) {
            return toMap(keyConverter, json -> JsonUtil.fromJson(json, valueCl));
        }

        /**
         * Converts the {@link JsonObject} into a {@link Map}.
         * @param valueConverter The converter for the values of the {@link Map}.
         * @return The {@link Map}{@code <}{@link String}{@code , }{@link V}{@code >}.
         * @param <V> The {@link Class} of the values of the {@link Map} as a generic.
         * @see ObjectLoader#toMap(Class)
         * @see ObjectLoader#toMap(Function, Class)
         * @see ObjectLoader#toMap(Function, Function)
         */
        public <V> Map<String, V> toMap(Function<JsonElement, V> valueConverter) {
            return toMap(s -> s, valueConverter);
        }

        /**
         * Converts the {@link JsonObject} into a {@link Map}.
         * @param keyConverter The converter for the keys of the {@link Map}.
         * @param valueConverter The converter for the values of the {@link Map}.
         * @return The {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}.
         * @param <K> The {@link Class} of the keys of the {@link Map} as a generic.
         * @param <V> The {@link Class} of the values of the {@link Map} as a generic.
         * @see ObjectLoader#toMap(Class)
         * @see ObjectLoader#toMap(Function, Class)
         * @see ObjectLoader#toMap(Function)
         */
        public <K, V> Map<K, V> toMap(Function<String, K> keyConverter, Function<JsonElement, V> valueConverter) {
            Map<K, V> result = new HashMap<>(json.size());
            json.asMap().forEach((key, value) -> result.put(keyConverter.apply(key), valueConverter.apply(value)));
            return result;
        }

        /**
         * Gets the property of the json and converts it into a {@link JsonObject} which is feed to a new {@link ObjectLoader}
         * otherwise throws a {@link JsonParseException} or {@link JsonSyntaxException}.
         * @param name <code>{ <b>name</b>: {...} }</code>
         * @return The result
         * @see JsonUtil#require(JsonElement, Class)
         * @see ObjectLoader#ObjectLoader(JsonObject) new JsonUtil.ObjectLoader(JsonObject)
         */
        public ObjectLoader object(String name) {
            return new ObjectLoader(JsonUtil.require(json.get(name), JsonObject.class));
        }

        /**
         * Gets the property of the json and converts it into a {@link JsonArray} which is feed to a new {@link ArrayLoader}
         * otherwise throws a {@link JsonParseException} or {@link JsonSyntaxException}.
         * @param name <code>{ <b>name</b>: [...] }</code>
         * @return The result
         * @see JsonUtil#require(JsonElement, Class)
         * @see ArrayLoader#ArrayLoader(JsonArray) new JsonUtil.ArrayLoader(JsonArray)
         */
        public ArrayLoader array(String name) {
            return new ArrayLoader(JsonUtil.require(json.get(name), JsonArray.class));
        }
    }

    public static class ArrayLoader {
        private final JsonArray json;
        private ArrayLoader(JsonArray json) {
            this.json = json;
        }

        /**
         * Gets the property of the json and converts it to the target class if possible
         * otherwise throws a {@link JsonParseException} or {@link JsonSyntaxException}.
         * @param index The index of the target element.
         * @param cl  The {@link Class} of the target element.
         * @return The result
         * @see JsonUtil#fromJson(JsonElement, Class)
         */
        public <R> R get(int index, Class<R> cl) {
            return JsonUtil.fromJson(json.get(index), cl);
        }

        /**
         * Gets the property of the json and converts it to the target class if possible
         * otherwise throws a {@link JsonParseException} or {@link JsonSyntaxException}.
         * @param index The index of the target element.
         * @param converter  The converter for the target element.
         * @return The result
         */
        public <R> R get(int index, Function<JsonElement, R> converter) {
            return converter.apply(json.get(index));
        }

        /**
         * Gets the property of the json and converts it to the target class if possible
         * otherwise throws a {@link JsonParseException} or {@link JsonSyntaxException}.
         * @param index The index of the target element.
         * @param jsonLoadable The {@link JsonLoadable} for the target element.
         * @return The result
         * @see JsonLoadable#load(JsonElement)
         */
        public <R extends JsonLoadable> R get(int index, R jsonLoadable) {
            jsonLoadable.load(json.get(index));
            return jsonLoadable;
        }

        /**
         * Converts the {@link JsonArray} into an Array.
         * @param cl The {@link Class} of the Array.
         * @return The Array
         * @param <R> The {@link Class} of the Array as a generic.
         * @see JsonUtil#fromJson(JsonElement, Class)
         */
        public <R> R[] toArray(Class<R> cl) {
            return toArray(json -> JsonUtil.fromJson(json, cl));
        }

        /**
         * Converts the {@link JsonArray} into an {@link List}.
         * @param cl The {@link Class} of the {@link List}.
         * @return The {@link List}
         * @param <R> The {@link Class} of the elements of the {@link List} as a generic.
         * @see JsonUtil#fromJson(JsonElement, Class)
         */
        public <R> List<R> toList(Class<R> cl) {
            return toList(json -> JsonUtil.fromJson(json, cl));
        }

        /**
         * Converts the {@link JsonArray} into an Array using the specified {@code converter}.
         * @param converter The converter.
         * @return The Array
         * @param <R> The {@link Class} of the Array as a generic.
         */
        @SuppressWarnings("unchecked")
        public <R> R[] toArray(Function<JsonElement, R> converter) {
            R[] array = (R[]) new Object[json.size()];
            for (int index = 0; index < array.length; index++) {
                array[index] = converter.apply(json.get(index));
            }
            return array;
        }

        /**
         * Converts the {@link JsonArray} into a {@link List} using the specified {@code converter}.
         * @param converter The converter.
         * @return The {@link List}
         * @param <R> The {@link Class} of the elements of the {@link List} as a generic.
         */
        public <R> List<R> toList(Function<JsonElement, R> converter) {
            List<R> list = new ArrayList<>(json.size());
            json.forEach(element -> list.add(converter.apply(element)));
            return list;
        }

        /**
         * Gets the property of the json and converts it into a {@link JsonObject} which is feed to a new {@link ObjectLoader}
         * otherwise throws a {@link JsonParseException} or {@link JsonSyntaxException}.
         * @param index The index of the target element.
         * @return The result
         * @see JsonUtil#require(JsonElement, Class)
         * @see ObjectLoader#ObjectLoader(JsonObject) new JsonUtil.ObjectLoader(JsonObject)
         */
        public ObjectLoader object(int index) {
            return new ObjectLoader(JsonUtil.require(json.get(index), JsonObject.class));
        }

        /**
         * Gets the property of the json and converts it into a {@link JsonArray} which is feed to a new {@link ArrayLoader}
         * otherwise throws a {@link JsonParseException} or {@link JsonSyntaxException}.
         * @param index The index of the target element.
         * @return The result
         * @see JsonUtil#require(JsonElement, Class)
         * @see ArrayLoader#ArrayLoader(JsonArray) new JsonUtil.ArrayLoader(JsonArray)
         */
        public ArrayLoader array(int index) {
            return new ArrayLoader(JsonUtil.require(json.get(index), JsonArray.class));
        }
    }

    /**
     * A Builder for {@link JsonObject}'s.
     * @return an instance of {@link ObjectBuilder}.
     */
    public static ObjectBuilder object() {
        return new ObjectBuilder();
    }

    /**
     * A Builder for {@link JsonArray}'s.
     * @return an instance of {@link ArrayBuilder}.
     */
    public static ArrayBuilder array() {
        return new ArrayBuilder();
    }

    /**
     * A Builder for {@link JsonArray}'s with a predefined capacity. <br>
     * <b>The capacity is not validated anywhere.</b>
     * It is the users responsibility to assure that the correct amount of values is added.
     * @return an instance of {@link ArrayBuilder}.
     */
    public static ArrayBuilder array(int capacity) {
        return new ArrayBuilder(capacity);
    }

    /**
     * A Builder for {@link JsonObject}'s.
     * @return an instance of {@link ObjectBuilder}.
     */
    public static ObjectLoader object(JsonElement json) {
        return new ObjectLoader(JsonUtil.require(json, JsonObject.class));
    }

    /**
     * A Builder for {@link JsonArray}'s.
     * @return an instance of {@link ArrayBuilder}.
     */
    public static ArrayLoader array(JsonElement json) {
        return new ArrayLoader(JsonUtil.require(json, JsonArray.class));
    }

    /**
     * Converts a {@code boolean} into an {@link JsonPrimitive}.
     * @param b the {@code boolean}
     * @return the {@link JsonPrimitive}
     */
    public static JsonPrimitive toJson(boolean b) {
        return new JsonPrimitive(b);
    }

    /**
     * Converts a {@code byte} into an {@link JsonPrimitive}.
     * @param b the {@code byte}
     * @return the {@link JsonPrimitive}
     */
    public static JsonPrimitive toJson(byte b) {
        return new JsonPrimitive(b);
    }

    /**
     * Converts a {@code short} into an {@link JsonPrimitive}.
     * @param s the {@code short}
     * @return the {@link JsonPrimitive}
     */
    public static JsonPrimitive toJson(short s) {
        return new JsonPrimitive(s);
    }

    /**
     * Converts a {@code int} into an {@link JsonPrimitive}.
     * @param i the {@code int}
     * @return the {@link JsonPrimitive}
     */
    public static JsonPrimitive toJson(int i) {
        return new JsonPrimitive(i);
    }

    /**
     * Converts a {@code long} into an {@link JsonPrimitive}.
     * @param l the {@code long}
     * @return the {@link JsonPrimitive}
     */
    public static JsonPrimitive toJson(long l) {
        return new JsonPrimitive(l);
    }

    /**
     * Converts a {@code float} into an {@link JsonPrimitive}.
     * @param f the {@code float}
     * @return the {@link JsonPrimitive}
     */
    public static JsonPrimitive toJson(float f) {
        return new JsonPrimitive(f);
    }

    /**
     * Converts a {@code double} into an {@link JsonPrimitive}.
     * @param d the {@code double}
     * @return the {@link JsonPrimitive}
     */
    public static JsonPrimitive toJson(double d) {
        return new JsonPrimitive(d);
    }

    /**
     * Converts a {@code char} into an {@link JsonPrimitive}.
     * @param c the {@code char}
     * @return the {@link JsonPrimitive}
     */
    public static JsonPrimitive toJson(char c) {
        return new JsonPrimitive(c);
    }

    /**
     * <p>
     *     Converts a {@link Object} into an {@link JsonPrimitive}.
     * </p>
     * <p>
     *     If the {@code object} is equal to {@code null} {@link JsonNull#INSTANCE} will be returned. <br>
     *     If the {@code object} is an instance of {@link JsonSavable} the result of {@link JsonSavable#save()} will be returned. <br>
     *     If the {@code object} is an instance of {@link JsonElement} the unmodified {@code object} will be returned. <br>
     *     If the {@code object} is an instance of {@link Enum} the {@code object} parsed using {@link JEnum#toJson(Enum)} will be returned. <br>
     *     If the {@code object} is an instance of {@link Collection} the {@code object} parsed using {@link ArrayBuilder#addAll(Collection)} will be returned. <br>
     *     If the {@code object} is an Array the {@code object} parsed using {@link ArrayBuilder#addAll(Object[])} will be returned. <br>
     *     Otherwise the {@code object} parsed using {@link Gson#toJsonTree(Object)} will be returned.
     * </p>
     * @param o the {@link Object}
     * @return the {@link JsonPrimitive}
     */
    public static JsonElement toJson(Object o) {
        if (o == null) return JsonNull.INSTANCE;
        if (o instanceof JsonSavable js) return js.save();
        if (o instanceof JsonElement je) return je;
        if (o instanceof Enum<?> e) return JEnum.toJson(e);
        if (o instanceof Collection<?> c) return array(c.size()).addAll(c).build();
        if (o.getClass().isArray()) {
            Object[] array = (Object[]) o;
            return array(array.length).addAll(array).build();
        }
        return GSON.toJsonTree(o);
    }

    /**
     * Converts the {@code json} to the target class
     * otherwise throws a {@link JsonParseException} or {@link JsonSyntaxException}.
     * @param json The {@link JsonElement}.
     * @param cl The target {@link Class}.
     * @return The result
     * @see JsonUtil#requireNonNull(JsonElement)
     * @see JsonUtil#require(JsonElement, Class)
     * @see JsonUtil#requireBoolean(JsonElement)
     * @see JsonUtil#requireNumber(JsonElement)
     * @see JsonUtil#requireString(JsonElement)
     * @see JsonUtil#requireEnum(JsonElement, Class)
     * @see Gson#fromJson(JsonElement, Class)
     */
    @SuppressWarnings({"unchecked", "rawtypes"}) // Every case is checked by an if statement a line above.
    public static <R> R fromJson(JsonElement json, Class<R> cl) {
        JsonUtil.requireNonNull(json);
        if (cl.isArray()) {
            return (R) array(json).toArray(cl.arrayType());
        } else if (JsonElement.class.isAssignableFrom(cl)) {
            return JsonUtil.require(json, cl);
        } else if (cl.isAssignableFrom(Boolean.class)) {
            return (R) JsonUtil.requireBoolean(json);
        } else if (cl.isAssignableFrom(Number.class)) {
            return (R) JsonUtil.requireNumber(json);
        } else if (cl.isAssignableFrom(Byte.class)) {
            return (R) (Byte) JsonUtil.requireNumber(json).byteValue();
        } else if (cl.isAssignableFrom(Short.class)) {
            return (R) (Short) JsonUtil.requireNumber(json).shortValue();
        } else if (cl.isAssignableFrom(Integer.class)) {
            return (R) (Integer) JsonUtil.requireNumber(json).intValue();
        } else if (cl.isAssignableFrom(Long.class)) {
            return (R) (Long) JsonUtil.requireNumber(json).longValue();
        } else if (cl.isAssignableFrom(Float.class)) {
            return (R) (Float) JsonUtil.requireNumber(json).floatValue();
        } else if (cl.isAssignableFrom(Double.class)) {
            return (R) (Double) JsonUtil.requireNumber(json).doubleValue();
        } else if (cl.isAssignableFrom(String.class)) {
            return (R) JsonUtil.requireString(json);
        } else if (cl.isAssignableFrom(Enum.class)) {
            return (R) JsonUtil.requireEnum(json, (Class<? extends Enum>) cl);
        } else {
            return GSON.fromJson(json, cl);
        }
    }

    /**
     * Utility class for converting {@code Arrays} to {@link JsonArray JsonArrays}.
     */
    public static class JArray {
        /**
         * Converts an {@code Array} of {@link Object Objects} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@code toJson}.
         * @param toJson Parser for the elements of the {@code array}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @param <T> class of the elements of the {@code array}.
         * @see JArray#toJson(Object[])
         */
        @SafeVarargs
        @Contract(pure = true)
        public static <T> JsonArray toJson(Function<T, JsonElement> toJson, T... array) {
            JsonArray json = new JsonArray(array.length);
            for (T t : array) {
                json.add(toJson.apply(t));
            }
            return json;
        }

        /**
         * Converts an {@code Array} of {@link Object Objects} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@link JsonUtil#toJson(Object)}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @param <T> class of the elements of the {@code array}.
         * @see JArray#toJson(Function, Object[])
         */
        @SafeVarargs
        @Contract(pure = true)
        public static <T> JsonArray toJson(T... array) {
            return toJson(JsonUtil::toJson, array);
        }

        /**
         * Converts an {@code Array} of {@code booleans} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@code toJson}.
         * @param toJson Parser for the elements of the {@code array}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(boolean[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(Function<Boolean, JsonElement> toJson, boolean... array) {
            JsonArray json = new JsonArray(array.length);
            for (boolean b : array) {
                json.add(toJson.apply(b));
            }
            return json;
        }

        /**
         * Converts an {@code Array} of {@code booleans} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@link JsonPrimitive#JsonPrimitive(Boolean) new JsonPrimitive(Boolean)}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(Function, boolean[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(boolean... array) {
            return toJson(JsonPrimitive::new, array);
        }

        /**
         * Converts an {@code Array} of {@code bytes} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@code toJson}.
         * @param toJson Parser for the elements of the {@code array}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(byte[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(Function<Byte, JsonElement> toJson, byte... array) {
            JsonArray json = new JsonArray(array.length);
            for (byte b : array) {
                json.add(toJson.apply(b));
            }
            return json;
        }

        /**
         * Converts an {@code Array} of {@code bytes} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@link JsonPrimitive#JsonPrimitive(Number) new JsonPrimitive(Number)}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(Function, byte[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(byte... array) {
            return toJson(JsonPrimitive::new, array);
        }

        /**
         * Converts an {@code Array} of {@code shorts} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@code toJson}.
         * @param toJson Parser for the elements of the {@code array}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(short[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(Function<Short, JsonElement> toJson, short... array) {
            JsonArray json = new JsonArray(array.length);
            for (short s : array) {
                json.add(toJson.apply(s));
            }
            return json;
        }

        /**
         * Converts an {@code Array} of {@code shorts} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@link JsonPrimitive#JsonPrimitive(Number) new JsonPrimitive(Number)}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(Function, short[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(short... array) {
            return toJson(JsonPrimitive::new, array);
        }

        /**
         * Converts an {@code Array} of {@code ints} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@code toJson}.
         * @param toJson Parser for the elements of the {@code array}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(int[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(Function<Integer, JsonElement> toJson, int... array) {
            JsonArray json = new JsonArray(array.length);
            for (int i : array) {
                json.add(toJson.apply(i));
            }
            return json;
        }

        /**
         * Converts an {@code Array} of {@code ints} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@link JsonPrimitive#JsonPrimitive(Number) new JsonPrimitive(Number)}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(Function, int[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(int... array) {
            return toJson(JsonPrimitive::new, array);
        }

        /**
         * Converts an {@code Array} of {@code longs} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@code toJson}.
         * @param toJson Parser for the elements of the {@code array}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(long[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(Function<Long, JsonElement> toJson, long... array) {
            JsonArray json = new JsonArray(array.length);
            for (long l : array) {
                json.add(toJson.apply(l));
            }
            return json;
        }

        /**
         * Converts an {@code Array} of {@code longs} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@link JsonPrimitive#JsonPrimitive(Number) new JsonPrimitive(Number)}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(Function, long[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(long... array) {
            return toJson(JsonPrimitive::new, array);
        }

        /**
         * Converts an {@code Array} of {@code floats} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@code toJson}.
         * @param toJson Parser for the elements of the {@code array}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(float[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(Function<Float, JsonElement> toJson, float... array) {
            JsonArray json = new JsonArray(array.length);
            for (float f : array) {
                json.add(toJson.apply(f));
            }
            return json;
        }

        /**
         * Converts an {@code Array} of {@code floats} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@link JsonPrimitive#JsonPrimitive(Number) new JsonPrimitive(Number)}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(Function, float[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(float... array) {
            return toJson(JsonPrimitive::new, array);
        }

        /**
         * Converts an {@code Array} of {@code doubles} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@code toJson}.
         * @param toJson Parser for the elements of the {@code array}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(double[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(Function<Double, JsonElement> toJson, double... array) {
            JsonArray json = new JsonArray(array.length);
            for (double d : array) {
                json.add(toJson.apply(d));
            }
            return json;
        }

        /**
         * Converts an {@code Array} of {@code double} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@link JsonPrimitive#JsonPrimitive(Number) new JsonPrimitive(Number)}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(Function, double[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(double... array) {
            return toJson(JsonPrimitive::new, array);
        }

        /**
         * Converts an {@code Array} of {@code chars} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@code toJson}.
         * @param toJson Parser for the elements of the {@code array}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(char[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(Function<Character, JsonElement> toJson, char... array) {
            JsonArray json = new JsonArray(array.length);
            for (char c : array) {
                json.add(toJson.apply(c));
            }
            return json;
        }

        /**
         * Converts an {@code Array} of {@code char} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code array}
         * parsed using {@link JsonPrimitive#JsonPrimitive(Character) new JsonPrimitive(Character)}.
         * @param array The {@code array} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JArray#toJson(Function, char[])
         */
        @Contract(pure = true)
        public static JsonArray toJson(char... array) {
            return toJson(JsonPrimitive::new, array);
        }
    }

    /**
     * Utility class for converting {@link Collection Collections} to {@link JsonArray JsonArrays}.
     */
    public static class JCollection {
        /**
         * Converts a {@link Collection} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code collection}
         * parsed using {@code toJson}.
         * @param toJson Parser for the elements of the {@code collection}.
         * @param collection The {@code collection} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JCollection#toJson(Collection)
         */
        @Contract(pure = true)
        public static <T> JsonArray toJson(Function<T, JsonElement> toJson, Collection<T> collection) {
            JsonArray json = new JsonArray(collection.size());
            collection.forEach(t -> json.add(toJson.apply(t)));
            return json;
        }

        /**
         * Converts a {@link Collection} into a {@link JsonArray}.
         * The resulting {@link JsonArray} will contain all elements of the original {@code collection}
         * parsed using {@link JsonUtil#toJson(Object)}.
         * @param collection The {@code collection} which should be converted to a {@link JsonArray}.
         * @return The converted {@link JsonArray}.
         * @see JCollection#toJson(Function, Collection)
         */
        @Contract(pure = true)
        public static <T> JsonArray toJson(Collection<T> collection) {
            return toJson(JsonUtil::toJson, collection);
        }
    }

    /**
     * Utility class for converting {@link Map Maps} to {@link JsonObject JsonObjects}.
     */
    public static class JMap {
        /**
         * Converts a {@link Map} into a {@link JsonObject}.
         * The resulting {@link JsonObject} will contain all entries of the original {@code map}
         * parsed using {@code toString} for the keys and {@code toJson} for the values.
         * @param toString Parser for the <b>keys</b> of the {@code map}.
         * @param toJson Parser for the <b>values</b> of the {@code map}.
         * @param map The {@code map} which should be converted to a {@link JsonObject}.
         * @return The converted {@link JsonObject}.
         * @see JMap#toJson(Function, Map)
         * @see JMap#toJson(Map)
         */
        @Contract(pure = true)
        public static <K, V> JsonObject toJson(Function<K, String> toString, Function<V, JsonElement> toJson, Map<K, V> map) {
            JsonObject json = new JsonObject();
            map.forEach((k, v) -> json.add(toString.apply(k), toJson.apply(v)));
            return json;
        }

        /**
         * Converts a {@link Map} into a {@link JsonObject}.
         * The resulting {@link JsonObject} will contain all entries of the original {@code map}
         * parsed using {@link Object#toString()} for the keys and {@code toJson} for the values.
         * @param toJson Parser for the <b>values</b> of the {@code map}.
         * @param map The {@code map} which should be converted to a {@link JsonObject}.
         * @return The converted {@link JsonObject}.
         * @see JMap#toJson(Function, Function, Map)
         * @see JMap#toJson(Map)
         */
        @Contract(pure = true)
        public static <V> JsonObject toJson(Function<V, JsonElement> toJson, Map<?, V> map) {
            return toJson(Object::toString, toJson, map);
        }

        /**
         * Converts a {@link Map} into a {@link JsonObject}.
         * The resulting {@link JsonObject} will contain all entries of the original {@code map}
         * parsed using {@link Object#toString()} for the keys and {@link JsonUtil#toJson(Object)} for the values.
         * @param map The {@code map} which should be converted to a {@link JsonObject}.
         * @return The converted {@link JsonObject}.
         * @see JMap#toJson(Function, Function, Map)
         * @see JMap#toJson(Function, Map)
         */
        @Contract(pure = true)
        public static JsonObject toJson(Map<?, ?> map) {
            return toJson(JsonUtil::toJson, map);
        }

        /**
         * Converts a {@link JsonElement} into a {@link Map}{@code <}{@link String}{@code , }{@link JsonElement}{@code >}.
         * The resulting {@link Map}{@code <}{@link String}{@code , }{@link JsonElement}{@code >} will contain all entries of the original {@code json}.
         * The {@link String} will be the property name and the {@link JsonElement} the property value for each entry.
         * <br>
         * If the {@link JsonElement} is an instance of {@link JsonNull} this function will return an empty {@link HashMap}{@code <}{@link String}{@code , }{@link JsonElement}{@code >}.
         * @param json The {@link JsonElement}
         * @return The {@link Map}{@code <}{@link String}{@code , }{@link JsonElement}{@code >}.
         * @throws JsonParseException If the {@link JsonElement} isn't an instance of {@link JsonObject} or {@link JsonNull}.
         */
        @Contract(value = "null -> fail", pure = true)
        public static Map<String, JsonElement> toStringMap(JsonElement json) {
            handle(() -> ObjectUtil.requireOneOf(json, JsonObject.class, JsonNull.class), json);
            if (json instanceof JsonNull)
                return new HashMap<>(0);
            if (json instanceof JsonObject jsonObject) {
                Map<String, JsonElement> stringMap = new HashMap<>(jsonObject.size());
                jsonObject.keySet().forEach(key -> stringMap.put(key, jsonObject.get(key)));
                return stringMap;
            }
            throw new Error("ObjectUtil#requireOneOf is broken");
        }
    }

    /**
     * Utility class for converting {@link Enum Enums} to {@link JsonPrimitive JsonPrimitives} with a {@link String} value.
     */
    public static class JEnum {
        /**
         * Converts an {@link Enum} into a {@link JsonPrimitive JsonPrimitives} with a {@link String} value.
         * Throws an Exception if {@code o} isn't an {@link Enum}
         * @param o The {@link Enum} which should be converted.
         * @return A {@link JsonPrimitive JsonPrimitives} with a {@link String} value.
         * @throws NullPointerException If {@code o} equals {@code null}.
         * @throws ClassCastException If {@code o} isn't an instance of {@link Enum}.
         */
        @Contract(pure = true)
        public static JsonPrimitive toJson(Object o) {
            return toJson(ObjectUtil.require(o, Enum.class));
        }

        /**
         * Converts an {@link Enum} into a {@link JsonPrimitive JsonPrimitives} with a {@link String} value.
         * @param e The {@link Enum} which should be converted.
         * @return A {@link JsonPrimitive JsonPrimitives} with a {@link String} value.
         */
        @Contract(pure = true)
        public static JsonPrimitive toJson(Enum<?> e) {
            return new JsonPrimitive(e.name());
        }
    }

    /**
     * Checks if the {@link JsonElement} is an instance of {@link JsonArray} and exactly matches the required {@code size}.
     * @param json The {@link JsonElement}.
     * @param size The supposed {@code size} of the {@link JsonArray}.
     * @return The {@link JsonArray}.
     * @throws JsonParseException If the element isn't an instance of {@link JsonArray} or doesn't match the required {@code size}.
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static JsonArray requireArray(JsonElement json, int size) {
        if (json == null)
            throw new JsonParseException("Expected JsonArray but found null");
        if (json instanceof JsonArray jsonArray) {
            if (jsonArray.size() != size)
                throw new JsonParseException("Expected JsonArray with size " + size + " not with size " + jsonArray.size());
            return jsonArray;
        } else {
            throw new JsonParseException("Expected JsonArray but found " + json.getClass().getSimpleName());
        }
    }

    /**
     * Checks if the {@link JsonElement} is a {@link JsonPrimitive} with a {@link String} value.
     * @param json The {@link JsonElement}.
     * @return The {@link String} value of the {@link JsonPrimitive}.
     * @throws JsonParseException If the element isn't an instance of {@link JsonPrimitive} or doesn't have a {@link String} value.
     */
    @Contract(value = "null -> fail", pure = true)
    public static String requireString(JsonElement json) {
        if (json == null)
            throw new JsonParseException("Expected JsonPrimitive but found null");
        if (json instanceof JsonPrimitive jsonPrimitive) {
            if (!jsonPrimitive.isString())
                throw new JsonParseException("Expected String but found " + json);
            return json.getAsString();
        } else {
            throw new JsonParseException("Expected JsonPrimitive but found " + json.getClass().getSimpleName() + " at " + json);
        }
    }

    /**
     * Checks if the {@link JsonElement} is a {@link JsonPrimitive} with a {@link Number} value.
     * @param json The {@link JsonElement}.
     * @return The {@link Number} value of the {@link JsonPrimitive}.
     * @throws JsonParseException If the element isn't an instance of {@link JsonPrimitive} or doesn't have a {@link Number} value.
     */
    @Contract(value = "null -> fail", pure = true)
    public static Number requireNumber(JsonElement json) {
        if (json == null)
            throw new JsonParseException("Expected JsonPrimitive but found null");
        if (json instanceof JsonPrimitive jsonPrimitive) {
            if (!jsonPrimitive.isNumber())
                throw new JsonParseException("Expected Number but found " + json);
            return json.getAsNumber();
        } else {
            throw new JsonParseException("Expected JsonPrimitive but found " + json.getClass().getSimpleName() + " at " + json);
        }
    }

    /**
     * Checks if the {@link JsonElement} is a {@link JsonPrimitive} with a {@link Boolean} value.
     * @param json The {@link JsonElement}.
     * @return The {@link Boolean} value of the {@link JsonPrimitive}.
     * @throws JsonParseException If the element isn't an instance of {@link JsonPrimitive} or doesn't have a {@link Boolean} value.
     */
    @Contract(value = "null -> fail", pure = true)
    public static Boolean requireBoolean(JsonElement json) {
        if (json == null)
            throw new JsonParseException("Expected JsonPrimitive but found null");
        if (json instanceof JsonPrimitive jsonPrimitive) {
            if (!jsonPrimitive.isBoolean())
                throw new JsonParseException("Expected Boolean but found " + json);
            return json.getAsBoolean();
        } else {
            throw new JsonParseException("Expected JsonPrimitive but found " + json.getClass().getSimpleName() + " at " + json);
        }
    }

    /**
     * Checks if the {@link JsonElement} is a {@link JsonPrimitive} with a {@link String} value
     * which is a name of an {@link Enum} in the specified {@link Class} {@code cl}.
     * @param json The {@link JsonElement}.
     * @param cl The class of the {@link Enum}.
     * @return The {@link String} value of the {@link JsonPrimitive}.
     * @param <T> The class of the {@link Enum} as a generic.
     * @throws JsonParseException If the element isn't an instance of {@link JsonPrimitive},
     * doesn't have a {@link String} value or the {@link String} value isn't a valid name for the {@link Enum}.
     */
    @Contract(value = "null, _ -> fail; _, null -> fail", pure = true)
    public static <T extends Enum<T>> T requireEnum(JsonElement json, Class<T> cl) {
        if (json == null)
            throw new JsonParseException("Expected JsonPrimitive but found null");
        if (json instanceof JsonPrimitive jsonPrimitive) {
            if (!jsonPrimitive.isString())
                throw new JsonParseException("Expected String but found " + json);
            String s = json.getAsString();
            try {
                return Enum.valueOf(cl, s);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Expected String to be one of " + Arrays.toString(cl.getEnumConstants()) + " but found " + s);
            }
        } else {
            throw new JsonParseException("Expected JsonPrimitive but found " + json.getClass().getSimpleName() + " at " + json);
        }
    }

    /**
     * Checks if the {@link JsonElement} is an instance of the {@link Class} {@code cl}.
     * @param json The {@link JsonElement}.
     * @param cl The target class of the {@link JsonElement}.
     * @return The {@link JsonElement} cast to the target {@link Class}.
     * @param <T> The target class of the {@link JsonElement} as a generic.
     * @throws JsonParseException If the {@link JsonElement} isn't an instance of {@link Class} {@code cl}.
     * @see ObjectUtil#require(Object, Class)
     * @see JsonUtil#handle(Supplier, JsonElement)
     */
    @Contract(pure = true)
    public static <T> T require(JsonElement json, Class<T> cl) {
        return handle(() -> ObjectUtil.require(json, cl), json);
    }

    /**
     * Checks if the {@link JsonElement} is not {@code null} (Note that this doesn't check for {@link JsonNull}).
     * @param json The {@link JsonElement} to be checked.
     * @return The {@link JsonElement}
     * @param <T> The class of the {@link JsonElement}
     * @throws JsonParseException If the {@link JsonElement} is {@code null}.
     * @see Objects#requireNonNull(Object)
     * @see JsonUtil#handle(Supplier, JsonElement)
     */
    @Contract(pure = true)
    public static <T extends JsonElement> T requireNonNull(T json) {
        return handle(() -> Objects.requireNonNull(json), json);
    }

    /**
     * Catches all {@link Exception Exceptions} (including {@link RuntimeException RuntimeExceptions})
     * thrown by the {@link Action} {@code action} and rethrows them wrapped in an {@link JsonParseException}
     * with the {@code cause} and {@code line} as additional info.
     * @param action The {@link Action} which should run.
     * @param cause The {@link JsonElement} which would be the reason for any {@link Exception Exceptions} thrown.
     * @param line The {@code line} in which the {@link JsonElement} {@code cause} was found.
     * @throws JsonParseException If any other {@link Exception} had been thrown.
     */
    @Contract(pure = true)
    public static void handle(Action action, JsonElement cause, int line) {
        Objects.requireNonNull(action);
        try {
            action.run();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing " + cause + " at line " + line, e);
        }
    }

    /**
     * Catches all {@link Exception Exceptions} (including {@link RuntimeException RuntimeExceptions})
     * thrown by the {@link Action} {@code action} and rethrows them wrapped in an {@link JsonParseException}
     * with the {@code cause} as additional info.
     * @param action The {@link Action} which should run.
     * @param cause The {@link JsonElement} which would be the reason for any {@link Exception Exceptions} thrown.
     * @throws JsonParseException If any other {@link Exception} had been thrown.
     */
    @Contract(pure = true)
    public static void handle(Action action, JsonElement cause) {
        Objects.requireNonNull(action);
        try {
            action.run();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing " + cause, e);
        }
    }

    /**
     * Catches all {@link Exception Exceptions} (including {@link RuntimeException RuntimeExceptions})
     * thrown by the {@link Action} {@code action} and rethrows them wrapped in an {@link JsonParseException}
     * with the {@code line} as additional info.
     * @param action The {@link Action} which should run.
     * @param line The {@code line} which would be the reason for any {@link Exception Exceptions} thrown.
     * @throws JsonParseException If any other {@link Exception} had been thrown.
     */
    @Contract(pure = true)
    public static void handle(Action action, int line) {
        Objects.requireNonNull(action);
        try {
            action.run();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing line " + line, e);
        }
    }

    /**
     * Catches all {@link Exception Exceptions} (including {@link RuntimeException RuntimeExceptions})
     * thrown by the {@link Action} {@code action} and rethrows them wrapped in an {@link JsonParseException}
     * with the {@code cause} and {@code line} as additional info.
     * @param action The {@link Supplier} which should run and whose return value should be returned.
     * @param cause The {@link JsonElement} which would be the reason for any {@link Exception Exceptions} thrown.
     * @param line The {@code line} in which the {@link JsonElement} {@code cause} was found.
     * @return The return value of the {@code action}.
     * @throws JsonParseException If any other {@link Exception} had been thrown.
     */
    @Contract(pure = true)
    public static <T> T handle(Supplier<T> action, JsonElement cause, int line) {
        Objects.requireNonNull(action);
        try {
            return action.get();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing " + cause + " at line " + line, e);
        }
    }

    /**
     * Catches all {@link Exception Exceptions} (including {@link RuntimeException RuntimeExceptions})
     * thrown by the {@link Action} {@code action} and rethrows them wrapped in an {@link JsonParseException}
     * with the {@code cause} and {@code line} as additional info.
     * @param action The {@link Supplier} which should run and whose return value should be returned.
     * @param cause The {@link JsonElement} which would be the reason for any {@link Exception Exceptions} thrown.
     * @return The return value of the {@code action}.
     * @throws JsonParseException If any other {@link Exception} had been thrown.
     */
    @Contract(pure = true)
    public static <T> T handle(Supplier<T> action, JsonElement cause) {
        Objects.requireNonNull(action);
        try {
            return action.get();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing " + cause, e);
        }
    }

    /**
     * Catches all {@link Exception Exceptions} (including {@link RuntimeException RuntimeExceptions})
     * thrown by the {@link Action} {@code action} and rethrows them wrapped in an {@link JsonParseException}
     * with the {@code cause} and {@code line} as additional info.
     * @param action The {@link Supplier} which should run and whose return value should be returned.
     * @param line The {@code line} which would be the reason for any {@link Exception Exceptions} thrown.
     * @return The return value of the {@code action}.
     * @throws JsonParseException If any other {@link Exception} had been thrown.
     */
    @Contract(pure = true)
    public static <T> T handle(Supplier<T> action, int line) {
        Objects.requireNonNull(action);
        try {
            return action.get();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing line " + line, e);
        }
    }

    /**
     * Creates a {@link FileUtil.Reader} which will read the file into {@link JsonElement} {@code reader}.
     * @param reader The {@link Consumer} for the {@link JsonElement}.
     * @return The {@link FileUtil.Reader} which still needs to be used.
     */
    @Contract(pure = true)
    public static FileUtil.Reader reader(@NotNull Consumer<JsonElement> reader) {
        return fileReader -> reader.accept(JsonParser.parseReader(fileReader));
    }

    /**
     * Creates a {@link FileUtil.Writer} which will write the supplied {@link JsonElement} {@code write} to the file.
     * @param writer The {@link Supplier} for the {@link JsonElement}.
     * @return The {@link FileUtil.Writer} which still needs to be used.
     */
    @Contract(pure = true)
    public static FileUtil.Writer writer(@NotNull Supplier<JsonElement> writer) {
        return fileWriter -> {
            fileWriter.write(GSON.toJson(writer.get()));
            fileWriter.flush();
        };
    }
}
