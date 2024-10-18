package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes;

import de.luludodo.rebindmykeys.gui.widget.ConfigWidget;
import de.luludodo.rebindmykeys.keybindings.registry.LuluRegistries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public abstract class OperationModeEditor<O extends OperationMode> {
    private O mode;
    public void setCurrent(O mode, boolean copy) {
        if (copy) {
            this.mode = create(mode.getId());
            this.mode.load(mode.save());
        } else {
            this.mode = mode;
        }
    }

    @SuppressWarnings("unchecked")
    public void setCurrentAndCast(OperationMode mode, boolean copy) {
        setCurrent((O) mode, copy);

        entries = new ArrayList<>();
        initConfigEntries();
    }

    @SuppressWarnings("unchecked")
    private O create(Identifier id) {
        return (O) LuluRegistries.OPERATION_MODE.constructOptional(id).orElseThrow();
    }

    private List<ConfigWidget.Entry> entries;
    protected void addEntry(ConfigWidget.Entry entry) {
        entries.add(entry);
    }
    public List<ConfigWidget.Entry> getConfigEntries() {
        return entries;
    }

    private boolean dirty;
    public boolean hasChanges() {
        return dirty;
    }
    public void markDirty() {
        dirty = true;
    }
    public void onChangesApplied() {
        dirty = false;
    }

    protected abstract void initConfigEntries();

    /**
     * If the {@link OperationMode#getEditor()} method of an {@link OperationMode} returns {@code this}.
     * It's guaranteed that the {@link Identifier} returned from the same {@link OperationMode}'s
     * {@link OperationMode#getId()} method is included in the {@link Collection} returned by this method.
     * @return A list of the {@link Identifier}s of compatible {@link OperationMode}s
     */
    public abstract Collection<Identifier> compatibleOperationModes();

    public O mode() {
        return mode;
    }
}
