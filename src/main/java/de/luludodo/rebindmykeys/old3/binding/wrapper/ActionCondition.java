package de.luludodo.rebindmykeys.old3.binding.wrapper;

import de.luludodo.rebindmykeys.old3.binding.mode.ActionMode;

public class ActionCondition extends BasicCondition<ActionMode> {
    @Override
    protected void onValidKeyDown() {
        switch (getMode()) {
            case BOTH, PRESS -> nextActive = true;
        }
    }
    @Override
    protected void onValidKeyUp() {
        switch (getMode()) {
            case BOTH, RELEASE -> nextActive = true;
        }
    }

    private boolean currentActive = false;
    private boolean nextActive = false;
    @Override
    public void next() {
        currentActive = nextActive;
        nextActive = false;
    }
    @Override
    public boolean get() {
        return currentActive;
    }
}
