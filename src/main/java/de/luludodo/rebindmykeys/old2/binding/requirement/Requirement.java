package de.luludodo.rebindmykeys.old2.binding.requirement;

import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.List;

public interface Requirement {
    void next();
    boolean get();

    void onKeyDown(InputUtil.Key key, List<Modifier> modifiers);
    void onKeyUp(InputUtil.Key key, List<Modifier> modifiers);

    void onCurrentStateChange(boolean currentState);

    Text getTranslation();
}
