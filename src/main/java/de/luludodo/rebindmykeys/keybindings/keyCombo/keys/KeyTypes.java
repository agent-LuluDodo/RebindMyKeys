package de.luludodo.rebindmykeys.keybindings.keyCombo.keys;

import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.basic.BasicKey;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.modifier.ModifierKey;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.reference.KeyReference;

public enum KeyTypes {
    BASIC,
    MODIFIER,
    REFERENCE;

    public static KeyTypes get(Key key) {
        if (key instanceof BasicKey) {
            return BASIC;
        } else if (key instanceof ModifierKey) {
            return MODIFIER;
        } else if (key instanceof KeyReference) {
            return REFERENCE;
        }
        throw new IllegalArgumentException("Invalid key");
    }
}
