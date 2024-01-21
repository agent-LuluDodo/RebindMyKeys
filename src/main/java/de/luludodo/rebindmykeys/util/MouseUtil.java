package de.luludodo.rebindmykeys.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;
import java.util.List;

public class MouseUtil {
    private static final List<KeyBinding> mouseBindings = new ArrayList<>();
    public static KeyBinding registerKeyBinding(KeyBinding keyBinding) {
        mouseBindings.add(keyBinding);
        KeyBindingHelper.registerKeyBinding(keyBinding);
        return keyBinding;
    }

    public static List<KeyBinding> getAll() {
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
