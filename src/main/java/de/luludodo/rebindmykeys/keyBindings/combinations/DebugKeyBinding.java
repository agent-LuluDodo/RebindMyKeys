package de.luludodo.rebindmykeys.keyBindings.combinations;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keyBindings.CombinationKeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.HashSet;

public class DebugKeyBinding extends CombinationKeyBinding {
    public DebugKeyBinding(String translationKey, int code, String category) {
        super(translationKey, code, category);
    }

    @Override
    public HashSet<InputUtil.Key> combinationKeys() {
        HashSet<InputUtil.Key> set = new HashSet<>(1);
        set.add(RebindMyKeys.debugKey.boundKey);
        return set;
    }
}
