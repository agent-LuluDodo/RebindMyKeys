package de.luludodo.rebindmykeys.binding.mode;

import de.luludodo.rebindmykeys.util.TranslationUtil;
import net.minecraft.text.Text;

public enum UnknownMode implements Mode {
    UNKNOWN;

    @Override
    public Text getTranslation() {
        return Text.translatable(TranslationUtil.UNKNOWN);
    }
}
