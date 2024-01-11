package de.luludodo.rebindmykeys.keyBindings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class SplitKeyBinding extends BasicKeyBinding {
    private final String partnerKey;
    public SplitKeyBinding(String translationKey, int code, String category, String partnerKey) {
        super(translationKey, -1, category);
        boundKey = InputUtil.Type.KEYSYM.createFromCode(code);
        defaultKey = boundKey;
        this.partnerKey = partnerKey;
    }

    @Override
    public boolean isPressed() {
        return boundKey.getCode() != -1 && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), boundKey.getCode());
    }

    public boolean isPartner(KeyBinding k) {
        return k.getTranslationKey().equals(partnerKey);
    }

    @Override
    public boolean equals(KeyBinding other) {
        return !isPartner(other) && super.equals(other);
    }
}
