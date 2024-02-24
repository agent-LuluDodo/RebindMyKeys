package de.luludodo.rebindmykeys.old2.binding.requirement.toggle.key;

import de.luludodo.rebindmykeys.util.Modifier;
import de.luludodo.rebindmykeys.old2.binding.requirement.toggle.ToggleMode;
import de.luludodo.rebindmykeys.old2.binding.requirement.toggle.ToggleRequirement;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.List;

public class KeyToggleRequirement extends ToggleRequirement {
    private InputUtil.Key key;
    public KeyToggleRequirement(InputUtil.Key key, ToggleMode mode) {
        super(mode);
        this.key = key;
    }

    public InputUtil.Key getKey() {
        return key;
    }
    public void setKey(InputUtil.Key key) {
        this.key = key;
    }

    @Override
    protected boolean valid(InputUtil.Key key, List<Modifier> modifiers) {
        return key.equals(this.key);
    }

    @Override
    public Text getTranslation() {
        return key.getLocalizedText();
    }
}
