package de.luludodo.rebindmykeys.binding.mode;

import de.luludodo.rebindmykeys.util.TranslationUtil;
import net.minecraft.text.Text;

public enum ActionMode implements Mode {
    PRESS(TranslationUtil.PRESS),
    RELEASE(TranslationUtil.RELEASE),
    BOTH(TranslationUtil.BOTH);

    private final String translationKey;
    ActionMode(String translationKey) {
        this.translationKey = translationKey;
    }

    @Override
    public Text getTranslation() {
        return Text.translatable(translationKey);
    }
}
