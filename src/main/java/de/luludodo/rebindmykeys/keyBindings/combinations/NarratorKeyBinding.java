package de.luludodo.rebindmykeys.keyBindings.combinations;

import de.luludodo.rebindmykeys.keyBindings.CombinationKeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.HashSet;

public class NarratorKeyBinding extends CombinationKeyBinding {
    public NarratorKeyBinding(String translationKey, int code, String category) {
        super(translationKey, code, category);
    }

    @Override
    public HashSet<InputUtil.Key> combinationKeys() {
        HashSet<InputUtil.Key> set = new HashSet<>(1);
        set.add(InputUtil.Type.KEYSYM.createFromCode(InputUtil.GLFW_KEY_LEFT_CONTROL));
        return set;
    }
}
