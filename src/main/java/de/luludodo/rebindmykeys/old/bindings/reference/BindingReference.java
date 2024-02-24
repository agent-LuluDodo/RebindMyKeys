package de.luludodo.rebindmykeys.old.bindings.reference;

import de.luludodo.rebindmykeys.old.bindings.Binding;
import de.luludodo.rebindmykeys.old.bindings.BindingManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

public class BindingReference implements Binding {
    private final String id;
    private String targetId;
    private final String defaultTargetId;
    public BindingReference(String id, String targetId) {
        this.id = id;
        this.targetId = targetId;
        defaultTargetId = targetId;
        BindingManager.register(id, this);
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetId() {
        return targetId;
    }

    @Override
    public boolean isActive() {
        return BindingManager.get(targetId).isActive();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Text getTranslation() {
        return Text.translatable(id);
    }

    @Override
    public Text getValueTranslation() {
        return BindingManager.get(targetId).getTranslation();
    }

    @Override
    public Text getKeyTranslation() {
        return BindingManager.get(targetId).getKeyTranslation();
    }

    @Override
    public Set<InputUtil.Key> getKeys() {
        return BindingManager.get(targetId).getKeys();
    }

    @Override
    public void reset() {
        targetId = defaultTargetId;
    }
}
