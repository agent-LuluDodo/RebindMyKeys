package de.luludodo.rebindmykeys.keyBindings;

import net.minecraft.client.option.KeyBinding;

public class AdvancedKeyBinding extends KeyBinding {
    private Settings settings;
    public AdvancedKeyBinding(String translationKey, int code, String category) {
        this(translationKey, code, category, Settings.builder().build());
    }
    public AdvancedKeyBinding(String translationKey, int code, String category, Settings settings) {
        super(translationKey, code, category);
        this.settings = settings;
    }
}
