package de.luludodo.rebindmykeys.potential.binding.button;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.luludodo.rebindmykeys.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ButtonManager {
    private static final Map<String, Function<JsonElement, Button>> typeToCreator = new HashMap<>();
    public static void register(String type, Function<JsonElement, Button> creator) {
        if (typeToCreator.containsKey(type))
            throw new IllegalArgumentException("Duplicate type '" + type + "'");
        typeToCreator.put(type, creator);
    }

    public static Button fromJson(JsonElement json) {
        JsonObject buttonJson = JsonUtil.require(json, JsonObject.class);
        JsonElement typeJson = buttonJson.get("type");
        String type = JsonUtil.requireString(typeJson);
        return JsonUtil.handle(() -> {
            if (typeToCreator.containsKey(type))
                return typeToCreator.get(type);
            throw new IllegalArgumentException("Type '" + type + "' is not registered");
        }, typeJson).apply(JsonUtil.requireNonNull(buttonJson.get("options")));
    }
}
