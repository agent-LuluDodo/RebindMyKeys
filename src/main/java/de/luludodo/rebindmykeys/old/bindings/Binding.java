package de.luludodo.rebindmykeys.old.bindings;

import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Binding extends AutoCloseable {
    @Contract("null -> null")
    static @Nullable Binding cast(Object o) {
        if (o instanceof Binding) {
            return (Binding) o;
        } else {
            return null;
        }
    }
    boolean isActive();
    String getId();
    Text getTranslation();
    Text getValueTranslation();
    Text getKeyTranslation();

    void reset();

    /**
     * ONLY USE THIS WHEN CHECKING FOR CONFLICTS
     */
    Set<InputUtil.Key> getKeys();
}
