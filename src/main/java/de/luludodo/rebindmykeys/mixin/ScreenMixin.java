package de.luludodo.rebindmykeys.mixin;

import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
public class ScreenMixin {
    @Redirect(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;shouldCloseOnEsc()Z",
                    ordinal = 0
            )
    )
    private boolean rebindmykeys$fixDoubleClosing(Screen instance) {
        return false;
    }
}
