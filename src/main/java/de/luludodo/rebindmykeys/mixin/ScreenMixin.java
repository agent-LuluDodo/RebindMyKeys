package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.RebindMyKeys;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.Debug;
@Debug(export = true)
@Mixin(Screen.class)
public abstract class ScreenMixin {
    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = 256))
    private int rebindmykeys$modifyNarrator(int constant, int keyCode, int scancode, int modifiers) {
        return RebindMyKeys.escapeKey.matchesKey(keyCode, scancode) ? keyCode : -1;
    }
}
