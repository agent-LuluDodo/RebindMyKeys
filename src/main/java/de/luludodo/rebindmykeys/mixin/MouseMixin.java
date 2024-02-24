package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.util.InputUtil2;
import net.minecraft.client.Keyboard;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Inject(method = "onMouseButton", at = @At(value = "JUMP", ordinal = 0, shift = At.Shift.AFTER))
    private void rebindmykeys$keyUtilEvent(long window, int button, int action, int modifiers, CallbackInfo ci) {
        InputUtil2.event(-1, -1, button, action, modifiers);
    }
}
