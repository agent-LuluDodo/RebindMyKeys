package de.luludodo.rebindmykeys.util;

import com.google.gson.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JsonUtil {
    @Contract(value = "null, _ -> fail", pure = true)
    public static void require(JsonElement json, Class<? extends JsonElement> cl) {
        if (json == null)
            throw new JsonParseException("Expected " + cl.getSimpleName() + " but found null");
        if (!cl.isInstance(json))
            throw new JsonParseException("Expected " + cl.getSimpleName() + " but found " + json.getClass().getSimpleName() + " at " + json);
    }
    @Contract(value = "null, _ -> fail", pure = true)
    @SafeVarargs
    public static void requireOneOf(JsonElement json, Class<? extends JsonElement>... cls) {
        if (json == null)
            throw new JsonParseException("Expected one of " + getSimpleName(cls) + " but found null");
        AtomicBoolean isOneOf = new AtomicBoolean(false);
        for (Class<?> cl : cls) {
            if (cl.isInstance(json)) {
                isOneOf.set(true);
                break;
            }
        }
        if (!isOneOf.get())
            throw new JsonParseException("Expected one of " + getSimpleName(cls) + " but found " + json.getClass().getSimpleName() + " at " + json);
    }
    private static String getSimpleName(Class<?>[] cls) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Class<?> cl : cls) {
            if (first) {
                first = false;
                builder.append('[');
            } else {
                builder.append(", ");
            }
            builder.append(cl.getSimpleName());
        }
        return builder.append(']').toString();
    }
    @Contract(value = "null, _ -> fail", pure = true)
    public static void requireArray(JsonElement json, int size) {
        if (json == null)
            throw new JsonParseException("Expected JsonArray but found null");
        if (json instanceof JsonArray jsonArray) {
            if (jsonArray.size() != size)
                throw new JsonParseException("Expected JsonArray with size " + size + " not with size " + jsonArray.size());
        } else {
            throw new JsonParseException("Expected JsonArray but found " + json.getClass().getSimpleName() + " at " + json);
        }
    }
    @Contract(value = "null -> fail", pure = true)
    public static void requireObject(Object o) {
        if (o == null)
            throw new JsonParseException("Object is null");
    }
    @Contract(value = "null -> fail", pure = true)
    public static void requireString(JsonElement json) {
        if (json == null)
            throw new JsonParseException("Expected JsonPrimitive but found null");
        if (json instanceof JsonPrimitive jsonPrimitive) {
            if (!jsonPrimitive.isString())
                throw new JsonParseException("Expected String but found " + json);
        } else {
            throw new JsonParseException("Expected JsonPrimitive but found " + json.getClass().getSimpleName() + " at " + json);
        }
    }
    @Contract(value = "null -> fail", pure = true)
    public static void requireNumber(JsonElement json) {
        if (json == null)
            throw new JsonParseException("Expected JsonPrimitive but found null");
        if (json instanceof JsonPrimitive jsonPrimitive) {
            if (!jsonPrimitive.isNumber())
                throw new JsonParseException("Expected Number but found " + json);
        } else {
            throw new JsonParseException("Expected JsonPrimitive but found " + json.getClass().getSimpleName() + " at " + json);
        }
    }
    @Contract(value = "null -> fail", pure = true)
    public static void requireBoolean(JsonElement json) {
        if (json == null)
            throw new JsonParseException("Expected JsonPrimitive but found null");
        if (json instanceof JsonPrimitive jsonPrimitive) {
            if (!jsonPrimitive.isBoolean())
                throw new JsonParseException("Expected Boolean but found " + json);
        } else {
            throw new JsonParseException("Expected JsonPrimitive but found " + json.getClass().getSimpleName() + " at " + json);
        }
    }

    @Contract(value = "null -> fail", pure = true)
    public static Map<String, JsonElement> toStringMap(JsonElement json) {
        requireOneOf(json, JsonObject.class, JsonNull.class);
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
        requireObject(stringMap);
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
