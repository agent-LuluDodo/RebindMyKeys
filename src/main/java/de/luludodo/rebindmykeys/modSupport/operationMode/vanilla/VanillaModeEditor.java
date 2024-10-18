package de.luludodo.rebindmykeys.modSupport.operationMode.vanilla;

import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold.HoldModeEditor;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;

public class VanillaModeEditor extends HoldModeEditor {
    @Override
    protected void initConfigEntries() {}

    @Override
    public Collection<Identifier> compatibleOperationModes() {
        return List.of(VanillaMode.ID);
    }
}
