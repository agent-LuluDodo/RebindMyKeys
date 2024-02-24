package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.binding.VanillaKeyBinding;
import de.luludodo.rebindmykeys.binding.mode.Mode;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements VanillaKeyBinding {
    @Unique
    private de.luludodo.rebindmykeys.binding.KeyBinding<?> rebindmykeys;
    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("RETURN"))
    private void rebindmykeys$init(String translationKey, InputUtil.Type type, int code, String category, CallbackInfo ci) {
        rebindmykeys = new de.luludodo.rebindmykeys.binding.KeyBinding<>(translationKey, true, Mode.UNKNOWN, List.of(type.createFromCode(code)), List.of());
    }

    @Override
    public de.luludodo.rebindmykeys.binding.KeyBinding<?> rebindmykeys() {
        return rebindmykeys;
    }
}
