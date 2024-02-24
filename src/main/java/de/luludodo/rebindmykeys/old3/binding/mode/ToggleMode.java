package de.luludodo.rebindmykeys.old3.binding.mode;

import de.luludodo.rebindmykeys.util.TranslationUtil;
import net.minecraft.text.Text;

public enum ToggleMode implements Mode {
    TOGGLE(TranslationUtil.TOGGLE),
    HOLD(TranslationUtil.HOLD),
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
