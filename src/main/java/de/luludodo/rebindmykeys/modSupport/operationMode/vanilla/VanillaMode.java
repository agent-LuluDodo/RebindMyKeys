package de.luludodo.rebindmykeys.modSupport.operationMode.vanilla;

import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold.HoldMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold.HoldModeEditor;
import net.minecraft.util.Identifier;

public class VanillaMode extends HoldMode {
    public static final Identifier ID = new Identifier("rebindmykeys", "vanilla");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public HoldModeEditor getEditor() {
        return new VanillaModeEditor();
    }

    @Override
    public Identifier getIcon() {
        return new Identifier("rebindmykeys", "textures/gui/vanilla.png");
    }

    @Override
    public String getTranslation() {
        return "rebindmykeys.operationMode.vanilla";
    }
}
