package de.luludodo.rebindmykeys.old2.binding.validator;

import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Set;

public class KeyValidator implements Validator {
    
    public KeyValidator(InputUtil.Key key) {

    }

    @Override
    public boolean valid(InputUtil.Key key, List<Modifier> modifiers) {
        return ;
    }

    @Override
    public Text getTranslation() {
        return null;
    }

    @Override
    public Set<InputUtil.Key> getKeysForConflictChecking() {
        return null;
    }
}
