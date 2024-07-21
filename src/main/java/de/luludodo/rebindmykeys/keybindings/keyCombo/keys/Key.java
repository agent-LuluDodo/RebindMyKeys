package de.luludodo.rebindmykeys.keybindings.keyCombo.keys;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.basic.BasicKey;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.modifier.ModifierKey;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.reference.KeyReference;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public interface Key extends JsonSavable {
    void onKeyDown(InputUtil.Key key);
    void onKeyUp(InputUtil.Key key);
    void release();
    boolean isPressed();
    Text getText();

    static JsonObject save(Key key) {
        return JsonUtil.object()
                .add("type", KeyTypes.get(key))
                .add("settings", key)
                .build();
    }

    static Key load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        return switch (loader.get("type", KeyTypes.class)) {
            case BASIC -> new BasicKey(loader.get("settings", JsonElement.class));
            case MODIFIER -> new ModifierKey(loader.get("settings", JsonElement.class));
            case REFERENCE -> new KeyReference(loader.get("settings", JsonElement.class));
        };
    }
}
