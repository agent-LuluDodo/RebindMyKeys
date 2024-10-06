package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.pressCount;

import de.luludodo.rebindmykeys.config.KeyBindingConfig;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationModeEditor;
import net.minecraft.text.Text;

import static de.luludodo.rebindmykeys.gui.widget.ConfigWidget.*;

public abstract class PressCountOperationModeEditor<O extends PressCountOperationMode> extends OperationModeEditor<O> {
    @Override
    protected void initConfigEntries() {
        addEntry(new NumberEntry<>(
                Text.translatable("rebindmykeys.gui.settings.pressCount"),
                mode().getPressCount(),
                value -> value == 1 ? Text.translatable("rebindmykeys.gui.settings.click") : Text.translatable("rebindmykeys.gui.settings.clicks"),
                Integer::valueOf,
                newValue -> newValue > 0,
                newValue -> {
                    mode().setPressCount(newValue);
                    KeyBindingConfig.getCurrent().save();
                }
        ));
    }
}
