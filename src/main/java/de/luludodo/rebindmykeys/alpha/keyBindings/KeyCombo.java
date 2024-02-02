package de.luludodo.rebindmykeys.alpha.keyBindings;

import de.luludodo.rebindmykeys.util.CollectionUtil;
import de.luludodo.rebindmykeys.util.KeyUtil;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class KeyCombo {
    private final List<InputUtil.Key> keys;
    @NotNull private Mode mode;
    private boolean active;
    public KeyCombo(@NotNull Mode mode) {
        checkMode(mode);
        keys = new ArrayList<>();
        this.mode = mode;
        active = false;
    }
    public KeyCombo(@NotNull Mode mode, InputUtil.Key... initialKeys) {
        this(mode, Arrays.asList(initialKeys));
    }
    public KeyCombo(@NotNull Mode mode, Collection<InputUtil.Key> initialKeys) {
        checkMode(mode);
        keys = new ArrayList<>(initialKeys.size());
        keys.addAll(initialKeys);
        this.mode = mode;
        active = false;
    }

    public int addKey(InputUtil.Key key) {
        keys.add(key);
        return keys.indexOf(key);
    }
    public void removeKey(int index) {
        keys.remove(index);
    }
    public void editKey(int index, InputUtil.Key newKey) {
        keys.remove(index);
        keys.add(index, newKey);
    }
    public void moveKey(int from, int to) {
        InputUtil.Key key = keys.remove(from);
        keys.add(to, key);
    }
    public InputUtil.Key getKey(int index) {
        return keys.get(index);
    }
    public InputUtil.Key getKeyOrUnknown(int index) {
        return (index >= 0 && index < keys.size())? keys.get(index) : InputUtil.UNKNOWN_KEY;
    }
    public List<InputUtil.Key> getKeys() {
        return Collections.unmodifiableList(keys);
    }

    public void checkMode(Mode mode) {
        if (mode == null)
            throw new NullPointerException("Mode cannot be null");
        if (mode == Mode.UNKNOWN)
            throw new IllegalArgumentException("Mode cannot be UNKNOWN");
    }
    public void setMode(@NotNull Mode mode) {
        checkMode(mode);
        if (this.mode == mode)
            return;
        active = false;
        this.mode = mode;
    }
    public @NotNull Mode getMode() {
        return mode;
    }

    public void onKey(InputUtil.Key key, int action) {
        switch (action) {
            case GLFW.GLFW_PRESS -> onKeyPressed(key);
            case GLFW.GLFW_RELEASE -> onKeyReleased(key);
        }
    }
    private void onKeyPressed(InputUtil.Key key) {
        // Check if the key pressed is relevant
        if (keys.isEmpty() || key != keys.get(0))
            return;
        for (int keyIndex = 1; keyIndex < keys.size(); keyIndex++) {
            if (!KeyUtil.pressed(keys.get(keyIndex)))
                return;
        }

        switch (mode) {
            case TOGGLE -> active = !active;
            case HOLD -> active = true;
        }
    }
    private void onKeyReleased(InputUtil.Key key) {
        // Check if the key released is relevant
        if (!keys.contains(key))
            return;

        switch (mode) {
            case HOLD -> active = false;
        }
    }
    public boolean isActive() {
        return active;
    }

    public boolean equals(KeyCombo keyCombo) {
        return CollectionUtil.equalsIgnoreOrder(keys, keyCombo.keys) && mode == keyCombo.mode;
    }
}
