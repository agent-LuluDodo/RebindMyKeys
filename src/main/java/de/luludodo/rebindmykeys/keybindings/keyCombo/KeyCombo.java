package de.luludodo.rebindmykeys.keybindings.keyCombo;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.ComboSettings;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.IContext;
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
    private boolean pressed = false;
    public KeyCombo(List<Key> keys, ComboSettings settings) {
        this.keys = keys;
        this.settings = settings;
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

    public void tick() {
        updateContextValid();
        /*
        if (!contextValid)
            keys.forEach(Key::release);
         */
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
        if (updatePressed()) return;
        settings.operationMode().onKeyDown();
    }

    public void onKeyUp(InputUtil.Key key) {
        keys.forEach(k -> k.onKeyUp(key));
        if (updatePressed()) return;
        settings.operationMode().onKeyUp();
    }

    /**
     * Updates {@link KeyCombo#pressed}.
     * @return {@code true} if pressed stayed the same otherwise {@code false}.
     */
    private boolean updatePressed() {
        final boolean oldPressed = pressed;
        pressed = CollectionUtil.allConditions(keys, Key::isPressed);
        return pressed == oldPressed;
    }

    public boolean isPressed() {
        return pressed;
    }

    private boolean contextValid = false;
    private void updateContextValid() { // Fixes for example pausing the game only to immediately unpause it since oh escape is pressed and the game is paused xD
        contextValid = ArrayUtil.oneCondition(settings.context(), IContext::isCurrent);
    }

    public boolean isActive() {
        return settings.operationMode().isActive() && contextValid;
    }

    public ComboSettings getSettings() {
        return settings;
    }

    public void setSettings(ComboSettings settings) {
        this.settings = settings;
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
