package de.luludodo.rebindmykeys.old3.binding.validator;

import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KeyValidator implements Validator {
    @NotNull private InputUtil.Key key;
    public KeyValidator(@NotNull InputUtil.Key key) {
        this.key = key;
    }

    public void setKey(@NotNull InputUtil.Key key) {
        this.key = key;
    }
    public @NotNull InputUtil.Key getKey() {
        return key;
    }

    @Override
    public boolean valid(InputUtil.Key key, List<Modifier> modifiers) {
        return this.key.equals(key);
    }
}
