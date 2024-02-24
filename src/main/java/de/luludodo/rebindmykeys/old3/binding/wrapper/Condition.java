package de.luludodo.rebindmykeys.old3.binding.wrapper;

import de.luludodo.rebindmykeys.old3.binding.mode.Mode;
import de.luludodo.rebindmykeys.old3.binding.validator.Validator;
import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;

import java.util.List;

public interface Condition<M extends Mode> extends Validator {
    void init(Validator validator);
    void next();
    boolean get();
    void onKeyDown(InputUtil.Key key, List<Modifier> modifiers);
    void onKeyUp(InputUtil.Key key, List<Modifier> modifiers);
    void setMode(M m);
    M getMode();
}
