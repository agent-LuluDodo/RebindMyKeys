package de.luludodo.rebindmykeys.update;

import net.minecraft.client.util.InputUtil;

public class ActionKeyBinding extends KeyBinding {
    private int pressed = 0;
    private ActionOperationMode mode;
    private final ActionOperationMode defaultMode;
    public ActionKeyBinding(String id, InputUtil.Key key, ActionOperationMode mode) {
        super(id, key);
        this.mode = mode;
        defaultMode = mode;
    }

    public ActionOperationMode getMode() {
        return mode;
    }

    public void setMode(ActionOperationMode mode) {
        this.mode = mode;
    }

    @Override
    public void onPressed(InputUtil.Key key) {
        switch (defaultMode) {
            case BOTH, PRESS -> {
                if (equalsBoundKey(key)) {
                    pressed++;
                    active = true;
                }
            }
        }
    }

    @Override
    public void onReleased(InputUtil.Key key) {
        switch (defaultMode) {
            case BOTH, RELEASE -> {
                if (equalsBoundKey(key)) {
                    pressed++;
                    active = false;
                }
            }
        }
    }

    public boolean wasPressed() {
        if (pressed > 0) {
            pressed--;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void reset() {
        super.reset();
        mode = defaultMode;
    }
}
