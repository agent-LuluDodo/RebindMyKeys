package de.luludodo.rebindmykeys.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import de.luludodo.rebindmykeys.util.ScissorUtil;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Inject(method = "enableScissor", at = @At("HEAD"))
    public void rebindmykeys$enableScissor(
            CallbackInfo ci,
            @Local(argsOnly = true, ordinal = 0) LocalIntRef x1,
            @Local(argsOnly = true, ordinal = 1) LocalIntRef y1,
            @Local(argsOnly = true, ordinal = 2) LocalIntRef x2,
            @Local(argsOnly = true, ordinal = 3) LocalIntRef y2
    ) {
        Vector2i scissorOffset = ScissorUtil.peek();
        x1.set(x1.get() + scissorOffset.x);
        y1.set(y1.get() + scissorOffset.y);
        x2.set(x2.get() + scissorOffset.x);
        y2.set(y2.get() + scissorOffset.y);
    }
}
