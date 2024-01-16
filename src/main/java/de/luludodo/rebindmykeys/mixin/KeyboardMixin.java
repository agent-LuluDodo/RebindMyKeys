package de.luludodo.rebindmykeys.mixin;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.TextureUtil;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.meta.Comparator;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.Locale;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow private long debugCrashStartTime;

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void debugLog(String key, Object... args);

    @Shadow public abstract void setClipboard(String clipboard);

    @Shadow protected abstract void copyLookAt(boolean hasQueryPermission, boolean queryServer);

    @Shadow protected abstract void debugLog(Text text);

    @Redirect(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z", ordinal = 0))
    private boolean rebindmykeys$modifyDebug(long handle, int code) {
        return RebindMyKeys.debugKey.isPressed();
    }

    @Redirect(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z", ordinal = 1))
    private boolean rebindmykeys$modifyDebugCrash1(long handle, int code) {
        return RebindMyKeys.debugCrashKey.isPressed();
    }
    @Redirect(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z", ordinal = 2))
    private boolean rebindmykeys$modifyDebugCrash2(long handle, int code) {
        return RebindMyKeys.debugCrashKey.isPressed();
    }

    @ModifyConstant(method = "onKey", constant = @Constant(intValue = 66))
    private int rebindmykeys$modifyNarrator(int constant, long window, int key, int scancode, int action, int modifiers) {
        return RebindMyKeys.narratorKey.matchesKey(key, scancode) ? key : -1;
    }

    @ModifyConstant(method = "onKey", constant = @Constant(intValue = 292, ordinal = 1))
    private int rebindmykeys$modifyDebug(int constant, long window, int key, int scancode, int action, int modifiers) {
        return RebindMyKeys.debugKey.matchesKey(key, scancode) ? key : -1;
    }

    @ModifyConstant(method = "onKey", constant = @Constant(intValue = 256))
    private int rebindmykeys$modifyEscape(int constant, long window, int key, int scancode, int action, int modifiers) {
        return RebindMyKeys.escapeKey.matchesKey(key, scancode) ? key : -1;
    }

    @ModifyConstant(method = "onKey", constant = @Constant(intValue = 290))
    private int rebindmykeys$modifyToggleHUD(int constant, long window, int key, int scancode, int action, int modifiers) {
        return RebindMyKeys.toggleHudKey.matchesKey(key, scancode) ? key : -1;
    }

    @Redirect(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Keyboard;processF3(I)Z"))
    private boolean rebindmykeys$modifyToggleHUD(Keyboard instance, int key, long window, int keyDuplicate, int scancode, int action, int modifiers) {
        if (debugCrashStartTime > 0L && debugCrashStartTime < Util.getMeasuringTimeMs() - 100L) {
            return true;
        } else {
            boolean isDebugCombination = false;
            if (Comparator.compareMc(">=1.20.2")) {
                if (RebindMyKeys.debugToggleRenderChartKey.matchesKey(key, scancode)) {
                    client.getDebugHud().toggleRenderingChart();
                    isDebugCombination = true;
                }
                if (RebindMyKeys.debugToggleRenderAndTickChartsKey.matchesKey(key, scancode)) {
                    client.getDebugHud().toggleRenderingAndTickCharts();
                    isDebugCombination = true;
                }
                if (RebindMyKeys.debugTogglePacketSizeAndPingChartsKey.matchesKey(key, scancode)) {
                    client.getDebugHud().togglePacketSizeAndPingCharts();
                    isDebugCombination = true;
                }
            }
            if (RebindMyKeys.debugReloadChunksKey.matchesKey(key, scancode)) {
                client.worldRenderer.reload();
                debugLog("debug.reload_chunks.message");
                isDebugCombination = true;
            }
            if (RebindMyKeys.debugShowHitboxKey.matchesKey(key, scancode)) {
                boolean bl = !client.getEntityRenderDispatcher().shouldRenderHitboxes();
                client.getEntityRenderDispatcher().setRenderHitboxes(bl);
                debugLog(bl ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
                isDebugCombination = true;
            }
            if (RebindMyKeys.debugCopyLocationKey.matchesKey(key, scancode)) {
                if (!client.player.hasReducedDebugInfo()) {
                    ClientPlayNetworkHandler clientPlayNetworkHandler = client.player.networkHandler;
                    if (clientPlayNetworkHandler != null) {
                        debugLog("debug.copy_location.message");
                        setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", client.player.getWorld().getRegistryKey().getValue(), client.player.getX(), client.player.getY(), client.player.getZ(), client.player.getYaw(), client.player.getPitch()));
                        isDebugCombination = true;
                    }
                }
            }
            if (RebindMyKeys.debugClearChatKey.matchesKey(key, scancode)) {
                if (client.inGameHud != null) {
                    client.inGameHud.getChatHud().clear(false);
                }
                isDebugCombination = true;
            }
            if (RebindMyKeys.debugToggleChunkBordersKey.matchesKey(key, scancode)) {
                boolean bl2 = client.debugRenderer.toggleShowChunkBorder();
                debugLog(bl2 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
                isDebugCombination = true;
            }
            if (RebindMyKeys.debugToggleAdvancedTooltipsKey.matchesKey(key, scancode)) {
                client.options.advancedItemTooltips = !client.options.advancedItemTooltips;
                debugLog(client.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
                client.options.write();
                isDebugCombination = true;
            }
            if (RebindMyKeys.debugCopyLookAtKey.matchesKey(key, scancode)) {
                if (!client.player.hasReducedDebugInfo()) {
                    copyLookAt(client.player.hasPermissionLevel(2), !Screen.hasShiftDown());
                }
                isDebugCombination = true;
            }
            if (RebindMyKeys.debugToggleDebugProfilerKey.matchesKey(key, scancode)) {
                if (client.toggleDebugProfiler(this::debugLog)) {
                    debugLog("debug.profiling.start", 10);
                }
                isDebugCombination = true;
            }
            if (RebindMyKeys.debugSwitchSpectatorKey.matchesKey(key, scancode)) {
                if (!client.player.hasPermissionLevel(2)) {
                    debugLog("debug.creative_spectator.error");
                } else if (!client.player.isSpectator()) {
                    client.player.networkHandler.sendCommand("gamemode spectator");
                } else {
                    client.player.networkHandler.sendCommand("gamemode " + MoreObjects.firstNonNull(client.interactionManager.getPreviousGameMode(), GameMode.CREATIVE).getName());
                }
                isDebugCombination = true;
            }
            if (RebindMyKeys.debugPauseOnLostFocusKey.matchesKey(key, scancode)) {
                client.options.pauseOnLostFocus = !client.options.pauseOnLostFocus;
                client.options.write();
                debugLog(client.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
                isDebugCombination = true;
            }
            if (RebindMyKeys.debugPrintHelpKey.matchesKey(key, scancode)) {
                debugLog("debug.help.message");
                ChatHud chatHud = client.inGameHud.getChatHud();
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.reload_chunks.help"), RebindMyKeys.debugReloadChunksKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.show_hitboxes.help"), RebindMyKeys.debugShowHitboxKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.copy_location.help"), RebindMyKeys.debugCopyLocationKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.clear_chat.help"), RebindMyKeys.debugClearChatKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.chunk_boundaries.help"), RebindMyKeys.debugToggleChunkBordersKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.advanced_tooltips.help"), RebindMyKeys.debugToggleAdvancedTooltipsKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.inspect.help"), RebindMyKeys.debugCopyLookAtKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.profiling.help"), RebindMyKeys.debugToggleDebugProfilerKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.creative_spectator.help"), RebindMyKeys.debugSwitchSpectatorKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.pause_focus.help"), RebindMyKeys.debugPauseOnLostFocusKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.help.help"), RebindMyKeys.debugPrintHelpKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.dump_dynamic_textures.help"), RebindMyKeys.debugDumpDynamicTexturesKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.reload_resourcepacks.help"), RebindMyKeys.debugReloadResourcepacksKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.pause.help"), RebindMyKeys.escapeKey));
                chatHud.addMessage(rebindmykeys$debugMessageWithCorrectKeybinds(Text.translatable("debug.gamemodes.help"), RebindMyKeys.debugOpenGamemodeSelectorKey));
                isDebugCombination = true;
            }
            if (RebindMyKeys.debugDumpDynamicTexturesKey.matchesKey(key, scancode)) {
                Path path = client.runDirectory.toPath().toAbsolutePath();
                Path path2 = TextureUtil.getDebugTexturePath(path);
                client.getTextureManager().dumpDynamicTextures(path2);
                Text text = Text.literal(path.relativize(path2).toString()).formatted(Formatting.UNDERLINE).styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path2.toFile().toString())));
                debugLog("debug.dump_dynamic_textures", text);
                isDebugCombination = true;
            }
            if (RebindMyKeys.debugReloadResourcepacksKey.matchesKey(key, scancode)) {
                debugLog("debug.reload_resourcepacks.message");
                client.reloadResources();
                isDebugCombination = true;
            }
            if (RebindMyKeys.debugOpenGamemodeSelectorKey.matchesKey(key, scancode)) {
                if (!client.player.hasPermissionLevel(2)) {
                    debugLog("debug.gamemodes.error");
                } else {
                    client.setScreen(new GameModeSelectionScreen());
                }
                isDebugCombination = true;
            }
            return isDebugCombination;
        }
    }

    @Unique
    private String rebindmykeys$key;
    @Inject(method = "debugLog(Ljava/lang/String;[Ljava/lang/Object;)V", at = @At("HEAD"))
    private void rebindmykeys$getKey(String key, Object[] args, CallbackInfo ci) {
        this.rebindmykeys$key = key;
    }
    @ModifyArg(method = "debugLog(Ljava/lang/String;[Ljava/lang/Object;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Keyboard;debugLog(Lnet/minecraft/text/Text;)V"))
    private Text rebindmykeys$replacePollDebugCrashInitMessage(Text text) {
        if ("debug.crash.message".equals(rebindmykeys$key)) {
            return rebindmykeys$debugMessageWithCorrectKeybinds(text, RebindMyKeys.debugCrashKey);
        }
        return text;
    }

    @Unique
    private Text rebindmykeys$debugMessageWithCorrectKeybinds(Text original, KeyBinding keyBind) {
        String[] textParts = original.getString().split(" ", 4);
        if (textParts.length < 4) {
            return original;
        }
        if (keyBind == RebindMyKeys.debugCopyLocationKey) {
            String[] textSplitOnF3 = textParts[3].split(InputUtil.Type.KEYSYM.createFromCode(InputUtil.GLFW_KEY_F3).getLocalizedText().getString(), 2);
            if (textSplitOnF3.length < 2) {
                return original;
            }
            String[] textSplitOnC = textSplitOnF3[1].split(InputUtil.Type.KEYSYM.createFromCode(InputUtil.GLFW_KEY_C).getLocalizedText().getString(), 2);
            if (textSplitOnC.length < 2) {
                return original;
            }
            textParts[3] = textSplitOnF3[0] + RebindMyKeys.debugKey.getBoundKeyLocalizedText().getString() + textSplitOnC[0] + RebindMyKeys.debugCrashKey.getBoundKeyLocalizedText().getString() + textSplitOnC[1];
        }
        return RebindMyKeys.debugKey.getBoundKeyLocalizedText().copy().append(" " + textParts[1] + " ").append(keyBind.getBoundKeyLocalizedText()).append(" " + textParts[3]);
    }
}
