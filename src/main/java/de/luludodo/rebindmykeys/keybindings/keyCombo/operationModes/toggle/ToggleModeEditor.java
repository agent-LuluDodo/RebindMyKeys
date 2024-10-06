package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.toggle;

import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold.HoldMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.pressCount.PressCountOperationModeEditor;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;

import static de.luludodo.rebindmykeys.gui.widget.ConfigWidget.OnOffButtonEntry;

public class ToggleModeEditor extends PressCountOperationModeEditor<ToggleMode> {
    @Override
    protected void initConfigEntries() {
        super.initConfigEntries();
        addEntry(new OnOffButtonEntry(
                Text.translatable("rebindmykeys.gui.settings.initialState"),
                Text.translatable("rebindmykeys.gui.settings.initialState.on"),
                Text.translatable("rebindmykeys.gui.settings.initialState.off"),
                mode().getInitialState(),
                (button, value) -> mode().setInitialState(value)
        ));
        addEntry(new OnOffButtonEntry(
                Text.translatable("rebindmykeys.gui.settings.toggleOnPress"),
                Text.translatable("rebindmykeys.gui.settings.toggleOnPress.on"),
                Text.translatable("rebindmykeys.gui.settings.toggleOnPress.off"),
                mode().getToggleOn(),
                (button, value) -> mode().setToggleOn(value)
        ));
    }

    @Override
    public Collection<Identifier> compatibleOperationModes() {
        return List.of(ToggleMode.ID, HoldMode.ID);
    }
}
