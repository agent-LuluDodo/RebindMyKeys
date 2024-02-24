package de.luludodo.rebindmykeys.old3.binding.wrapper;

import de.luludodo.rebindmykeys.old3.binding.mode.Mode;
import de.luludodo.rebindmykeys.old3.binding.validator.Validator;
import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;

import java.util.List;

public abstract class BasicCondition<M extends Mode> implements Condition<M> {
    private Validator validator;
    @Override
    public void init(Validator validator) {
        this.validator = validator;
    }
    @Override
    public boolean valid(InputUtil.Key key, List<Modifier> modifiers) {
        return validator.valid(key, modifiers);
    }

    @Override
    public void onKeyDown(InputUtil.Key key, List<Modifier> modifiers) {
        if (valid(key, modifiers))
            onValidKeyDown();
    }
    public void onKeyUp(InputUtil.Key key, List<Modifier> modifiers) {
        if (valid(key, modifiers))
            onValidKeyUp();
    }
    protected abstract void onValidKeyDown();
    protected abstract void onValidKeyUp();

    private M mode;
    @Override
    public void setMode(M mode) {
        this.mode = mode;
    }
    @Override
    public M getMode() {
        return mode;
    }
}
