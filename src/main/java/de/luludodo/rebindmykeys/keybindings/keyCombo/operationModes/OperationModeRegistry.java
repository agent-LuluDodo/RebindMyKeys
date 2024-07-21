package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes;

import com.google.gson.JsonElement;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class OperationModeRegistry {
    public static class Entry<O extends OperationMode> {
        private final Class<O> cl;
        private final Function<JsonElement, O> constructor;
        public Entry(Class<O> cl, Function<JsonElement, O> constructor) {
            this.cl = cl;
            this.constructor = constructor;
        }

        public Class<O> get() {
            return cl;
        }

        public O construct(JsonElement json) {
            return constructor.apply(json);
        }
    }

    private static final Map<Identifier, Entry<? extends OperationMode>> registry = new HashMap<>();
    private static final Map<Class<? extends OperationMode>, Identifier> reverseRegistry = new HashMap<>();

    public static @Nullable Entry<? extends OperationMode> get(Identifier id) {
        return registry.get(id);
    }

    public static Optional<Entry<? extends OperationMode>> getOptional(Identifier id) {
        return Optional.ofNullable(get(id));
    }

    public static @Nullable OperationMode construct(Identifier id, JsonElement json) {
        Entry<? extends OperationMode> entry = registry.get(id);
        return entry == null? null : entry.construct(json);
    }

    public static Optional<OperationMode> constructOptional(Identifier id, JsonElement json) {
        return Optional.ofNullable(construct(id, json));
    }

    public static Identifier getId(Class<? extends OperationMode> mode) {
        return reverseRegistry.get(mode);
    }

    public static Optional<Identifier> getIdOptional(Class<? extends OperationMode> mode) {
        return Optional.ofNullable(getId(mode));
    }

    public static <O extends OperationMode> void register(Identifier id, Class<O> mode, Function<JsonElement, O> constructor) {
        if (registry.containsKey(id)) throw new IllegalArgumentException("Duplicate id '" + id + "'");
        if (reverseRegistry.containsKey(mode)) throw new IllegalArgumentException("Duplicate class '" + mode.getName() + "'");
        registry.put(id, new Entry<>(mode, constructor));
        reverseRegistry.put(mode, id);
    }
}
