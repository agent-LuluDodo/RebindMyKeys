package de.luludodo.rebindmykeys.util.interfaces;

import de.luludodo.rebindmykeys.util.InputUtil2;
import de.luludodo.rebindmykeys.util.Modifier;

import java.util.List;

public interface InputListener {
    default void initInputListener(String id) {
        InputUtil2.registerListeners(id, this::onKeyDown, null, this::onKeyUp);
    }
    default void unregisterInputListener(String id) {
        InputUtil2.unregisterListeners(id);
    }
    void onKeyDown(net.minecraft.client.util.InputUtil.Key key, List<Modifier> modifiers);
    void onKeyUp(net.minecraft.client.util.InputUtil.Key key, List<Modifier> modifiers);
}
