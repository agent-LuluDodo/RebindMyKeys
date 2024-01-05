package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.RebindMyKeys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
    @Shadow private boolean renderingChartVisible;
    @Shadow private boolean renderingAndTickChartsVisible;
    @Shadow private boolean packetSizeAndPingChartsVisible;
    @Shadow @Final private MinecraftClient client;

    @ModifyArg(method = "drawLeftText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1), index = 0)
    private Object rebindmykeys$chartsText(Object o) {
        String f3Key = RebindMyKeys.debugKey.getBoundKeyLocalizedText().getString();
        return "Debug charts: [" +
                f3Key + "+" + RebindMyKeys.debugToggleRenderChartKey.getBoundKeyLocalizedText().getString() + "] Profiler " +
                (renderingChartVisible ? "visible" : "hidden") +
                "; [" + f3Key + "+" + RebindMyKeys.debugToggleRenderAndTickChartsKey.getBoundKeyLocalizedText().getString() + "] " +
                (client.getServer() == null ? "FPS " : "FPS + TPS ") +
                (renderingAndTickChartsVisible ? "visible" : "hidden") +
                "; [" + f3Key + "+" + RebindMyKeys.debugTogglePacketSizeAndPingChartsKey.getBoundKeyLocalizedText().getString() + "] " +
                (!client.isInSingleplayer() ? "Bandwidth + Ping" : "Ping") +
                (packetSizeAndPingChartsVisible ? " visible" : " hidden");
    }

    @ModifyArg(method = "drawLeftText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2), index = 0)
    private Object rebindmykeys$helpText(Object o) {
        return "For help: press " +
                RebindMyKeys.debugKey.getBoundKeyLocalizedText().getString() +
                " + " +
                RebindMyKeys.debugPrintHelpKey.getBoundKeyLocalizedText().getString();
    }
}
