package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class OperationModeRegistry {
    private static final Map<Identifier, Supplier<? extends OperationMode>> registry = new HashMap<>();

    public static @Nullable Supplier<? extends OperationMode> get(Identifier id) {
        return registry.get(id);
    }

    public static Optional<Supplier<? extends OperationMode>> getOptional(Identifier id) {
        return Optional.ofNullable(get(id));
    }

    public static @Nullable OperationMode construct(Identifier id) {
        Supplier<? extends OperationMode> entry = registry.get(id);
        return entry == null? null : entry.get();
    }

    public static Optional<OperationMode> constructOptional(Identifier id) {
        return Optional.ofNullable(construct(id));
    }

    public static <O extends OperationMode> void register(Supplier<O> constructor) {
        Identifier id;
        try {
            id = constructor.get().getId();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid constructor, failed construction", e);
        }

        if (registry.containsKey(id)) throw new IllegalArgumentException("Duplicate id '" + id + "'");
        registry.put(id, constructor);
    }
}
