package de.luludodo.rebindmykeys.binding.mode;

import de.luludodo.rebindmykeys.util.TranslationUtil;
import net.minecraft.text.Text;

public enum ToggleMode implements Mode {
    TOGGLE(TranslationUtil.TOGGLE),
    HOLD(TranslationUtil.HOLD),
    RELEASE(TranslationUtil.RELEASE),
    ACTIVATE(TranslationUtil.ACTIVATE),
    DEACTIVATE(TranslationUtil.DEACTIVATE);

    private final String translationKey;
    ToggleMode(String translationKey) {
        this.translationKey = translationKey;
    }

    @Override
    public Text getTranslation() {
        return Text.translatable(translationKey);
    }
}
