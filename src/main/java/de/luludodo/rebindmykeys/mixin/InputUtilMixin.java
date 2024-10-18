package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.modSupport.VanillaKeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InputUtil.class)
public class InputUtilMixin {
    @Inject(method = "isKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void rebindmykeys$isKeyPressed(long handle, int code, CallbackInfoReturnable<Boolean> cir) {
        if (code <= -1000) {
            KeyBinding binding = VanillaKeyBindingHelper.getBinding(code);
            if (binding != null) {
                cir.setReturnValue(binding.isPressed());
            }
        }
    }
}
