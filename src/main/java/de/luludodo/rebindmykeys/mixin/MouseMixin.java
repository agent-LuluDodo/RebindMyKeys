package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.util.MouseUtil;
import net.minecraft.client.Mouse;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.spongepowered.asm.mixin.Debug;
@Debug(export = true)
@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Inject(method = "onMouseButton", at = @At(value = "JUMP", ordinal = 26, shift = At.Shift.BY, by = -2)) // shifts before this.client.currentScreen
    private void rebindmykeys$handleKeyBinds(long window, int button, int action, int mods, CallbackInfo ci) {
        MouseUtil.getAll().forEach(keyBinding -> {
            if (keyBinding.matchesMouse(button))
                MouseUtil.callKeyboard(window, keyBinding.getTranslationKey(), action, mods);
        });
    }
}
