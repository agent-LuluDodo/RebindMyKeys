package de.luludodo.rebindmykeys.old2.binding.requirement.toggle;

import de.luludodo.rebindmykeys.util.Modifier;
import de.luludodo.rebindmykeys.old2.binding.requirement.Requirement;
import net.minecraft.client.util.InputUtil;

import java.util.List;

public abstract class ToggleRequirement implements Requirement {
    private boolean toggled = false;

    private ToggleMode mode;
    public ToggleRequirement(ToggleMode mode) {
        this.mode = mode;
    }

    public ToggleMode getMode() {
        return mode;
    }
    public void setMode(ToggleMode mode) {
        this.mode = mode;
    }

    @Override
    public void onKeyDown(InputUtil.Key key, List<Modifier> modifiers) {
        if (valid(key, modifiers)) {
            switch (mode) {
                case TOGGLE -> toggled = !toggled;
                case HOLD, ACTIVATE -> toggled = true;
                case DEACTIVATE -> toggled = false;
            }
        }
    }

    @Override
    public void onKeyUp(InputUtil.Key key, List<Modifier> modifiers) {
        if (valid(key, modifiers)) {
            switch (mode) {
                case HOLD -> toggled = false;
            }
        }
    }

    protected abstract boolean valid(InputUtil.Key key, List<Modifier> modifiers);

    @Override
    public boolean get() {
        return toggled;
    }
    @Override
    public void next() {}

    @Override
    public void onCurrentStateChange(boolean currentState) {
        this.toggled = currentState;
    }
}
