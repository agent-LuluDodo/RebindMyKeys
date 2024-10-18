package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.keybindings.info.KeyBindingInfo;
import de.luludodo.rebindmykeys.keybindings.info.VanillaKeyBindingInfo;
import de.luludodo.rebindmykeys.modSupport.VanillaKeyBindingWrapper;
import de.luludodo.rebindmykeys.util.FabricUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBindingHelper.class)
public abstract class KeyBindingHelperMixin {
    @Inject(method = "registerKeyBinding", at = @At("HEAD"))
    private static void rebindmykeys$registerKeyBinding(KeyBinding value, CallbackInfoReturnable<KeyBinding> cir) {
        String id = value.getTranslationKey();
        ModContainer mod = FabricUtil.getMod();
        KeyBindingInfo.add(id, value.getCategory(), mod);
        VanillaKeyBindingInfo.add(value, mod);

        VanillaKeyBindingWrapper.wrapAndRegister(value);
    }
}
