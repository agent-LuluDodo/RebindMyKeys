package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.util.KeyUtil;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Implementations of methods from KeyBinding which my mod breaks.
 */
@Debug(export = true)
@Mixin(KeyBinding.class)
public class KeyBindingMixin {
    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void rebindmykeys$onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        // simulate a short keypress (method adds one to timesPressed in vanilla and leaves pressed alone)
        KeyUtil.getAll().forEach(binding -> binding.onKeyDown(key));
        KeyUtil.getAll().forEach(binding -> binding.onKeyUp(key));
        ci.cancel();
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void rebindmykeys$setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        KeyUtil.getAll().forEach(binding -> {
            if (pressed) {
                binding.onKeyDown(key);
            } else {
                binding.onKeyUp(key);
            }
        });
        ci.cancel();
    }

    @Inject(method = "unpressAll", at = @At("HEAD"), cancellable = true)
    private static void rebindmykeys$unpressAll(CallbackInfo ci) { // why did they call this "unpress" instead of release?
        KeyUtil.getAll().forEach(de.luludodo.rebindmykeys.keybindings.KeyBinding::release);
    }

    @Inject(method = "untoggleStickyKeys", at = @At("HEAD"), cancellable = true)
    private static void untoggleStickyKeys(CallbackInfo ci) {
        // TODO: figure out what StickyKeyBinding is and if I need to do something here
        ci.cancel();
    }

    @Inject(method = "updatePressedStates", at = @At("HEAD"), cancellable = true)
    private static void rebindmykeys$updatePressedStates(CallbackInfo ci) {
        // Idk what purpose this had in Vanilla, but it's breaking movement keys with my mod
        ci.cancel();
    }
}
