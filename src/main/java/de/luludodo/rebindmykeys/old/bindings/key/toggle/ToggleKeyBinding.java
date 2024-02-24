package de.luludodo.rebindmykeys.old.bindings.key.toggle;

import de.luludodo.rebindmykeys.old.bindings.BindingManager;
import de.luludodo.rebindmykeys.util.Modifier;
import de.luludodo.rebindmykeys.old.bindings.key.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.List;

public class ToggleKeyBinding extends KeyBinding {
    private ToggleOperationMode mode;
    private final ToggleOperationMode defaultMode;
    public ToggleKeyBinding(String id, InputUtil.Key key, ToggleOperationMode mode) {
        super(id, key);
        this.mode = mode;
        this.defaultMode = mode;
        BindingManager.register(id, this);
    }

    @Override
    public void onPressed(InputUtil.Key key, List<Modifier> modifiers) {
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
    public void onReleased(InputUtil.Key key, List<Modifier> modifiers) {
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
