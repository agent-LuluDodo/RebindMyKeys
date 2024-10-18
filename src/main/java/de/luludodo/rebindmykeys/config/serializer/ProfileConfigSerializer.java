package de.luludodo.rebindmykeys.config.serializer;

import com.google.gson.*;
import de.luludodo.rebindmykeys.api.config.serializer.MapSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

import static de.luludodo.rebindmykeys.config.ProfileConfig.*;

@SuppressWarnings("unused")
public class ProfileConfigSerializer extends MapSerializer<String, Object> {
    @Override
    public JsonElement serializeContent(HashMap<String, Object> config, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.add(CURRENT_PROFILE, new JsonPrimitive(config.get(CURRENT_PROFILE).toString()));
        return json;
    }

    @Override
    public HashMap<String, Object> deserializeContent(JsonElement jsonElement, int fromVersion, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (fromVersion != getVersion())
            throw new JsonParseException("Unsupported version v" + fromVersion + ", current version v" + getVersion());

        HashMap<String, Object> config = new HashMap<>();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        config.put(CURRENT_PROFILE, UUID.fromString(jsonObject.get(CURRENT_PROFILE).getAsString()));
        return config;
    }
}
