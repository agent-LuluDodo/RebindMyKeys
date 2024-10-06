package de.luludodo.rebindmykeys.api.config.serializer;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;

public abstract class MapSerializer<K, V> implements JsonSerializer<HashMap<K, V>>, JsonDeserializer<HashMap<K, V>> {
    private int version;
    public void setVersion(int version) {
        this.version = version;
    }
    public int getVersion() {
        return version;
    }

    @Override
    public HashMap<K, V> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        return deserializeContent(json.get("content"), json.get("version").getAsInt(), type, jsonDeserializationContext);
    }

    @Override
    public JsonElement serialize(HashMap<K, V> config, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.add("version", new JsonPrimitive(getVersion()));
        json.add("content", serializeContent(config, type, jsonSerializationContext));
        return json;
    }

    public abstract HashMap<K, V> deserializeContent(JsonElement jsonElement, int fromVersion, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException;
    public abstract JsonElement serializeContent(HashMap<K, V> config, Type type, JsonSerializationContext jsonSerializationContext);
}
