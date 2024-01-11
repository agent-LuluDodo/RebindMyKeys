package de.luludodo.rebindmykeys.keyBindings;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class BasicKeyBinding extends KeyBinding {
    public BasicKeyBinding(String translationKey, int code, String category) {
        super(translationKey, InputUtil.Type.KEYSYM, code, category);
    }

    private boolean unsupported = false;
    @Override
    public void setBoundKey(InputUtil.Key boundKey) {
        if (boundKey.getCategory() == InputUtil.Type.MOUSE) {
            unsupported = true;
            boundKey = InputUtil.UNKNOWN_KEY;
        } else {
            unsupported = false;
        }
        super.setBoundKey(boundKey);
    }

    @Override
    public boolean isUnbound() {
        return !isUnsupported() && super.isUnbound();
    }

    public boolean isUnsupported() {
        return unsupported;
    }

    private static final String UNSUPPORTED_TRANSLATION_KEY = "rebindmykeys.key.unsupported";
    @Override
    public Text getBoundKeyLocalizedText() {
        if (isUnsupported()) {
            return Text.translatable(UNSUPPORTED_TRANSLATION_KEY);
        } else {
            return super.getBoundKeyLocalizedText();
        }
    }

    @Override
    public String getBoundKeyTranslationKey() {
        if (isUnsupported()) {
            return UNSUPPORTED_TRANSLATION_KEY;
        } else {
            return super.getBoundKeyTranslationKey();
        }
    }
}
