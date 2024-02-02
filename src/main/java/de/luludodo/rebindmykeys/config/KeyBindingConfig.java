package de.luludodo.rebindmykeys.config;

import de.luludodo.rebindmykeys.keyBindings.AdvancedKeyBinding;

import java.util.ArrayList;
import java.util.List;

public class KeyBindingConfig {
    private static final List<AdvancedKeyBinding> keyBindings = new ArrayList<>();
    public static void register(AdvancedKeyBinding keyBinding) {
        keyBindings.add(keyBinding);
    }

    public static void save() {

    }

    public static void load() {

    }
}
