package de.luludodo.rebindmykeys.old3.binding.wrapper;

import de.luludodo.rebindmykeys.old3.binding.mode.ToggleMode;

public class ToggleCondition extends BasicCondition<ToggleMode> {
    @Override
    protected void onValidKeyDown() {
        switch (getMode()) {
            case TOGGLE -> active = !active;
            case HOLD, ACTIVATE -> active = true;
            case DEACTIVATE -> active = false;
        }
    }
    @Override
    protected void onValidKeyUp() {
        if (getMode() == ToggleMode.HOLD)
            active = false;
    }

    private boolean active = false;
    @Override
    public void next() {}
    @Override
    public boolean get() {
        return active;
    }

    @Override
    public void setMode(ToggleMode mode) {
        if (getMode() == mode)
            return;
        active = false;
        super.setMode(mode);
    }
}
