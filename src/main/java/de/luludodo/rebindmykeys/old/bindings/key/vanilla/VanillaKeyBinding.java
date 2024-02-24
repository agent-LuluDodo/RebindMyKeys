package de.luludodo.rebindmykeys.old.bindings.key.vanilla;

import de.luludodo.rebindmykeys.old.bindings.Binding;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;

public interface VanillaKeyBinding extends Binding {
    @Contract("null -> null")
    static VanillaKeyBinding cast(KeyBinding keyBinding) {
        return (VanillaKeyBinding) keyBinding;
    }

    default boolean isActive() {
        return rebindmykeys$isActive();
    }
    default String getId() {
        return rebindmykeys$getId();
    }
    default Text getTranslation() {
        return rebindmykeys$getTranslation();
    }
    default Text getValueTranslation() {
        return rebindmykeys$getValueTranslation();
    }

    boolean rebindmykeys$isActive();
    String rebindmykeys$getId();
    Text rebindmykeys$getTranslation();
    Text rebindmykeys$getValueTranslation();
}
