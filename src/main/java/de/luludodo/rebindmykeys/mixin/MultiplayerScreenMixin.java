package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.RebindMyKeys;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.Debug;
@Debug(export = true)
@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin {
    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = 294))
    private int rebindmykeys$refreshServers(int constant, int keyCode, int scanCode, int modifiers) {
        return RebindMyKeys.refreshServers.matchesKey(keyCode, scanCode)? keyCode : -1;
    }
}
