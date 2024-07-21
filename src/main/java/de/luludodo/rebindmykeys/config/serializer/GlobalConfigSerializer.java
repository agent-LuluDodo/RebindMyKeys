package de.luludodo.rebindmykeys.config.serializer;

import com.google.gson.*;
import de.luludodo.rebindmykeys.api.config.serializer.MapSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;

import static de.luludodo.rebindmykeys.config.GlobalConfig.*;

public class GlobalConfigSerializer implements MapSerializer<String, Object> {
    @Override
    public JsonElement serialize(HashMap<String, Object> config, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject global = new JsonObject();
        global.add(MULTI_CLICK_DELAY_MS, new JsonPrimitive((long)config.get(MULTI_CLICK_DELAY_MS)));
        global.add(DEBUG_CRASH_TIME, new JsonPrimitive((long)config.get(DEBUG_CRASH_TIME)));
        global.add(DEBUG_CRASH_JAVA_TIME, new JsonPrimitive((long)config.get(DEBUG_CRASH_JAVA_TIME)));
        global.add(VERTICAL_SCROLL_SPEED_MODIFIER, new JsonPrimitive((double)config.get(VERTICAL_SCROLL_SPEED_MODIFIER)));
        global.add(HORIZONTAL_SCROLL_SPEED_MODIFIER, new JsonPrimitive((double)config.get(HORIZONTAL_SCROLL_SPEED_MODIFIER)));
        return global;
    }

    @Override
    public HashMap<String, Object> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        HashMap<String, Object> global = new HashMap<>();
        global.put(MULTI_CLICK_DELAY_MS, json.get(MULTI_CLICK_DELAY_MS).getAsLong());
        global.put(DEBUG_CRASH_TIME, json.get(DEBUG_CRASH_TIME).getAsLong());
        global.put(DEBUG_CRASH_JAVA_TIME, json.get(DEBUG_CRASH_JAVA_TIME).getAsLong());
        global.put(VERTICAL_SCROLL_SPEED_MODIFIER, json.get(VERTICAL_SCROLL_SPEED_MODIFIER).getAsDouble());
        global.put(HORIZONTAL_SCROLL_SPEED_MODIFIER, json.get(HORIZONTAL_SCROLL_SPEED_MODIFIER).getAsDouble());
        return global;
    }
}
