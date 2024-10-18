package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.modSupport.VanillaKeyBindingHelper;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InputUtil.Type.class)
public class InputUtilTypeMixin {
    @Inject(method = "createFromCode", at = @At("HEAD"), cancellable = true)
    public void rebindmykeys$createFromCode(int code, CallbackInfoReturnable<InputUtil.Key> cir) {
        if (code <= -1000) {
            cir.setReturnValue(new VanillaKeyBindingHelper.SpecialKey(code, "rebindmykeys.specialKey"));
        }
    }
}
