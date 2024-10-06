package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold;

import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.pressCount.PressCountOperationModeEditor;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.toggle.ToggleMode;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;

import static de.luludodo.rebindmykeys.gui.widget.ConfigWidget.*;

public class HoldModeEditor extends PressCountOperationModeEditor<HoldMode> {
    @Override
    protected void initConfigEntries() {
        super.initConfigEntries();
        addEntry(new OnOffButtonEntry(
                Text.translatable("rebindmykeys.gui.settings.inverted"),
                Text.translatable("rebindmykeys.gui.settings.inverted.on"),
                Text.translatable("rebindmykeys.gui.settings.inverted.off"),
                mode().getInverted(),
                (button, value) -> mode().setInverted(value)
        ));
    }

    @Override
    public Collection<Identifier> compatibleOperationModes() {
        return List.of(ToggleMode.ID, HoldMode.ID);
    }
}
