package de.luludodo.rebindmykeys.old2.binding.requirement.reference;

import de.luludodo.rebindmykeys.util.Modifier;
import de.luludodo.rebindmykeys.old2.binding.requirement.Requirement;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.List;

public class ReferenceRequirement implements Requirement {
    @Override
    public void next() {}

    @Override
    public boolean get() {
        return false;
    }

    @Override
    public void onKeyDown(InputUtil.Key key, List<Modifier> modifiers) {}

    @Override
    public void onKeyUp(InputUtil.Key key, List<Modifier> modifiers) {}

    @Override
    public void onCurrentStateChange(boolean currentState) {}

    @Override
    public Text getTranslation() {
        return null;
    }
}
