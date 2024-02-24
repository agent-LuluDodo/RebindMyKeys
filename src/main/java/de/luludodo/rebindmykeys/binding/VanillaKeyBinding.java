package de.luludodo.rebindmykeys.binding;

import de.luludodo.rebindmykeys.binding.mode.Mode;

public interface VanillaKeyBinding {
    @SuppressWarnings("unchecked")
    static <T extends Mode> KeyBinding<T> cast(net.minecraft.client.option.KeyBinding keyBinding) {
        return (KeyBinding<T>) ((VanillaKeyBinding) keyBinding).rebindmykeys();
    }

    KeyBinding<?> rebindmykeys();
}