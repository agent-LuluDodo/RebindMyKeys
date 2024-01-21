package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.meta.Comparator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.Debug;
@Debug(export = true)
@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
    @ModifyArg(method = "drawLeftText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1), index = 0)
    private Object rebindmykeys$chartsText(Object o) {
        if (Comparator.compareMc("<1.20.2")) {
            return o;
        }
        if (o instanceof String s) {
            String f3Key = RebindMyKeys.debugKey.getBoundKeyLocalizedText().getString();
            String[] parts = s.split("\\[[^]]*]");
            if (parts.length != 4) {
                return o;
            }
            return parts[0] +
                    "[" + f3Key + "+" + RebindMyKeys.debugToggleRenderChartKey.getBoundKeyLocalizedText().getString() + "]" +
                    parts[1] +
                    "[" + f3Key + "+" + RebindMyKeys.debugToggleRenderAndTickChartsKey.getBoundKeyLocalizedText().getString() + "]" +
                    parts[2] +
                    "[" + f3Key + "+" + RebindMyKeys.debugTogglePacketSizeAndPingChartsKey.getBoundKeyLocalizedText().getString() + "]" +
                    parts[3];
        } else {
            return o;
        }
    }

    @ModifyArg(method = "drawLeftText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2), index = 0)
    private Object rebindmykeys$helpText(Object o) {
        return "For help: press " +
                RebindMyKeys.debugKey.getBoundKeyLocalizedText().getString() +
                " + " +
                RebindMyKeys.debugPrintHelpKey.getBoundKeyLocalizedText().getString();
    }
}
