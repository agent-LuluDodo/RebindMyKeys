package de.luludodo.rebindmykeys;

import de.luludodo.rebindmykeys.keyBindings.CustomKeyBinding;
import de.luludodo.rebindmykeys.keyBindings.SplitKeyBinding;
import de.luludodo.rebindmykeys.keyBindings.Type;
import de.luludodo.rebindmykeys.keyBindings.combinations.CtrlKeyBinding;
import de.luludodo.rebindmykeys.keyBindings.combinations.DebugKeyBinding;
import de.luludodo.rebindmykeys.meta.Comparator;
import de.luludodo.rebindmykeys.util.MouseUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.luludodo.rebindmykeys.util.KeyUtil.*;

public class RebindMyKeys implements ClientModInitializer {
    public static final Logger LOG = LoggerFactory.getLogger("RebindMyKeys");

    public static KeyBinding escapeKey;
    public static KeyBinding narratorKey;
    public static KeyBinding toggleHudKey;
    public static KeyBinding debugKey;
    public static KeyBinding debugCrashKey;
    public static KeyBinding debugToggleRenderChartKey;
    public static KeyBinding debugToggleRenderAndTickChartsKey;
    public static KeyBinding debugTogglePacketSizeAndPingChartsKey;
    public static KeyBinding debugReloadChunksKey;
    public static KeyBinding debugShowHitboxKey;
    public static KeyBinding debugCopyLocationKey;
    public static KeyBinding debugClearChatKey;
    public static KeyBinding debugToggleChunkBordersKey;
    public static KeyBinding debugToggleAdvancedTooltipsKey;
    public static KeyBinding debugCopyLookAtKey;
    public static KeyBinding debugToggleDebugProfilerKey;
    public static KeyBinding debugSwitchSpectatorKey;
    public static KeyBinding debugPauseOnLostFocusKey;
    public static KeyBinding debugPrintHelpKey;
    public static KeyBinding debugDumpDynamicTexturesKey;
    public static KeyBinding debugReloadResourcepacksKey;
    public static KeyBinding debugOpenGamemodeSelectorKey;
    public static KeyBinding dismountKey; // Suggestion by @tadm12 (https://github.com/agent-LuluDodo/RebindMyKeys/issues/2)
    public static KeyBinding refreshServers; // Suggestion by @BioTechproject27 (https://github.com/agent-LuluDodo/RebindMyKeys/issues/4)
    @Override
    public void onInitializeClient() {
        /* ONLY FOR DEBUGGING (getting the stack trace of a gl error)
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            GLFW.glfwSetErrorCallback((errorCode, description) -> {
                throw new RuntimeException("GLFW ERROR " + errorCode + ": " + description);
            });
        });
         */
        escapeKey = MouseUtil.registerKeyBinding(new CustomKeyBinding(
                "rebindmykeys.key.escape",
                keysm(InputUtil.GLFW_KEY_ESCAPE),
                KeyBinding.MISC_CATEGORY,
                Type.EVERYWHERE
        ));
        narratorKey = MouseUtil.registerKeyBinding(new CtrlKeyBinding(
                "rebindmykeys.key.narrator",
                InputUtil.GLFW_KEY_B,
                KeyBinding.MISC_CATEGORY,
                Type.EVERYWHERE
        ));
        toggleHudKey = MouseUtil.registerKeyBinding(new CustomKeyBinding(
                "rebindmykeys.key.toggle-hud",
                InputUtil.GLFW_KEY_F1,
                KeyBinding.MISC_CATEGORY,
                Type.GAME
        ));
        debugKey = MouseUtil.registerKeyBinding(new CustomKeyBinding(
                "rebindmykeys.key.debug",
                InputUtil.GLFW_KEY_F3,
                KeyBinding.MISC_CATEGORY,
                Type.GAME
        ));
        debugCrashKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.crash",
                -1,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        if (Comparator.compareMc(">=1.20.2")) {
            debugToggleRenderChartKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                    "rebindmykeys.key.toggle-render-chart",
                    InputUtil.GLFW_KEY_1,
                    "rebindmykeys.key.categories.debug",
                    Type.GAME
            ));
            debugToggleRenderAndTickChartsKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                    "rebindmykeys.key.toggle-render-and-tick-charts",
                    InputUtil.GLFW_KEY_2,
                    "rebindmykeys.key.categories.debug",
                    Type.GAME
            ));
            debugTogglePacketSizeAndPingChartsKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                    "rebindmykeys.key.toggle-packet-size-and-ping-charts",
                    InputUtil.GLFW_KEY_3,
                    "rebindmykeys.key.categories.debug",
                    Type.GAME
            ));
        }
        debugReloadChunksKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.reload-chunks",
                InputUtil.GLFW_KEY_A,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugShowHitboxKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.show-hitbox",
                InputUtil.GLFW_KEY_B,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugCopyLocationKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.copy-location",
                InputUtil.GLFW_KEY_C,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugClearChatKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.clear-chat",
                InputUtil.GLFW_KEY_D,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugToggleChunkBordersKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.toggle-chunk-borders",
                InputUtil.GLFW_KEY_G,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugToggleAdvancedTooltipsKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.toggle-advanced-tooltips",
                InputUtil.GLFW_KEY_H,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugCopyLookAtKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.copy-look-at",
                InputUtil.GLFW_KEY_I,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugToggleDebugProfilerKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.toggle-debug-profiler",
                InputUtil.GLFW_KEY_L,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugSwitchSpectatorKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.switch-spectator",
                InputUtil.GLFW_KEY_N,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugPauseOnLostFocusKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.pause-on-lost-focus",
                InputUtil.GLFW_KEY_P,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugPrintHelpKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.print-help",
                InputUtil.GLFW_KEY_Q,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugDumpDynamicTexturesKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.dump-dynamic-textures",
                InputUtil.GLFW_KEY_S,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugReloadResourcepacksKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.reload-resourcepacks",
                InputUtil.GLFW_KEY_T,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        debugOpenGamemodeSelectorKey = MouseUtil.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.open-gamemode-selector",
                InputUtil.GLFW_KEY_F4,
                "rebindmykeys.key.categories.debug",
                Type.GAME
        ));
        dismountKey = MouseUtil.registerKeyBinding(new SplitKeyBinding(
                "rebindmykeys.key.dismount",
                InputUtil.GLFW_KEY_LEFT_SHIFT,
                KeyBinding.GAMEPLAY_CATEGORY,
                Type.MOUNTED,
                "key.sneak",
                Type.UNMOUNTED
        ));
        refreshServers = MouseUtil.registerKeyBinding(new CustomKeyBinding(
                "rebindmykeys.key.refresh-servers",
                InputUtil.GLFW_KEY_F5,
                KeyBinding.MISC_CATEGORY,
                Type.MULTIPLAYER_MENU
        ));
    }
}
