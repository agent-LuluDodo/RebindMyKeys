package de.luludodo.rebindmykeys.old2.binding.requirement.toggle.modifier;

import de.luludodo.rebindmykeys.util.Modifier;
import de.luludodo.rebindmykeys.old2.binding.requirement.toggle.ToggleMode;
import de.luludodo.rebindmykeys.old2.binding.requirement.toggle.ToggleRequirement;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.List;

public class ModifierToggleRequirement extends ToggleRequirement {
    private Modifier modifier;
    public ModifierToggleRequirement(Modifier modifier, ToggleMode mode) {
        super(mode);
        this.modifier = modifier;
    }

    public Modifier getModifier() {
        return modifier;
    }
    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    @Override
    protected boolean valid(InputUtil.Key key, List<Modifier> modifiers) {
        return modifiers.contains(modifier);
    }

    @Override
    public Text getTranslation() {
        return modifier.getTranslation();
    }
}
