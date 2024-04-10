package de.luludodo.rebindmykeys.util.enums;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public enum Mouse {
    LEFT(GLFW.GLFW_MOUSE_BUTTON_LEFT),
    MIDDLE(GLFW.GLFW_MOUSE_BUTTON_MIDDLE),
    RIGHT(GLFW.GLFW_MOUSE_BUTTON_RIGHT);

    private final int button;
    Mouse(int button) {
        this.button = button;
    }

    public int getButton() {
        return button;
    }

    public InputUtil.Key getKey() {
        return InputUtil.Type.MOUSE.createFromCode(button);
    }

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static double getX() {
        return CLIENT.mouse.getX() * CLIENT.getWindow().getScaledWidth() / CLIENT.getWindow().getWidth();
    }
    public static double getY() {
        return CLIENT.mouse.getY() * CLIENT.getWindow().getScaledHeight() / CLIENT.getWindow().getHeight();
    }
}
