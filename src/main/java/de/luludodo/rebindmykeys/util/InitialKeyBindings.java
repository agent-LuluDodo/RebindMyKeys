package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class InitialKeyBindings {
    private InitialKeyBindings() {
        throw new UnsupportedOperationException();
    }

    private static final Map<String, KeyBinding> initialKeyBindings = new HashMap<>();
    public static void add(KeyBinding keyBinding) {
        initialKeyBindings.put(keyBinding.getId(), keyBinding);
    }

    /**
     * <b>Use {@link KeyBinding#get(String)} instead of this!</b>
     */
    public static @Nullable KeyBinding get(String id) {
        return initialKeyBindings.get(id);
    }

    public static Map<String, KeyBinding> getDefaults() {
        return initialKeyBindings;
    }

    private static boolean active = true;
    public static boolean isActive() {
        return active;
    }

    public static void disable() {
        active = false;
    }
    public static void enable() {
        active = true;
    }
}
