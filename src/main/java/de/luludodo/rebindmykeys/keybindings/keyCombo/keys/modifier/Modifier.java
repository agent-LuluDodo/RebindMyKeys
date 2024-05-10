package de.luludodo.rebindmykeys.keybindings.keyCombo.keys.modifier;

import de.luludodo.rebindmykeys.util.enums.Key;
import net.minecraft.client.util.InputUtil;

public enum Modifier {
    SHIFT("rebindmykeys.modifier.shift"),
    CONTROL("rebindmykeys.modifier.control"),
    ALT("rebindmykeys.modifier.alt"),;

    // TODO: replace with saving based system
    static {
        SHIFT.setKeys(new InputUtil.Key[]{Key.LEFT_SHIFT.getKey(), Key.RIGHT_SHIFT.getKey()});
        CONTROL.setKeys(new InputUtil.Key[]{Key.LEFT_CONTROL.getKey(), Key.RIGHT_CONTROL.getKey()});
        ALT.setKeys(new InputUtil.Key[]{Key.LEFT_ALT.getKey(), Key.RIGHT_ALT.getKey()});
    }

    private InputUtil.Key[] keys;
    private final String translationKey;
    Modifier(String translationKey) {
        this.translationKey = translationKey;
    }

    public void setKeys(InputUtil.Key[] keys) {
        this.keys = keys;
    }

    public InputUtil.Key[] getKeys() {
        return keys;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
