package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class InitialKeyBindings {
    private InitialKeyBindings() {
        throw new UnsupportedOperationException();
    }

    private static final Map<String, KeyBinding> initialKeyBindings = new HashMap<>();
    public static void add(KeyBinding binding) {
        if (initialKeyBindings.containsKey(binding.getId()))
            throw new IllegalArgumentException("A KeyBinding with the id '" + binding.getId() + "' already exists.");

        initialKeyBindings.put(binding.getId(), binding);
    }

    /**
     * <b>Use {@link KeyBinding#get(String)} instead of this!</b>
     */
    public static @Nullable KeyBinding get(String id) {
        return initialKeyBindings.get(id);
    }

    /**
     * <b>Use {@link KeyBinding#getAll()} instead of this!</b>
     */
    public static Collection<KeyBinding> getAll() {
        return initialKeyBindings.values();
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
