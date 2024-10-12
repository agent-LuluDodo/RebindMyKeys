package de.luludodo.rebindmykeys.keybindings.registry;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class LuluRegistry<V extends LuluRegistry.Indexable> {
    public interface Indexable {
        Identifier getId();
    }

    public static <V extends LuluRegistry.Indexable> LuluRegistry<V> createFor(Class<V> cl) {
        return create();
    }

    public static <V extends LuluRegistry.Indexable> LuluRegistry<V> create() {
        return new LuluRegistry<>();
    }

    protected LuluRegistry() {}

    private final Map<Identifier, Supplier<? extends V>> registry = new HashMap<>();

    public @Nullable Supplier<? extends V> get(Identifier id) {
        return registry.get(id);
    }

    public Optional<Supplier<? extends V>> getOptional(Identifier id) {
        return Optional.ofNullable(get(id));
    }

    public @Nullable V construct(Identifier id) {
        Supplier<? extends V> entry = registry.get(id);
        return entry == null? null : entry.get();
    }

    public Optional<V> constructOptional(Identifier id) {
        return Optional.ofNullable(construct(id));
    }

    public Collection<Identifier> options() {
        return registry.keySet();
    }

    public boolean contains(Identifier id) {
        return registry.containsKey(id);
    }

    public void register(Supplier<? extends V> constructor) {
        Identifier id;
        try {
            id = constructor.get().getId();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid constructor, failed construction", e);
        }

        if (registry.containsKey(id)) throw new IllegalArgumentException("Duplicate id '" + id + "'");
        registry.put(id, constructor);
    }

    @SafeVarargs
    public final void register(Supplier<? extends V>... constructors) {
        for (Supplier<? extends V> constructor : constructors) {
            register(constructor);
        }
    }
}
