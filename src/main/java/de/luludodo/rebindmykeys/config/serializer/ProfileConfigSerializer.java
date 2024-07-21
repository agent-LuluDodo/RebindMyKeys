package de.luludodo.rebindmykeys.config.serializer;

import com.google.gson.*;
import de.luludodo.rebindmykeys.api.config.serializer.MapSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;

import static de.luludodo.rebindmykeys.config.ProfileConfig.*;

public class ProfileConfigSerializer implements MapSerializer<String, Object> {
    @Override
    public JsonElement serialize(HashMap<String, Object> config, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.add(CURRENT_PROFILE, new JsonPrimitive((String) config.get(CURRENT_PROFILE)));
        return json;
    }

    @Override
    public HashMap<String, Object> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        HashMap<String, Object> config = new HashMap<>();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        config.put(CURRENT_PROFILE, jsonObject.get(CURRENT_PROFILE).getAsString());
        return config;
    }
}
