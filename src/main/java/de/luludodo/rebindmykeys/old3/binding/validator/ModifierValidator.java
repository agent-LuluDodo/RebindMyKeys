package de.luludodo.rebindmykeys.old3.binding.validator;

import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModifierValidator implements Validator {
    @NotNull private Modifier modifier;
    public ModifierValidator(@NotNull Modifier modifier) {
        this.modifier = modifier;
    }

    public void setKey(@NotNull Modifier modifier) {
        this.modifier = modifier;
    }
    public @NotNull Modifier getKey() {
        return modifier;
    }

    @Override
    public boolean valid(InputUtil.Key key, List<Modifier> modifiers) {
        return modifiers.contains(this.modifier);
    }
}
