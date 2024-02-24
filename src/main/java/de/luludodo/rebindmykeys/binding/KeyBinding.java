package de.luludodo.rebindmykeys.binding;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.luludodo.rebindmykeys.binding.mode.ActionMode;
import de.luludodo.rebindmykeys.binding.mode.Mode;
import de.luludodo.rebindmykeys.binding.mode.ToggleMode;
import de.luludodo.rebindmykeys.util.EnumUtil;
import de.luludodo.rebindmykeys.util.InputUtil2;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.Modifier;
import de.luludodo.rebindmykeys.util.interfaces.InputListener;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class KeyBinding<T extends Mode> implements InputListener, JsonSavable {
    private final String id;
    private boolean ordered;
    private final boolean defaultOrdered;
    private T mode;
    private final T defaultMode;
    private List<InputUtil.Key> keys;
    private final List<InputUtil.Key> defaultKeys;
    private List<String> keyBindings;
    private final List<String> defaultKeyBindings;
    public KeyBinding(String id, boolean ordered, T mode, List<InputUtil.Key> keys, List<String> keyBindings) {
        this.id = id;
        this.ordered = ordered;
        this.defaultOrdered = ordered;
        this.mode = mode;
        this.defaultMode = mode;
        this.keys = new ArrayList<>(keys);
        this.defaultKeys = Collections.unmodifiableList(keys);
        this.keyBindings = new ArrayList<>(keyBindings);
        this.defaultKeyBindings = Collections.unmodifiableList(keyBindings);
    }

    public String getId() {
        return id;
    }

    public boolean getOrdered() {
        return ordered;
    }
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public T getMode() {
        return mode;
    }
    public void setMode(T mode) {
        this.mode = mode;
    }

    public List<InputUtil.Key> getKeys() {
        return Collections.unmodifiableList(keys);
    }
    public void setKeys(Collection<InputUtil.Key> keys) {
        this.keys = new ArrayList<>(keys);
    }
    public void rebind() {
        InputUtil2.rebindKeyBinding(this);
    }

    public List<String> getKeyBindings() {
        return Collections.unmodifiableList(keyBindings);
    }
    public void setKeyBindings(Collection<String> keyBindings) {
        this.keyBindings = new ArrayList<>(keyBindings);
    }

    @Override
    public void onKeyDown(InputUtil.Key pressedKey, List<Modifier> modifiers) {
        if (validate(pressedKey)) {
            pressed = true;
            EnumUtil.switchEnum(
                    mode,
                    EnumUtil.switchCase(
                            ActionMode.class,
                            actionMode -> {
                                switch (actionMode) {
                                    case PRESS, BOTH -> timesPressed++;
                                }
                            }
                    ),
                    EnumUtil.switchCase(
                            ToggleMode.class,
                            toggleMode -> {
                                switch (toggleMode) {
                                    case TOGGLE -> toggled = !toggled;
                                    case ACTIVATE, HOLD -> toggled = true;
                                    case DEACTIVATE, RELEASE -> toggled = false;
                                }
                            }
                    )
            );
        }
    }

    @Override
    public void onKeyUp(InputUtil.Key releasedKey, List<Modifier> modifiers) {
        if (pressed && !validate(releasedKey)) {
            pressed = false;
            EnumUtil.switchEnum(
                    mode,
                    EnumUtil.switchCase(
                            ActionMode.class,
                            actionMode -> {
                                switch (actionMode) {
                                    case RELEASE, BOTH -> timesPressed++;
                                }
                            }
                    ),
                    EnumUtil.switchCase(
                            ToggleMode.class,
                            toggleMode -> {
                                switch (toggleMode) {
                                    case HOLD -> toggled = false;
                                    case RELEASE -> toggled = true;
                                }
                            }
                    )
            );
        }
    }

    public boolean validate(InputUtil.Key updatedKey) {
        boolean firstAndOrdered = ordered;
        for (InputUtil.Key key : keys) {
            if (firstAndOrdered) {
                if (!key.equals(updatedKey))
                    return false;
                firstAndOrdered = false;
            } else {
                if (!InputUtil2.pressed(key))
                    return false;
            }
        }
        return toggled;
    }

    private boolean toggled = false;
    public boolean isToggled() {
        return toggled;
    }

    private boolean pressed = false;
    public boolean isPressed() {
        return pressed;
    }

    private int timesPressed = 0;
    public boolean wasPressed() {
        if (timesPressed <= 0)
            return false;
        timesPressed--;
        return false;
    }

    public void reset() {
        ordered = defaultOrdered;
        mode = defaultMode;
        keys = defaultKeys;
        keyBindings = defaultKeyBindings;
    }

    @Override
    public JsonElement save() {
        JsonObject json = new JsonObject();
        json.add("ordered", new JsonPrimitive(ordered));
        json.add("mode", JsonUtil.toJsonEnum(mode));
        json.add("keys", JsonUtil.toJsonArray(key -> {
            JsonObject keyJson = new JsonObject();
            keyJson.add("type", JsonUtil.toJsonEnum(key.getCategory()));
            keyJson.add("code", JsonUtil.toJsonEnum(key.getCode()));
            return keyJson;
        }, keys));
        json.add("keyBindings", JsonUtil.toJsonArray(keyBindings));
        return json;
    }

    @Override
    public void load(JsonElement json) {

    }
}
