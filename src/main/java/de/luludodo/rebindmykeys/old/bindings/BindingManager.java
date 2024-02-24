package de.luludodo.rebindmykeys.old.bindings;

import java.util.*;

public class BindingManager {
    private static final Map<String, Binding> idToBinding = new HashMap<>();
    private static final List<String> markedForUpdates = new ArrayList<>(1);
    public static void register(String id, Binding binding) {
        if (idToBinding.containsKey(id) && !markedForUpdates.remove(id))
            throw new IllegalArgumentException("Duplicate id '" + id + "'");
        idToBinding.put(id, binding);
    }

    /**
     * Call this before creating a KeyBinding with the same id as another if you intend to replace the original
     * @param id id of the Binding to update/replace
     */
    public static void markForUpdate(String id) {
        if (!idToBinding.containsKey(id))
            throw new IllegalArgumentException("Cannot find Binding with id '" + id + "' to update");
        markedForUpdates.add(id);
    }

    public static Binding get(String id) {
        if (!idToBinding.containsKey(id))
            throw new IllegalArgumentException("Cannot find Binding with id '" + id + "'");
        return idToBinding.get(id);
    }

    public static Set<String> getIds() {
        return idToBinding.keySet();
    }
}
