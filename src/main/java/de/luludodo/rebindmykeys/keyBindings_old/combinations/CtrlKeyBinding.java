package de.luludodo.rebindmykeys.keyBindings_old.combinations;

import de.luludodo.rebindmykeys.keyBindings_old.CombinationKeyBinding;
import de.luludodo.rebindmykeys.keyBindings_old.Type;
import net.minecraft.client.util.InputUtil;

import java.util.Set;

import static de.luludodo.rebindmykeys.util.KeyUtil.*;

public class CtrlKeyBinding extends CombinationKeyBinding {

    public CtrlKeyBinding(String translationKey, InputUtil.Key key, String category, Type type) {
        super(translationKey, key, category, type);
    }
    public CtrlKeyBinding(String translationKey, int code, String category, Type type) {
        super(translationKey, code, category, type);
    }

    @Override
    public Set<InputUtil.Key> possibleInitialKeys() {
        return Set.of(keysm(InputUtil.GLFW_KEY_LEFT_CONTROL), keysm(InputUtil.GLFW_KEY_RIGHT_CONTROL));
    }
}
