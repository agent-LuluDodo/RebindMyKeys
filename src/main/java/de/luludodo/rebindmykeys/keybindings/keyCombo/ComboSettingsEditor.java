package de.luludodo.rebindmykeys.keybindings.keyCombo;

import de.luludodo.rebindmykeys.gui.widget.ConfigWidget;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationModeEditor;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationModeRegistry;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.ComboSettings;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.IContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComboSettingsEditor {
    private final KeyCombo combo;
    private List<OperationModeEditor<?>> operationModeEditors;
    private int currentOperationMode;
    private IContext[] context; // TODO: make this editable
    private boolean orderSensitive;
    private boolean skipFilter;
    private boolean hasChanges = false;
    public ComboSettingsEditor(KeyCombo combo) {
        this.combo = combo;
        reload();
    }

    public void reload() {
        context = combo.getSettings().context();
        orderSensitive = combo.getSettings().orderSensitive();
        skipFilter = combo.getSettings().skipFilter();

        OperationMode currentMode = combo.getSettings().operationMode();
        Identifier currentId = currentMode.getId();
        OperationModeEditor<?> currentEditor = currentMode.getEditor();

        Collection<Identifier> compatibleIdentifiers = currentEditor.compatibleOperationModes();
        operationModeEditors = new ArrayList<>(compatibleIdentifiers.size());
        for (Identifier id : compatibleIdentifiers) {
            OperationModeEditor<?> editor;
            if (id == currentId) {
                editor = currentEditor;
                editor.setCurrentAndCast(currentMode, true);
                currentOperationMode = operationModeEditors.size();
            } else {
                OperationMode mode = OperationModeRegistry.constructOptional(id).orElseThrow();
                editor = mode.getEditor();
                editor.setCurrentAndCast(mode, false);
            }
            operationModeEditors.add(editor);
        }
    }

    public Integer[] getOperationModeOptions() {
        Integer[] options = new Integer[operationModeEditors.size()];
        for (int i = 0; i < operationModeEditors.size(); i++) {
            options[i] = i;
        }
        return options;
    }
    public void setOperationMode(Integer i) {
        hasChanges = true;
        currentOperationMode = i;
    }
    public Text getOperationModeTranslation(Integer i) {
        return Text.translatable(operationModeEditors.get(i).mode().getTranslation());
    }
    public Collection<ConfigWidget.Entry> getOperationModeEntries() {
        return operationModeEditors.get(currentOperationMode).getConfigEntries();
    }
    public Integer getCurrentOperationMode() {
        return currentOperationMode;
    }
    public boolean getOperationModeActive() {
        return operationModeEditors.size() > 1;
    }

    public IContext[] getContext() {
        return context;
    }

    public void setOrderSensitive(boolean orderSensitive) {
        hasChanges = true;
        this.orderSensitive = orderSensitive;
    }
    public boolean getOrderSensitive() {
        return orderSensitive;
    }

    public void setSkipFilter(boolean skipFilter) {
        hasChanges = true;
        this.skipFilter = skipFilter;
    }
    public boolean getSkipFilter() {
        return skipFilter;
    }

    public void apply() {
        combo.setSettings(new ComboSettings(
                operationModeEditors.get(currentOperationMode).mode(),
                context,
                orderSensitive,
                skipFilter
        ));
        operationModeEditors.get(currentOperationMode).onChangesApplied();
        hasChanges = false;
    }

    public boolean hasChanges() {
        return hasChanges || operationModeEditors.get(currentOperationMode).hasChanges();
    }

    public void setNoChanges() {
        hasChanges = false;
        operationModeEditors.get(currentOperationMode).onChangesApplied();
    }
}
