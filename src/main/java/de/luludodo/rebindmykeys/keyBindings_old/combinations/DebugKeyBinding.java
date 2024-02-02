package de.luludodo.rebindmykeys.keyBindings_old.combinations;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keyBindings_old.CombinationKeyBinding;
import de.luludodo.rebindmykeys.keyBindings_old.Type;
import net.minecraft.client.util.InputUtil;

import java.util.Set;

public class DebugKeyBinding extends CombinationKeyBinding {
    public DebugKeyBinding(String translationKey, InputUtil.Key key, String category, Type type) {
        super(translationKey, key, category, type);
    }
    public DebugKeyBinding(String translationKey, int code, String category, Type type) {
        super(translationKey, code, category, type);
    }

    @Override
    public Set<InputUtil.Key> possibleInitialKeys() {
        return Set.of(RebindMyKeys.debugKey.boundKey);
    }
}
