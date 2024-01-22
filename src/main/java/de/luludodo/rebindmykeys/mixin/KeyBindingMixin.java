package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.keyBindings.CustomKeyBinding;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Debug(export = true)
@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @ModifyArg(method = "updateKeysByCode", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), index = 0)
    private static Object rebindmykeys$keepVanillaKeyBinds(Object boundKey, Object keyBinding) {
        if (keyBinding instanceof CustomKeyBinding) {
            return InputUtil.UNKNOWN_KEY;
        } else {
            return boundKey;
        }
    }
}
