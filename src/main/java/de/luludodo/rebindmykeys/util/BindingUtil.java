package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.binding.KeyBinding;
import de.luludodo.rebindmykeys.util.interfaces.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindingUtil {
    private static final Map<String, KeyBinding<?>> idToBinding = new HashMap<>();
    public static void register(KeyBinding<?> binding) {
        idToBinding.put(binding.getId(), binding);
    }

    private static final List<Action> validatorCallbacks = new ArrayList<>();
    public static void registerLoadingDoneCallback(Action action) {
        validatorCallbacks.add(action);
    }
    public static void onAllIdsLoaded() {
        validatorCallbacks.forEach(Action::run);
    }

    public static KeyBinding<?> get(String id) {
        return idToBinding.get(id);
    }

    public static boolean contains(String id) {
        return idToBinding.containsKey(id);
    }
}
