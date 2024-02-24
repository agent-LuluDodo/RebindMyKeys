package de.luludodo.rebindmykeys.old2.binding.validator;

import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Set;

public interface Validator {
    boolean valid(InputUtil.Key key, List<Modifier> modifiers);
    Text getTranslation();
    Set<InputUtil.Key> getKeysForConflictChecking();
}
