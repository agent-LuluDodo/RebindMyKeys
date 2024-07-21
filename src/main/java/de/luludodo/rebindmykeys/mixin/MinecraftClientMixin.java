package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.gui.binding.screen.KeyBindingScreen;
import de.luludodo.rebindmykeys.util.KeyBindingUtil;
import de.luludodo.rebindmykeys.util.TickUtil;
import de.luludodo.rebindmykeys.util.enums.KeyBindings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Debug(export = true)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Unique
    private boolean rebindmykeys$firstTime = true;
    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 7))
    public boolean rebindmykeys$isDropKeyPressed(KeyBinding instance) {
        if (!rebindmykeys$firstTime) {
            rebindmykeys$firstTime = true;
            return false;
        }
        return KeyBindings.DROP.get("key").isActive() || KeyBindings.DROP_STACK.get("rebindmykeys.key").isActive();
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z", ordinal = 0))
    public boolean rebindmykeys$shouldDropStack() {
        rebindmykeys$firstTime = false;
        return KeyBindings.DROP_STACK.get("rebindmykeys.key").isActive();
    }

    @Inject(method = "setScreen", at = @At("RETURN"))
    private void rebindmykeys$setScreen(Screen screen, CallbackInfo ci) {
        final int hash = Objects.hashCode(screen); // we don't need to keep the entire screen object for memory reasons (might have a lot of references to other objects, etc.)
        TickUtil.schedule(() -> {
            if (Objects.hashCode(MinecraftClient.getInstance().currentScreen) != hash) return; // If screen changes multiple times in one tick only do for the most recent one
            //RebindMyKeys.DEBUG.info("Screen changed to " + MinecraftClient.getInstance().currentScreen);
            KeyBindingUtil.checkContext();
        });
    }

    @ModifyVariable(method = "setScreen", at = @At("HEAD"), index = 1, argsOnly = true)
    public Screen rebindmykeys$setScreen(Screen screen) {
        return screen instanceof KeybindsScreen keybindsScreen? new KeyBindingScreen(keybindsScreen.parent) : screen;
    }
}
