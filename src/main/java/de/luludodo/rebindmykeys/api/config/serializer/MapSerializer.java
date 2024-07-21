package de.luludodo.rebindmykeys.api.config.serializer;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;

public interface MapSerializer<K, V> extends JsonSerializer<HashMap<K, V>>, JsonDeserializer<HashMap<K, V>> {
    @Override
    HashMap<K, V> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException;

    @Override
    JsonElement serialize(HashMap<K, V> config, Type type, JsonSerializationContext jsonSerializationContext);
}
