package de.luludodo.rebindmykeys.keybindings.keyCombo;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.ComboSettings;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.Context;
import de.luludodo.rebindmykeys.util.ArrayUtil;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import net.minecraft.client.util.InputUtil;

import java.util.Collections;
import java.util.List;

public class KeyCombo implements JsonSavable {
    private ComboSettings settings;
    private final List<Key> keys;
    private boolean oldPressed = false;
    private boolean active;
    public KeyCombo(List<Key> keys, ComboSettings settings) {
        this.keys = keys;
        this.settings = settings;
        init();
    }

    private void init() {
        contextValid = settings.contextValid();
        active = settings.operationMode().isActive() && contextValid;
    }

    public boolean move(Key key, int targetIndex) {
        return CollectionUtil.CList.move(keys, key, targetIndex);
    }

    public void addKey(Key key) {
        keys.add(0, key);
    }

    public boolean removeKey(Key key) {
        return keys.remove(key);
    }

    public List<Key> getKeys() {
        return Collections.unmodifiableList(keys);
    }

    public void onKeyDown(InputUtil.Key key) {
        if (settings.orderSensitive()) {
            int left = keys.size();
            boolean allPressed = true;
            for (Key k : keys) {
                if (--left == 0) {
                    if (allPressed) k.onKeyDown(key);
                } else {
                    if (!k.isPressed()) allPressed = false;
                    k.onKeyDown(key);
                }
            }
        } else {
            keys.forEach(k -> k.onKeyDown(key));
        }
    }

    public void onKeyUp(InputUtil.Key key) {
        keys.forEach(k -> k.onKeyUp(key));
    }

    public void updatePressed() {
        boolean pressed = isPressed();
        if (oldPressed == pressed) return;
        if (pressed) {
            settings.operationMode().onKeyDown();
        } else {
            settings.operationMode().onKeyUp();
        }
        oldPressed = pressed;
    }

    public void updateActive() {
        active = settings.operationMode().isActive() && contextValid;
    }

    private boolean contextValid = false;
    public boolean checkContext() {
        boolean oldContextValid = contextValid;
        contextValid = settings.contextValid();
        return contextValid != oldContextValid;
    }

    public boolean isPressed() { // Can't cache here cause of ReferenceKey's :)
        return CollectionUtil.allConditions(keys, Key::isPressed);
    }

    public boolean isActive() {
        return active;
    }

    public ComboSettings getSettings() {
        return settings;
    }

    public void setSettings(ComboSettings settings) {
        this.settings = settings;
        init();
    }

    @Override
    public JsonElement save() {
        return JsonUtil.object()
                .add("settings", settings)
                .add("keys", keys)
                .build();
    }

    public static KeyCombo load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        return new KeyCombo(
                loader.array("keys").toList(Key::load),
                loader.get("settings", ComboSettings::load)
        );
    }

    public void release() {
        keys.forEach(Key::release);
    }
}
