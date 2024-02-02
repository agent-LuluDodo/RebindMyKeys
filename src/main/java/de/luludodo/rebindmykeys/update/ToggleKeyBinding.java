package de.luludodo.rebindmykeys.update;

import net.minecraft.client.util.InputUtil;

public class ToggleKeyBinding extends KeyBinding {
    private ToggleOperationMode mode;
    private final ToggleOperationMode defaultMode;
    public ToggleKeyBinding(String id, InputUtil.Key key, ToggleOperationMode mode) {
        super(id, key);
        this.mode = mode;
        this.defaultMode = mode;
    }

    @Override
    public void onPressed(InputUtil.Key key) {
        switch (mode) {
            case TOGGLE -> {
                if (equalsBoundKey(key)) {
                    active = !active;
                }
            }
            case ACTIVATE, HOLD -> {
                if (equalsBoundKey(key)) {
                    active = true;
                }
            }
            case DEACTIVATE -> {
                if (equalsBoundKey(key)) {
                    active = false;
                }
            }
        }
    }

    @Override
    public void onReleased(InputUtil.Key key) {
        switch (mode) {
            case HOLD -> {
                if (equalsBoundKey(key)) {
                    active = false;
                }
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        mode = defaultMode;
    }
}
