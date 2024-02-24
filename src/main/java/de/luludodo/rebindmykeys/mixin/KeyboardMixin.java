package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.util.InputUtil2;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Inject(method = "onKey", at = @At(value = "JUMP", ordinal = 0, shift = At.Shift.AFTER))
    private void rebindmykeys$keyUtilEvent(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        InputUtil2.event(key, scancode, -1, action, modifiers);
    }
}
