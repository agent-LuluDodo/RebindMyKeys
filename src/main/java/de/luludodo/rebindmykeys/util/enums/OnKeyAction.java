package de.luludodo.rebindmykeys.util.enums;

import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.minecraft.client.MinecraftClient;

import java.util.function.Consumer;
import java.util.function.Supplier;

public enum OnKeyAction {
    START_DEBUG_CRASH,
    STOP_DEBUG_CRASH,
    TOGGLE_FULLSCREEN,
    ACTION_SCREENSHOT,
    ACTION_CYCLE_NARRATOR,
    TOGGLE_DEBUG_HUD,
    TOGGLE_POST_PROCESSOR,
    TOGGLE_HUD,
    ACTION_PAUSE,
    ACTION_PAUSE_WITHOUT_MENU, // FIXME: Maybe works differently
    TOGGLE_PROFILER_CHART,
    TOGGLE_FRAME_TIME_CHARTS,
    TOGGLE_NETWORK_CHARTS,
    ACTION_RELOAD_CHUNKS,
    TOGGLE_HITBOXES,
    ACTION_COPY_LOCATION,
    ACTION_CLEAR_CHAT,
    TOGGLE_CHUNK_BORDERS,
    TOGGLE_ADVANCED_TOOLTIPS,
    ACTION_COPY_SERVER_DATA,
    ACTION_COPY_CLIENT_DATA,
    TOGGLE_DEBUG_PROFILER,
    TOGGLE_SPECTATOR,
    TOGGLE_PAUSE_ON_LOST_FOCUS,
    ACTION_PRINT_HELP,
    ACTION_DUMP_TEXTURES,
    ACTION_RELOAD_RESOURCES,
    ACTION_OPEN_GAMEMODE_SWITCHER;

    OnKeyAction() {}

    public void trigger() {
        currentAction = this;
        MinecraftClient.getInstance().keyboard.onKey(-1, -1, -1, -1, -1);
    }

    public Action action() {
        return this::trigger;
    }

    private boolean currentState = false;
    public Consumer<Boolean> toggle() {
        return newValue -> {
            if (newValue != currentState) {
                currentState = newValue;
                trigger();
            }
        };
    }

    public Consumer<Boolean> toggle(Supplier<Boolean> getCurrentState) {
        return newValue -> {
            if (newValue != getCurrentState.get()) {
                trigger();
            }
        };
    }

    private static OnKeyAction currentAction;
    public static boolean hasCurrentAction() {
        return currentAction != null;
    }
    public static OnKeyAction consumeCurrentAction() {
        if (!hasCurrentAction()) throw new IllegalStateException("currentAction is null");
        OnKeyAction currentActionCopy = currentAction;
        currentAction = null;
        return currentActionCopy;
    }
}
