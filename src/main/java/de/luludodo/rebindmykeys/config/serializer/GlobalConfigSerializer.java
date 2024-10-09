package de.luludodo.rebindmykeys.config.serializer;

import com.google.gson.*;
import de.luludodo.rebindmykeys.api.config.serializer.MapSerializer;
import de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget.SortAfter;
import de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget.SortOrder;
import de.luludodo.rebindmykeys.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.HashMap;

import static de.luludodo.rebindmykeys.config.GlobalConfig.*;

public class GlobalConfigSerializer extends MapSerializer<String, Object> {
    @Override
    public JsonElement serializeContent(HashMap<String, Object> config, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject global = new JsonObject();
        global.add(NAME, new JsonPrimitive((String)config.get(NAME)));
        global.add(HIDE_ESSENTIAL_KEYS, new JsonPrimitive((boolean)config.get(HIDE_ESSENTIAL_KEYS)));
        global.add(MULTI_CLICK_DELAY_MS, new JsonPrimitive((long)config.get(MULTI_CLICK_DELAY_MS)));
        global.add(DEBUG_CRASH_TIME, new JsonPrimitive((long)config.get(DEBUG_CRASH_TIME)));
        global.add(DEBUG_CRASH_JAVA_TIME, new JsonPrimitive((long)config.get(DEBUG_CRASH_JAVA_TIME)));
        global.add(VERTICAL_SCROLL_SPEED_MODIFIER, new JsonPrimitive((double)config.get(VERTICAL_SCROLL_SPEED_MODIFIER)));
        global.add(HORIZONTAL_SCROLL_SPEED_MODIFIER, new JsonPrimitive((double)config.get(HORIZONTAL_SCROLL_SPEED_MODIFIER)));
        global.add(SHOW_CONFIRM_POPUPS, new JsonPrimitive((boolean)config.get(SHOW_CONFIRM_POPUPS)));
        global.add(SORT_AFTER, JsonUtil.JEnum.toJson(config.get(SORT_AFTER)));
        global.add(SORT_ORDER, JsonUtil.JEnum.toJson(config.get(SORT_ORDER)));
        return global;
    }

    @Override
    public HashMap<String, Object> deserializeContent(JsonElement jsonElement, int fromVersion, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (fromVersion != getVersion())
            throw new JsonParseException("Unsupported version v" + fromVersion + ", current version v" + getVersion());

        JsonObject json = jsonElement.getAsJsonObject();
        HashMap<String, Object> global = new HashMap<>();
        global.put(NAME, json.get(NAME).getAsString());
        global.put(HIDE_ESSENTIAL_KEYS, json.get(HIDE_ESSENTIAL_KEYS).getAsBoolean());
        global.put(MULTI_CLICK_DELAY_MS, json.get(MULTI_CLICK_DELAY_MS).getAsLong());
        global.put(DEBUG_CRASH_TIME, json.get(DEBUG_CRASH_TIME).getAsLong());
        global.put(DEBUG_CRASH_JAVA_TIME, json.get(DEBUG_CRASH_JAVA_TIME).getAsLong());
        global.put(VERTICAL_SCROLL_SPEED_MODIFIER, json.get(VERTICAL_SCROLL_SPEED_MODIFIER).getAsDouble());
        global.put(HORIZONTAL_SCROLL_SPEED_MODIFIER, json.get(HORIZONTAL_SCROLL_SPEED_MODIFIER).getAsDouble());
        global.put(SHOW_CONFIRM_POPUPS, json.get(SHOW_CONFIRM_POPUPS).getAsBoolean());
        global.put(SORT_AFTER, JsonUtil.fromJson(json.get(SORT_AFTER), SortAfter.class));
        global.put(SORT_ORDER, JsonUtil.fromJson(json.get(SORT_ORDER), SortOrder.class));
        return global;
    }
}
