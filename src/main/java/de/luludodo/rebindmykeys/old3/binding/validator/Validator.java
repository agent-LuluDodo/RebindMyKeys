package de.luludodo.rebindmykeys.old3.binding.validator;

import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;

import java.util.List;

public interface Validator {
    boolean valid(InputUtil.Key key, List<Modifier> modifiers);
}
