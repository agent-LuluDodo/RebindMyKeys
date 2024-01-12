package de.luludodo.rebindmykeys.keyBindings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.HashSet;

public class CombinationKeyBinding extends WrappedKeyBinding {
    public CombinationKeyBinding(String translationKey, int code, String category) {
        super(translationKey, code, category);
    }

    public HashSet<InputUtil.Key> combinationKeys() {
        return new HashSet<>(0);
    }

    @Override
    public boolean equals(KeyBinding other) {
        return other instanceof CombinationKeyBinding otherComb? (combinationKeys().equals(otherComb.combinationKeys()) && boundKey == otherComb.boundKey) : (combinationKeys().isEmpty() && warpMethod(() -> super.equals(other)));
    }

    @Override
    public boolean isPressed() {
        return boundKey.getCode() != -1 && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), boundKey.getCode());
    }
}
