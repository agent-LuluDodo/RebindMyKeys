package de.luludodo.rebindmykeys.util.interfaces;

import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.util.KeyUtil;

public interface IKeyBinding {
    String getId();
    default KeyBinding get(String prefix) {
        return KeyUtil.get(KeyUtil.processPrefix(prefix) + getId());
    }
}
