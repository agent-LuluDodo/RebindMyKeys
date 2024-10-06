package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action;

import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.pressCount.PressCountOperationModeEditor;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;

import static de.luludodo.rebindmykeys.gui.widget.ConfigWidget.*;

public class ActionModeEditor extends PressCountOperationModeEditor<ActionMode> {
    @Override
    protected void initConfigEntries() {
        super.initConfigEntries();
        addEntry(new CyclingButtonEntry<>(
                Text.translatable("rebindmykeys.gui.settings.activateOn"),
                value -> switch (value) {
                    case PRESS -> Text.translatable("rebindmykeys.activateOn.press");
                    case RELEASE -> Text.translatable("rebindmykeys.activateOn.release");
                    case BOTH -> Text.translatable("rebindmykeys.activateOn.both");
                    case UNKNOWN -> Text.translatable("rebindmykeys.activateOn.unknown");
                },
                mode().getActivateOn(),
                (button, value) -> mode().setActivateOn(value),
                ActivateOn.values()
        ));
    }

    @Override
    public Collection<Identifier> compatibleOperationModes() {
        return List.of(ActionMode.ID);
    }
}
