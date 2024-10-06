package de.luludodo.rebindmykeys.util.interfaces;

import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.keybindings.info.KeyBindingInfo;
import de.luludodo.rebindmykeys.util.KeyUtil;

public interface IKeyBinding {
    String getId();
    default KeyBinding get(String prefix) {
        return KeyBinding.get(KeyBindingInfo.processPrefix(prefix) + getId());
    }
}
