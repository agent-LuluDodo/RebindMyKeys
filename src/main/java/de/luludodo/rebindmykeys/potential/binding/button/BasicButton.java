package de.luludodo.rebindmykeys.potential.binding.button;

import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;

import java.util.List;

public abstract class BasicButton implements Button {
    private boolean pressed = false;
    public void press() {
        pressed = true;
    }
    public void release() {
        pressed = false;
    }

    public abstract boolean validate(InputUtil.Key key, List<Modifier> modifiers);

    @Override
    public void onKeyDown(InputUtil.Key key, List<Modifier> modifiers) {
        if (validate(key, modifiers))
            press();
    }

    @Override
    public void onKeyUp(InputUtil.Key key, List<Modifier> modifiers) {
        if (validate(key, modifiers))
            release();
    }

    @Override
    public boolean isPressed() {
        return pressed;
    }
}
