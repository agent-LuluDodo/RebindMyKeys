package de.luludodo.rebindmykeys.keyBindings_old;

import net.minecraft.client.util.InputUtil;

public class SplitKeyBinding extends CustomKeyBinding {
    public SplitKeyBinding(String translationKey, InputUtil.Key key, String category, Type type, String partnerKey, Type partnerType) {
        super(translationKey, key, category, type);
        CustomKeyBinding.registerSpecialKeyBinding(partnerKey, partnerType);
    }
    public SplitKeyBinding(String translationKey, int code, String category, Type type, String partnerKey, Type partnerType) {
        super(translationKey, code, category, type);
        CustomKeyBinding.registerSpecialKeyBinding(partnerKey, partnerType);
    }
}
