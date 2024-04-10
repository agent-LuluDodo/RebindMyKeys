package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.util.enums.KeyBindings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Debug(export = true)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    private boolean firstTime = true;
    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 7))
    public boolean rebindmykeys$isDropKeyPressed(KeyBinding instance) {
        if (!firstTime) {
            firstTime = true;
            return false;
        }
        return KeyBindings.DROP.get().isActive() || KeyBindings.DROP_STACK.get().isActive();
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z", ordinal = 0))
    public boolean rebindmykeys$shouldDropStack() {
        firstTime = false;
        return KeyBindings.DROP_STACK.get().isActive();
    }
}
