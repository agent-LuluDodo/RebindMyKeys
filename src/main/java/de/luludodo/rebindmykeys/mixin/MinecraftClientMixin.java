package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.util.KeyBindingUtil;
import de.luludodo.rebindmykeys.util.TickUtil;
import de.luludodo.rebindmykeys.util.enums.KeyBindings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

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

    @Inject(method = "setScreen", at = @At("RETURN"))
    private void rebindmykeys$setScreen(Screen screen, CallbackInfo ci) {
        final int hash = Objects.hashCode(screen); // we don't need to keep the entire screen object for memory reasons (might have a lot of references to other objects, etc.)
        TickUtil.schedule(() -> {
            if (Objects.hashCode(MinecraftClient.getInstance().currentScreen) != hash) return; // If screen changes multiple times in one tick only do for the most recent one
            //RebindMyKeys.DEBUG.info("Screen changed to " + MinecraftClient.getInstance().currentScreen);
            KeyBindingUtil.checkContextAll();
        });
    }
}
