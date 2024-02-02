package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.RebindMyKeys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyUtil {
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

    public static boolean keysmPressed(int code) {
        return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), code) == GLFW.GLFW_PRESS;
    }

    public static boolean mousePressed(int button) {
        return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), button) == GLFW.GLFW_PRESS;
    }

    public static boolean scancodePressed(int button) {
        return scancodePressedMap.getOrDefault(button, false);
    }

    public static boolean pressed(InputUtil.Key key) {
        return switch (key.getCategory()) {
            case MOUSE -> mousePressed(key.getCode());
            case KEYSYM -> keysmPressed(key.getCode());
            case SCANCODE -> scancodePressed(key.getCode());
        };
    }

    private static final Map<Integer, Boolean> scancodePressedMap = new HashMap<>();
    public static void setScancodePressed(long window, int code, int action) {
        if (window != MinecraftClient.getInstance().getWindow().getHandle())
            return;
        switch (action) {
            case GLFW.GLFW_PRESS, GLFW.GLFW_REPEAT -> scancodePressedMap.put(code, true);
            case GLFW.GLFW_RELEASE -> scancodePressedMap.put(code, false);
            default -> RebindMyKeys.LOG.warn("Invalid action " + action);
        }
    }

    private static final List<KeyBinding> mouseListeners = new ArrayList<>();
    public static void registerMouse(KeyBinding keyBinding) {
        mouseListeners.add(keyBinding);
    }

    public static List<KeyBinding> getMouseListeners() {
        return List.copyOf(mouseListeners);
    }

    public static String mouseTranslationKey = null;
}
