package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes;

import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActionMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.toggle.ToggleMode;

public enum OperationModeTypes {
    ACTION,
    TOGGLE,
    HOLD;

    public static OperationModeTypes get(OperationMode mode) {
        if (mode instanceof ActionMode) {
            return ACTION;
        } else if (mode instanceof ToggleMode) {
            return TOGGLE;
        } else if (mode instanceof HoldMode) {
            return HOLD;
        }
        throw new IllegalArgumentException("Invalid mode");
    }
}
