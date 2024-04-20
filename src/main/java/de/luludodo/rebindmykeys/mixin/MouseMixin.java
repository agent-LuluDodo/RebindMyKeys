package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.util.KeyBindingUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
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
@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    public void rebindmykeys$onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window != client.getWindow().getHandle()) return; // if the minecraft window isn't focused return
        if (action != GLFW.GLFW_PRESS && action != GLFW.GLFW_RELEASE) return; // if the action is not press and not release

        InputUtil.Key key = InputUtil.Type.MOUSE.createFromCode(button);
        KeyBindingUtil.onKeyAll(key, action == GLFW.GLFW_PRESS);

        KeyBindingUtil.updateAll();

        ci.cancel();
    }
}
