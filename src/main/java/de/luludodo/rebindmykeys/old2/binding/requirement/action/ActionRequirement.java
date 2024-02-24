package de.luludodo.rebindmykeys.old2.binding.requirement.action;

import de.luludodo.rebindmykeys.util.Modifier;
import de.luludodo.rebindmykeys.old2.binding.requirement.Requirement;
import net.minecraft.client.util.InputUtil;

import java.util.List;

public abstract class ActionRequirement implements Requirement {
    private boolean oldActive = false;
    protected boolean active;

    private ActionMode mode;
    public ActionRequirement(ActionMode mode) {
        this.mode = mode;
    }

    @Override
    public void next() {
        oldActive = active;
        active = false;
    }

    @Override
    public boolean get() {
        return oldActive;
    }

    @Override
    public void onKeyDown(InputUtil.Key key, List<Modifier> modifiers) {
        if (valid(key, modifiers)) {
            switch (mode) {
                case BOTH, PRESS -> active = true;
            }
        }
    }

    @Override
    public void onKeyUp(InputUtil.Key key, List<Modifier> modifiers) {
        if (valid(key, modifiers)) {
            switch (mode) {
                case BOTH, RELEASE -> active = true;
            }
        }
    }

    protected abstract boolean valid(InputUtil.Key key, List<Modifier> modifiers);

    @Override
    public void onCurrentStateChange(boolean currentState) {}
}
