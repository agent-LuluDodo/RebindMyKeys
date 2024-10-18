package de.luludodo.rebindmykeys.config.serializer;

import com.google.gson.*;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.api.config.serializer.MapSerializer;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.ComboSettings;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.FilterMode;
import de.luludodo.rebindmykeys.util.InitialKeyBindings;
import de.luludodo.rebindmykeys.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KeyBindingConfigSerializer extends MapSerializer<String, KeyBinding> {
    public static class FakeKeyBinding extends KeyBinding {
        private FakeKeyBinding() {
            super(null, null, new ComboSettings(null, null, false, FilterMode.ALL));
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
    public JsonElement serializeContent(HashMap<String, KeyBinding> config, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        config.forEach((id, keyBinding) -> json.add(id, keyBinding.save()));
        return json;
    }

    @Override
    public HashMap<String, KeyBinding> deserializeContent(JsonElement jsonElement, int fromVersion, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (fromVersion != getVersion())
            throw new JsonParseException("Unsupported version v" + fromVersion + ", current version v" + getVersion());

        JsonObject json = JsonUtil.require(jsonElement, JsonObject.class);
        List<String> loadedIds = new ArrayList<>();
        HashMap<String, KeyBinding> map = new HashMap<>();
        InitialKeyBindings.getAll().forEach(binding -> {
            String id = binding.getId();
            if (json.has(id)) {
                KeyBinding bindingCopy = binding.copy();
                try {
                    bindingCopy.load(json.get(id));
                } catch (RuntimeException e) {
                    RebindMyKeys.LOG.error("Failed to load KeyBinding {}", binding.getId(), e);
                    bindingCopy = binding.copy();
                }
                loadedIds.add(id);
                map.put(id, bindingCopy);
            }
        });
        json.entrySet().forEach(bindingEntry -> {
            String id = bindingEntry.getKey();
            if (!loadedIds.contains(id)) {
                KeyBinding binding = new FakeKeyBinding();
                binding.load(bindingEntry.getValue());
                loadedIds.add(id);
                map.put(id, binding);
            }
        });
        return map;
    }
}
