package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.util.KeyUtil;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void rebindmykeys$onKey(long window, int keycode, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (window != client.getWindow().getHandle())
            return;
        InputUtil.Key key = InputUtil.fromKeyCode(keycode, scancode);
        //RebindMyKeys.DEBUG.info("A key was " + (action == 1? "pressed" : action == 0? "released" : ("idk [" + action + "]")) + "! keycode: " + keycode + ", scancode: " + scancode + "; key: " + key.getTranslationKey());
        KeyUtil.getAll().forEach(keyBinding -> {
            if (action == GLFW.GLFW_PRESS) {
                keyBinding.onKeyDown(key);
            } else if (action == GLFW.GLFW_RELEASE) {
                keyBinding.onKeyUp(key);
            }
        });
        ci.cancel();
    }
}