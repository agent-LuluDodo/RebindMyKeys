package de.luludodo.rebindmykeys.keybindings.registry;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.JsonLoadable;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import net.minecraft.util.Identifier;

import java.util.NoSuchElementException;

public class JsonRegistry<V extends LuluRegistry.Indexable & JsonLoadable & JsonSavable> extends LuluRegistry<V> {
    public static <V extends LuluRegistry.Indexable & JsonLoadable & JsonSavable> JsonRegistry<V> createJsonFor(Class<V> cl) {
        return createJson();
    }

    public static <V extends LuluRegistry.Indexable & JsonLoadable & JsonSavable> JsonRegistry<V> createJson() {
        return new JsonRegistry<>();
    }

    protected JsonRegistry() {}

    public V load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        JsonElement settings = loader.get("settings", JsonElement.class);
        V mode = constructOptional(loader.get("type", Identifier.class)).orElseThrow(() -> new NoSuchElementException("No type '" + loader.get("type", Identifier.class).toString() + "' present"));
        mode.load(settings);
        return mode;
    }

    public JsonElement save(V value) {
        return JsonUtil.object()
                .add("type", value.getId())
                .add("settings", value.save())
                .build();
    }

    public V copy(V original) {
        return load(save(original));
    }
}
