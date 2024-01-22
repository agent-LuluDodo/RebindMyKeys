package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.RebindMyKeys;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Debug;
//@Debug(export = true)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow public abstract boolean startRiding(Entity entity, boolean force);

    public ClientPlayerEntityMixin() {
        super(null, null);
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;sneaking:Z", opcode = Opcodes.GETFIELD))
    private boolean rebindmykeys$sendPlayerInputC2SPacket2(Input instance) {
        return RebindMyKeys.dismountKey.isPressed();
    }

    @Inject(method = "isSneaking", at = @At(value = "HEAD"), cancellable = true)
    private void rebindmykeys$clientSideSneaking(CallbackInfoReturnable<Boolean> cir) {
        if (hasVehicle()) {
            cir.setReturnValue(false);
        }
    }
}
