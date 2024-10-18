package de.luludodo.rebindmykeys.keybindings.info;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.option.KeyBinding;

public record VanillaKeyBinding(KeyBinding vanilla, ModContainer mod) {
    public String getId() {
        return vanilla.getTranslationKey();
    }
}
