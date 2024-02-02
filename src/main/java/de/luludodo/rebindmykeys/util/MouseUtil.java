package de.luludodo.rebindmykeys.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;
import java.util.List;

public class MouseUtil {
    private static final List<KeyBinding> mouseBindings = new ArrayList<>();
    public static void registerKeyBinding(KeyBinding keyBinding) {
        mouseBindings.add(keyBinding);
    }

    public static List<KeyBinding> getAll() {
        List<KeyBinding> mouseBindings = new ArrayList<>(MouseUtil.mouseBindings);
        mouseBindings.add(MinecraftClient.getInstance().options.fullscreenKey);
        mouseBindings.add(MinecraftClient.getInstance().options.screenshotKey);
        return mouseBindings;
    }

    private static String translationKey = null;

    public static boolean wasMouseInput() {
        return translationKey != null;
    }

    public static void callKeyboard(long window, String translationKey, int action, int mods) {
        MouseUtil.translationKey = translationKey;
        MinecraftClient.getInstance().keyboard.onKey(window, 0, 0, action, mods);
    }

    public static String getTranslationKey() {
        return translationKey;
    }

    public static void finishKeyboardCall() {
        translationKey = null;
    }
}
