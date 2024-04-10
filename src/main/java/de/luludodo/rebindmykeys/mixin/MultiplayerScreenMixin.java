package de.luludodo.rebindmykeys.mixin;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Debug(export = true)
@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {
    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = 294))
    public int rebindmykeys$invalidateF5Refresh(int constant) {
        return -2; // Not -1 cause GLFW can return -1 if it doesn't recognize the key
    }
}
