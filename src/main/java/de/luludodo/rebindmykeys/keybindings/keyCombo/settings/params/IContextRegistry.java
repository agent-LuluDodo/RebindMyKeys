package de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class IContextRegistry {
    private static final HashMap<String, IContext> idToContext = new HashMap<>();

    public static void register(IContext context) {
        idToContext.put(context.getId(), context);
    }

    public static void register(IContext[] contexts) {
        for (IContext context : contexts) {
            register(context);
        }
    }

    public static void register(Collection<IContext> contexts) {
        for (IContext context : contexts) {
            register(context);
        }
    }

    public static IContext get(String id) {
        return Objects.requireNonNull(idToContext.get(id));
    }
}
