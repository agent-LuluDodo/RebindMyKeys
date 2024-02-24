package de.luludodo.rebindmykeys.util;

import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

import static de.luludodo.rebindmykeys.util.InputUtil2.*;

public enum Modifier {
    SHIFT("rebindmykeys.modifier.shift", Set.of(keysm(GLFW.GLFW_KEY_LEFT_SHIFT), keysm(GLFW.GLFW_KEY_RIGHT_SHIFT))),
    ALT("rebindmykeys.modifier.alt", Set.of(keysm(GLFW.GLFW_KEY_LEFT_ALT), keysm(GLFW.GLFW_KEY_RIGHT_ALT))),
    CONTROL("rebindmykeys.modifier.control", Set.of(keysm(GLFW.GLFW_KEY_LEFT_CONTROL), keysm(GLFW.GLFW_KEY_RIGHT_CONTROL))),
    SUPER("rebindmykeys.modifier.super", Set.of(keysm(GLFW.GLFW_KEY_LEFT_SUPER), keysm(GLFW.GLFW_KEY_RIGHT_SUPER))),
    CAPS_LOCK("rebindmykeys.modifier.caps-lock", Set.of(keysm(GLFW.GLFW_KEY_CAPS_LOCK))),
    NUM_LOCK("rebindmykeys.modifier.num-lock", Set.of(keysm(GLFW.GLFW_KEY_NUM_LOCK)));

    final String id;
    final Set<InputUtil.Key> keys;
    Modifier(String id, Set<InputUtil.Key> keys) {
        this.id = id;
        this.keys = keys;
    }

    public Text getTranslation() {
        return Text.translatable(id);
    }

    /**
     * @return Key which will trigger the Modifier (not necessarily all)
     */
    public Set<InputUtil.Key> getKeys() {
        return keys;
    }
}
