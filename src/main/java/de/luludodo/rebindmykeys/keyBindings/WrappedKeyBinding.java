package de.luludodo.rebindmykeys.keyBindings;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public class WrappedKeyBinding extends BasicKeyBinding {
    public InputUtil.Key boundKey;
    public WrappedKeyBinding(String translationKey, int code, String category) {
        super(translationKey, -1, category);
        boundKey = InputUtil.Type.KEYSYM.createFromCode(code);
        defaultKey = boundKey;
    }

    @Override
    public boolean equals(KeyBinding other) {
        return warpMethod(() -> super.equals(other));
    }

    @Override
    public void setBoundKey(InputUtil.Key boundKey) {
        warpMethod(() -> super.setBoundKey(boundKey));
    }

    @Override
    public boolean matchesKey(int keyCode, int scanCode) {
        return warpMethod(() -> super.matchesKey(keyCode, scanCode));
    }

    @Override
    public boolean matchesMouse(int code) {
        return warpMethod(() -> super.matchesMouse(code));
    }

    @Override
    public Text getBoundKeyLocalizedText() {
        return warpMethod(super::getBoundKeyLocalizedText);
    }

    @Override
    public boolean isDefault() {
        return warpMethod(super::isDefault);
    }

    @Override
    public String getBoundKeyTranslationKey() {
        return warpMethod(super::getBoundKeyTranslationKey);
    }

    @Override
    public boolean isUnbound() {
        return warpMethod(super::isUnbound);
    }

    @Override
    public boolean isPressed() {
        return warpMethod(super::isPressed);
    }

    protected void warpMethod(Runnable method) {
        super.boundKey = boundKey;
        method.run();
        boundKey = super.boundKey;
        super.boundKey = InputUtil.UNKNOWN_KEY;
    }

    protected <T> T warpMethod(Supplier<T> method) {
        super.boundKey = boundKey;
        T result = method.get();
        boundKey = super.boundKey;
        super.boundKey = InputUtil.UNKNOWN_KEY;
        return result;
    }
}
