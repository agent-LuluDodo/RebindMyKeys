package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.binding.KeyBinding;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.BiConsumer;

import net.minecraft.client.util.InputUtil;

public class InputUtil2 {
    public static InputUtil.Key keysm(int code) {
        return InputUtil.Type.KEYSYM.createFromCode(code);
    }
    public static InputUtil.Key mouse(int code) {
        return InputUtil.Type.MOUSE.createFromCode(code);
    }
    public static InputUtil.Key scancode(int code) {
        return InputUtil.Type.SCANCODE.createFromCode(code);
    }
    public static InputUtil.Key keysmOrScancode(int keysm, int scancode) {
        if (keysm == -1) {
            return scancode(scancode);
        } else {
            return keysm(keysm);
        }
    }
    public static InputUtil.Key key(int keysm, int scancode, int mouse) {
        if (keysm == -1) {
            return keysm(keysm);
        } else if (scancode == -1) {
            return scancode(scancode);
        } else {
            return mouse(mouse);
        }
    }

    public static boolean keysmPressed(int code) {
        return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), code) == GLFW.GLFW_PRESS;
    }
    public static boolean mousePressed(int button) {
        return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), button) == GLFW.GLFW_PRESS;
    }
    private static final Set<Integer> pressedScancodes = new HashSet<>();
    public static boolean scancodePressed(int button) {
        return pressedScancodes.contains(button);
    }
    public static boolean pressed(InputUtil.Key key) {
        return switch (key.getCategory()) {
            case MOUSE -> mousePressed(key.getCode());
            case KEYSYM -> keysmPressed(key.getCode());
            case SCANCODE -> scancodePressed(key.getCode());
        };
    }

    public static List<Modifier> getModifiers(int modifierFlags) {
        List<Modifier> modifiers = new ArrayList<>();
        if ((modifierFlags & GLFW.GLFW_MOD_SHIFT) != 0)
            modifiers.add(Modifier.SHIFT);
        if ((modifierFlags & GLFW.GLFW_MOD_CONTROL) != 0)
            modifiers.add(Modifier.CONTROL);
        if ((modifierFlags & GLFW.GLFW_MOD_ALT) != 0)
            modifiers.add(Modifier.ALT);
        if ((modifierFlags & GLFW.GLFW_MOD_SUPER) != 0)
            modifiers.add(Modifier.SUPER);
        if ((modifierFlags & GLFW.GLFW_MOD_CAPS_LOCK) != 0)
            modifiers.add(Modifier.CAPS_LOCK);
        if ((modifierFlags & GLFW.GLFW_MOD_NUM_LOCK) != 0)
            modifiers.add(Modifier.NUM_LOCK);
        return modifiers;
    }

    private static final Map<String, BiConsumer<InputUtil.Key, List<Modifier>>> onPressListeners = new HashMap<>();
    private static final Map<String, BiConsumer<InputUtil.Key, List<Modifier>>> onRepeatListeners = new HashMap<>();
    private static final Map<String, BiConsumer<InputUtil.Key, List<Modifier>>> onReleaseListeners = new HashMap<>();
    public static void event(int keyCode, int scanCode, int button, int action, int modifierFlags) {
        InputUtil.Key key = key(keyCode, scanCode, button);
        switch (action) {
            case GLFW.GLFW_PRESS -> {
                if (scanCode != -1)
                    pressedScancodes.add(scanCode);
                if (rebinding) {
                    rebindKeys.add(key);
                }
            }
            case GLFW.GLFW_RELEASE -> {
                if (scanCode != -1)
                    pressedScancodes.remove(scanCode);
                if (rebinding) {
                    keyBindingToRebind.setKeys(rebindKeys);
                    rebinding = false;
                    keyBindingToRebind = null;
                    rebindKeys.clear();
                }
            }
        }
        if (rebinding)
            return;
        List<Modifier> modifiers = getModifiers(modifierFlags);
        switch (action) {
            case GLFW.GLFW_PRESS -> onPressListeners.values().forEach(listener -> listener.accept(key, modifiers));
            case GLFW.GLFW_REPEAT -> onRepeatListeners.values().forEach(listener -> listener.accept(key, modifiers));
            case GLFW.GLFW_RELEASE -> onReleaseListeners.values().forEach(listener -> listener.accept(key, modifiers));
        }
    }
    public static void registerListeners(
            String id,
            @Nullable BiConsumer<InputUtil.Key, List<Modifier>> onPress,
            @Nullable BiConsumer<InputUtil.Key, List<Modifier>> onRepeat,
            @Nullable BiConsumer<InputUtil.Key, List<Modifier>> onRelease
    ) {
        if (onPress != null) {
            if (onPressListeners.containsKey(id))
                throw new IllegalArgumentException("Duplicate id '" + id + "' in onPress");
            onPressListeners.put(id, onPress);
        }
        if (onRepeat != null) {
            if (onRepeatListeners.containsKey(id))
                throw new IllegalArgumentException("Duplicate id '" + id + "' in onPress");
            onRepeatListeners.put(id, onRepeat);
        }
        if (onRelease != null) {
            if (onReleaseListeners.containsKey(id))
                throw new IllegalArgumentException("Duplicate id '" + id + "' in onPress");
            onReleaseListeners.put(id, onRelease);
        }
    }
    public static void unregisterListeners(String id) {
        onPressListeners.remove(id);
        onRepeatListeners.remove(id);
        onReleaseListeners.remove(id);
    }

    private static boolean rebinding = false;
    private static KeyBinding<?> keyBindingToRebind = null;
    private static final Set<InputUtil.Key> rebindKeys = new HashSet<>();
    public static void rebindKeyBinding(KeyBinding<?> keyBinding) {
        if (rebinding)
            return;
        keyBindingToRebind = keyBinding;
        rebindKeys.clear();
        rebinding = true;
    }

    public static boolean isRebinding() {
        return rebinding;
    }
}
