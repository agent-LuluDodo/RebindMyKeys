package de.luludodo.rebindmykeys.keybindings;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActionMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.ComboSettings;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.JsonLoadable;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import net.minecraft.client.util.InputUtil;

import java.util.Collections;
import java.util.List;

public class KeyBinding implements JsonSavable, JsonLoadable {
    private final String id;
    private final List<KeyCombo> defaultKeyCombos;
    private List<KeyCombo> keyCombos;
    private final ComboSettings defaultSettings;
    private final boolean isAction;
    public KeyBinding(String id, List<KeyCombo> keyCombos, ComboSettings defaultSettings) {
        this.id = id;
        defaultKeyCombos = keyCombos;
        this.keyCombos = keyCombos;
        this.defaultSettings = defaultSettings;
        isAction = defaultSettings.operationMode() instanceof ActionMode;
    }

    public boolean isAction() {
        return isAction;
    }

    public boolean wasPressed() {
        if (!isAction) throw new IllegalArgumentException("KeyBinding isn't an Action");
        return isActive();
    }

    public boolean isToggled() {
        if (isAction) throw new IllegalArgumentException("KeyBinding is an Action");
        return isActive();
    }

    private boolean oldActive = false;
    public boolean stateChanged() {
        if (isAction) throw new IllegalArgumentException("KeyBinding is an Action");
        boolean active = isActive();
        if (oldActive == active)
            return false;
        oldActive = active;
        return true;
    }

    public String getId() {
        return id;
    }

    public void reset() {
        keyCombos = defaultKeyCombos;
    }

    public void addKeyCombo(KeyCombo keyCombo) {
        keyCombos.add(keyCombo);
    }

    public void removeKeyCombo(KeyCombo keyCombo) {
        keyCombos.remove(keyCombo);
    }

    public ComboSettings getDefaultSettings() {
        return defaultSettings;
    }

    public List<KeyCombo> getKeyCombos() {
        return Collections.unmodifiableList(keyCombos);
    }

    public void tick() {
        keyCombos.forEach(KeyCombo::tick);
    }

    public boolean isPressed() {
        return CollectionUtil.oneCondition(keyCombos, KeyCombo::isPressed);
    }

    public boolean isActive() {
        return CollectionUtil.oneCondition(keyCombos, KeyCombo::isActive);
    }

    public void onKeyDown(InputUtil.Key key) {
        keyCombos.forEach(keyCombo -> keyCombo.onKeyDown(key));
    }
    public void onKeyUp(InputUtil.Key key) {
        keyCombos.forEach(keyCombo -> keyCombo.onKeyUp(key));
    }

    @Override
    public JsonElement save() {
        return JsonUtil.object()
                .add("keyCombos", keyCombos)
                .build();
    }

    @Override
    public void load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        keyCombos = loader.array("keyCombos").toList(KeyCombo::load);
    }

    public void release() {
        keyCombos.forEach(KeyCombo::release);
    }
}
