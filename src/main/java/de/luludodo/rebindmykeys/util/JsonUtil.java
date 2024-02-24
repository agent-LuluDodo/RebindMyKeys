package de.luludodo.rebindmykeys.util;

import com.google.gson.*;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JsonUtil {
    public static class JArray {
        @SafeVarargs
        @Contract(pure = true)
        public static <T> JsonArray toJson(Function<T, JsonElement> toJson, T... array) {
            JsonArray json = new JsonArray(array.length);
            for (T t : array) {
                json.add(toJson.apply(t));
            }
            return json;
        }

        @SafeVarargs
        @Contract(pure = true)
        public static <J extends JsonElement> JsonArray toJson(J... array) {
            return toJson(j -> j, array);
        }

        @Contract(pure = true)
        public static JsonArray toJson(String... array) {
            return toJson(JsonPrimitive::new, array);
        }

        @Contract(pure = true)
        public static JsonArray toJson(Number... array) {
            return toJson(JsonPrimitive::new, array);
        }

        @Contract(pure = true)
        public static JsonArray toJson(Character... array) {
            return toJson(JsonPrimitive::new, array);
        }

        @Contract(pure = true)
        public static JsonArray toJson(Function<Boolean, JsonElement> toJson, boolean... array) {
            JsonArray json = new JsonArray(array.length);
            for (boolean b : array) {
                json.add(toJson.apply(b));
            }
            return json;
        }
        @Contract(pure = true)
        public static JsonArray toJson(boolean... array) {
            return toJson(JsonPrimitive::new, array);
        }

        @Contract(pure = true)
        public static JsonArray toJson(Function<Byte, JsonElement> toJson, byte... array) {
            JsonArray json = new JsonArray(array.length);
            for (byte b : array) {
                json.add(toJson.apply(b));
            }
            return json;
        }
        @Contract(pure = true)
        public static JsonArray toJson(byte... array) {
            return toJson(JsonPrimitive::new, array);
        }

        @Contract(pure = true)
        public static JsonArray toJson(Function<Integer, JsonElement> toJson, int... array) {
            JsonArray json = new JsonArray(array.length);
            for (int i : array) {
                json.add(toJson.apply(i));
            }
            return json;
        }
        @Contract(pure = true)
        public static JsonArray toJson(int... array) {
            return toJson(JsonPrimitive::new, array);
        }

        @Contract(pure = true)
        public static JsonArray toJson(Function<Long, JsonElement> toJson, long... array) {
            JsonArray json = new JsonArray(array.length);
            for (long l : array) {
                json.add(toJson.apply(l));
            }
            return json;
        }
        @Contract(pure = true)
        public static JsonArray toJson(long... array) {
            return toJson(JsonPrimitive::new, array);
        }

        @Contract(pure = true)
        public static JsonArray toJson(Function<Float, JsonElement> toJson, float... array) {
            JsonArray json = new JsonArray(array.length);
            for (float f : array) {
                json.add(toJson.apply(f));
            }
            return json;
        }
        @Contract(pure = true)
        public static JsonArray toJson(float... array) {
            return toJson(JsonPrimitive::new, array);
        }

        @Contract(pure = true)
        public static JsonArray toJson(Function<Double, JsonElement> toJson, double... array) {
            JsonArray json = new JsonArray(array.length);
            for (double d : array) {
                json.add(toJson.apply(d));
            }
            return json;
        }
        @Contract(pure = true)
        public static JsonArray toJson(double... array) {
            return toJson(JsonPrimitive::new, array);
        }

        @Contract(pure = true)
        public static JsonArray toJson(Function<Character, JsonElement> toJson, char... array) {
            JsonArray json = new JsonArray(array.length);
            for (char c : array) {
                json.add(toJson.apply(c));
            }
            return json;
        }
        @Contract(pure = true)
        public static JsonArray toJson(char... array) {
            return toJson(JsonPrimitive::new, array);
        }
    }

    public static class JCollection {
        @Contract(pure = true)
        public static <T> JsonArray toJson(Function<T, JsonElement> toJson, Collection<T> collection) {
            JsonArray json = new JsonArray(collection.size());
            collection.forEach(t -> json.add(toJson.apply(t)));
            return json;
        }

        @Contract(pure = true)
        public static <J extends JsonElement> JsonArray jsonToJson(Collection<J> collection) {
            return toJson(j -> j, collection);
        }

        @Contract(pure = true)
        public static <T> JsonArray toJson(Collection<T> collection) {
            return jsonToJson(ObjectUtil.requireOneOfForEach(
                    collection,
                    ObjectUtil.classCase(JsonElement.class, j -> (JsonElement) j),
                    ObjectUtil.classCase(Number.class, n -> (JsonElement) new JsonPrimitive(n)),
                    ObjectUtil.classCase(Character.class, c -> (JsonElement) new JsonPrimitive(c)),
                    ObjectUtil.classCase(String.class, s -> (JsonElement) new JsonPrimitive(s)),
                    ObjectUtil.classCase(Enum.class, e -> (JsonElement) JsonUtil.JEnum.toJson(e))
            ));
        }

        @Contract(pure = true)
        public static JsonArray numberToJson(Collection<Number> collection) {
            return toJson(JsonPrimitive::new, collection);
        }

        @Contract(pure = true)
        public static JsonArray charToJson(Collection<Character> collection) {
            return toJson(JsonPrimitive::new, collection);
        }

        @Contract(pure = true)
        public static JsonArray stringToJson(Collection<String> collection) {
            return toJson(JsonPrimitive::new, collection);
        }

        @Contract(pure = true)
        public static JsonArray boolToJson(Collection<Boolean> collection) {
            return toJson(JsonPrimitive::new, collection);
        }
    }

    public static class JMap {
        @Contract(pure = true)
        public static <K, V> JsonObject toJson(Function<K, String> toString, Function<V, JsonElement> toJson, Map<K, V> map) {
            JsonObject json = new JsonObject();
            map.forEach((k, v) -> json.add(toString.apply(k), toJson.apply(v)));
            return json;
        }

        @Contract(pure = true)
        public static <V> JsonObject toJson(Function<V, JsonElement> toJson, Map<?, V> map) {
            return toJson(Object::toString, toJson, map);
        }

        @Contract(pure = true)
        public static JsonObject jsonToJson(Map<?, JsonElement> map) {
            return toJson(j -> j, map);
        }

        @Contract(pure = true)
        public static JsonObject toJson(Map<?, ?> map) {
            return jsonToJson(ObjectUtil.requireOneOfForEachValue(
                    map,
                    ObjectUtil.classCase(JsonElement.class, j -> (JsonElement) j),
                    ObjectUtil.classCase(Number.class, n -> (JsonElement) new JsonPrimitive(n)),
                    ObjectUtil.classCase(Character.class, c -> (JsonElement) new JsonPrimitive(c)),
                    ObjectUtil.classCase(String.class, s -> (JsonElement) new JsonPrimitive(s)),
                    ObjectUtil.classCase(Enum.class, e -> (JsonElement) JsonUtil.JEnum.toJson(e))
            ));
        }

        @Contract(pure = true)
        public static JsonObject numberToJson(Map<?, Number> map) {
            return toJson(JsonPrimitive::new, map);
        }

        @Contract(pure = true)
        public static JsonObject charToJson(Map<?, Character> map) {
            return toJson(JsonPrimitive::new, map);
        }

        @Contract(pure = true)
        public static JsonObject stringToJson(Map<?, String> map) {
            return toJson(JsonPrimitive::new, map);
        }

        @Contract(pure = true)
        public static JsonObject boolToJson(Map<?, Boolean> map) {
            return toJson(JsonPrimitive::new, map);
        }
    }

    public static class JEnum {
        @Contract(pure = true)
        public static JsonPrimitive toJson(Object o) {
            if (o.getClass().isEnum()) {
                return toJson((Enum<?>) o);
            } else {
                throw new IllegalArgumentException("Expected Enum but found " + o.getClass().getSimpleName());
            }
        }

        @Contract(pure = true)
        public static JsonPrimitive toJson(Enum<?> e) {
            return new JsonPrimitive(e.name());
        }
    }

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
    @Contract(pure = true)
    public static <V> JsonObject toJsonObject(Map<Object, V> map, Function<V, JsonElement> vToJson) {
        return toJsonObject(map, Object::toString, vToJson);
    }

    @Contract(pure = true)
    public static <K, V> JsonObject toJsonObject(Map<K, V> map, Function<K, String> kToString, Function<V, JsonElement> vToJson) {
        JsonObject json = new JsonObject();
        map.forEach((k, v) -> json.add(kToString.apply(k), vToJson.apply(v)));
        return json;
    }

    @Contract(pure = true)
    public static void handle(Action action, JsonElement cause, int line) {
        Objects.requireNonNull(action);
        try {
            action.run();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing " + cause + " at line " + line, e);
        }
    }
    @Contract(pure = true)
    public static void handle(Action action, JsonElement cause) {
        Objects.requireNonNull(action);
        try {
            action.run();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing " + cause, e);
        }
    }
    @Contract(pure = true)
    public static void handle(Action action, int line) {
        Objects.requireNonNull(action);
        try {
            action.run();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing line " + line, e);
        }
    }
    @Contract(pure = true)
    public static <T> T handle(Supplier<T> action, JsonElement cause, int line) {
        Objects.requireNonNull(action);
        try {
            return action.get();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing " + cause + " at line " + line, e);
        }
    }
    @Contract(pure = true)
    public static <T> T handle(Supplier<T> action, JsonElement cause) {
        Objects.requireNonNull(action);
        try {
            return action.get();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing " + cause, e);
        }
    }
    @Contract(pure = true)
    public static <T> T handle(Supplier<T> action, int line) {
        Objects.requireNonNull(action);
        try {
            return action.get();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing line " + line, e);
        }
    }
    @Contract(pure = true)
    public static <T> T require(JsonElement json, Class<T> cl) {
        return handle(() -> ObjectUtil.require(json, cl), json);
    }
    @Contract(pure = true)
    public static <T extends JsonElement> T requireNonNull(T json) {
        return handle(() -> Objects.requireNonNull(json), json);
    }

    @Contract(value = "null -> fail", pure = true)
    public static Map<String, JsonElement> toStringMap(JsonElement json) {
        handle(() -> ObjectUtil.requireOneOf(json, JsonObject.class, JsonNull.class), json);
        if (json instanceof JsonNull)
            return new HashMap<>(0);
        if (json instanceof JsonObject jsonObject) {
            Map<String, JsonElement> stringMap = new HashMap<>(jsonObject.size());
            jsonObject.keySet().forEach(key -> {
                stringMap.put(key, jsonObject.get(key));
            });
            return stringMap;
        }
        throw new Error(JsonUtil.class.getName() + "#requireOneOf is broken");
    }

    @Contract(value = "null -> fail", pure = true)
    public static JsonObject toJsonObject(Map<String, JsonElement> stringMap) {
        Objects.requireNonNull(stringMap);
        JsonObject object = new JsonObject();
        stringMap.forEach(object::add);
        return object;
    }

    @Contract(pure = true)
    public static FileUtil.Reader reader(@NotNull Consumer<JsonElement> reader) {
        return fileReader -> reader.accept(JsonParser.parseReader(fileReader));
    }

    private static final Gson gson = new Gson();
    @Contract(pure = true)
    public static FileUtil.Writer writer(@NotNull Supplier<JsonElement> writer) {
        return fileWriter -> {
            fileWriter.write(gson.toJson(writer.get()));
            fileWriter.flush();
        };
    }
}
