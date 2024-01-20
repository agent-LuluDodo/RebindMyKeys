package de.luludodo.rebindmykeys.keyBindings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class NoConflictKeyBinding extends WrappedKeyBinding {
    public NoConflictKeyBinding(String translationKey, int code, String category) {
        super(translationKey, code, category);
    }

    @Override
    public boolean equals(KeyBinding other) {
        return false;
    }

    @Override
    public boolean isPressed() {
        return boundKey.getCode() != -1 && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), boundKey.getCode());
    }
}
