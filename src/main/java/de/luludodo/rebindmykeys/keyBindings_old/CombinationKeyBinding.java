package de.luludodo.rebindmykeys.keyBindings_old;

import de.luludodo.rebindmykeys.util.CollectionUtil;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.Set;

public abstract class CombinationKeyBinding extends CustomKeyBinding {
    public CombinationKeyBinding(String translationKey, InputUtil.Key key, String category, Type type) {
        super(translationKey, key, category, type);
    }

    public CombinationKeyBinding(String translationKey, int code, String category, Type type) {
        super(translationKey, code, category, type);
    }

    public abstract Set<InputUtil.Key> possibleInitialKeys();

    @Override
    public boolean canConflictWith(KeyBinding other) {
        return super.canConflictWith(other) && (other instanceof CombinationKeyBinding combination && CollectionUtil.shareOneOrMoreElements(possibleInitialKeys(), combination.possibleInitialKeys()));
    }
}
