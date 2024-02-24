package de.luludodo.rebindmykeys.potential.binding.button;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;

import java.util.List;

public class ModifierButton extends BasicButton {
    public static ModifierButton fromJson(JsonElement json) {
        return new ModifierButton(
                JsonUtil.requireEnum(json, Modifier.class)
        );
    }

    private Modifier modifier;
    public ModifierButton(Modifier modifier) {
        this.modifier = modifier;
    }

    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }
    public Modifier getModifier() {
        return modifier;
    }

    @Override
    public boolean validate(InputUtil.Key key, List<Modifier> modifiers) {
        return modifiers.contains(modifier);
    }
}
