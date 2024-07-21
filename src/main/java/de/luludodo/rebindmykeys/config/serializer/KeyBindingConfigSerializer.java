package de.luludodo.rebindmykeys.config.serializer;

import com.google.gson.*;
import de.luludodo.rebindmykeys.api.config.serializer.MapSerializer;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.ComboSettings;
import de.luludodo.rebindmykeys.util.InitialKeyBindings;
import de.luludodo.rebindmykeys.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.HashMap;

public class KeyBindingConfigSerializer implements MapSerializer<String, KeyBinding> {
    public static class FakeKeyBinding extends KeyBinding {
        private FakeKeyBinding() {
            super(null, null, null, new ComboSettings(null, null, false, false));
        }

        private JsonElement json;
        @Override
        public JsonElement save() {
            return json;
        }
        @Override
        public void load(JsonElement json) {
            this.json = json;
        }
    }

    @Override
    public HashMap<String, KeyBinding> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject json = JsonUtil.require(jsonElement, JsonObject.class);
        HashMap<String, KeyBinding> map = new HashMap<>();
        json.entrySet().forEach(keyBindingEntry -> {
            String id = keyBindingEntry.getKey();
            KeyBinding keyBinding = InitialKeyBindings.get(keyBindingEntry.getKey());
            if (keyBinding == null) {
                keyBinding = new FakeKeyBinding();
            }
            keyBinding.load(keyBindingEntry.getValue());
            map.put(id, keyBinding);
        });
        return map;
    }

    @Override
    public JsonElement serialize(HashMap<String, KeyBinding> config, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        config.forEach((id, keyBinding) -> json.add(id, keyBinding.save()));
        return json;
    }
}
