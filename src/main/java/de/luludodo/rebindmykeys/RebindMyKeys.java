package de.luludodo.rebindmykeys;

import de.luludodo.rebindmykeys.keyBindings.combinations.DebugKeyBinding;
import de.luludodo.rebindmykeys.keyBindings.BasicKeyBinding;
import de.luludodo.rebindmykeys.keyBindings.combinations.NarratorKeyBinding;
import de.luludodo.rebindmykeys.keyBindings.SplitKeyBinding;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.advancement.criterion.StartedRidingCriterion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Mount;

public class RebindMyKeys implements ClientModInitializer {
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
    @Override
    public void onInitializeClient() {
        escapeKey = KeyBindingHelper.registerKeyBinding(new BasicKeyBinding(
                "rebindmykeys.key.escape",
                InputUtil.GLFW_KEY_ESCAPE,
                KeyBinding.MISC_CATEGORY
        ));
        narratorKey = KeyBindingHelper.registerKeyBinding(new NarratorKeyBinding(
                "rebindmykeys.key.narrator",
                InputUtil.GLFW_KEY_B,
                KeyBinding.MISC_CATEGORY
        ));
        toggleHudKey = KeyBindingHelper.registerKeyBinding(new BasicKeyBinding(
                "rebindmykeys.key.toggle-hud",
                InputUtil.GLFW_KEY_F1,
                KeyBinding.MISC_CATEGORY
        ));
        debugKey = KeyBindingHelper.registerKeyBinding(new BasicKeyBinding(
                "rebindmykeys.key.debug",
                InputUtil.GLFW_KEY_F3,
                KeyBinding.MISC_CATEGORY
        ));
        debugCrashKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.crash",
                -1,
                "rebindmykeys.key.categories.debug"
        ));
        debugToggleRenderChartKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.toggle-render-chart",
                InputUtil.GLFW_KEY_1,
                "rebindmykeys.key.categories.debug"
        ));
        debugToggleRenderAndTickChartsKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.toggle-render-and-tick-charts",
                InputUtil.GLFW_KEY_2,
                "rebindmykeys.key.categories.debug"
        ));
        debugTogglePacketSizeAndPingChartsKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.toggle-packet-size-and-ping-charts",
                InputUtil.GLFW_KEY_3,
                "rebindmykeys.key.categories.debug"
        ));
        debugReloadChunksKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.reload-chunks",
                InputUtil.GLFW_KEY_A,
                "rebindmykeys.key.categories.debug"
        ));
        debugShowHitboxKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.show-hitbox",
                InputUtil.GLFW_KEY_B,
                "rebindmykeys.key.categories.debug"
        ));
        debugCopyLocationKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.copy-location",
                InputUtil.GLFW_KEY_C,
                "rebindmykeys.key.categories.debug"
        ));
        debugClearChatKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.clear-chat",
                InputUtil.GLFW_KEY_D,
                "rebindmykeys.key.categories.debug"
        ));
        debugToggleChunkBordersKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.toggle-chunk-borders",
                InputUtil.GLFW_KEY_G,
                "rebindmykeys.key.categories.debug"
        ));
        debugToggleAdvancedTooltipsKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.toggle-advanced-tooltips",
                InputUtil.GLFW_KEY_H,
                "rebindmykeys.key.categories.debug"
        ));
        debugCopyLookAtKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.copy-look-at",
                InputUtil.GLFW_KEY_I,
                "rebindmykeys.key.categories.debug"
        ));
        debugToggleDebugProfilerKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.toggle-debug-profiler",
                InputUtil.GLFW_KEY_L,
                "rebindmykeys.key.categories.debug"
        ));
        debugSwitchSpectatorKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.switch-spectator",
                InputUtil.GLFW_KEY_N,
                "rebindmykeys.key.categories.debug"
        ));
        debugPauseOnLostFocusKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.pause-on-lost-focus",
                InputUtil.GLFW_KEY_P,
                "rebindmykeys.key.categories.debug"
        ));
        debugPrintHelpKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.print-help",
                InputUtil.GLFW_KEY_Q,
                "rebindmykeys.key.categories.debug"
        ));
        debugDumpDynamicTexturesKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.dump-dynamic-textures",
                InputUtil.GLFW_KEY_S,
                "rebindmykeys.key.categories.debug"
        ));
        debugReloadResourcepacksKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.reload-resourcepacks",
                InputUtil.GLFW_KEY_T,
                "rebindmykeys.key.categories.debug"
        ));
        debugOpenGamemodeSelectorKey = KeyBindingHelper.registerKeyBinding(new DebugKeyBinding(
                "rebindmykeys.key.open-gamemode-selector",
                InputUtil.GLFW_KEY_F4,
                "rebindmykeys.key.categories.debug"
        ));
        dismountKey = KeyBindingHelper.registerKeyBinding(new SplitKeyBinding(
                "rebindmykeys.key.dismount",
                InputUtil.GLFW_KEY_LEFT_SHIFT,
                KeyBinding.GAMEPLAY_CATEGORY,
                "key.sneak"
        ));
    }
}
