package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.keyBindings.SplitKeyBinding;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Inject(method = "equals", at = @At("HEAD"), cancellable = true)
    private void rebindmykeys$equals(KeyBinding other, CallbackInfoReturnable<Boolean> cir) {
        if (other instanceof SplitKeyBinding otherSplit) {
            cir.setReturnValue(otherSplit.equals((KeyBinding) (Object) this));
        }
    }
}
