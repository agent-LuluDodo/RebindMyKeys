package de.luludodo.rebindmykeys.old.bindings.key;

import de.luludodo.rebindmykeys.old.bindings.Binding;
import de.luludodo.rebindmykeys.util.Modifier;
import de.luludodo.rebindmykeys.util.InputUtil2;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Set;

public abstract class KeyBinding implements Binding {
    protected boolean active = false;
    private final String id;
    private net.minecraft.client.util.InputUtil.Key boundKey;
    private final net.minecraft.client.util.InputUtil.Key defaultKey;
    private boolean allowRepeat = false;
    public KeyBinding(String id, net.minecraft.client.util.InputUtil.Key key) {
        this.id = id;
        boundKey = key;
        defaultKey = key;
        InputUtil2.registerListeners(id, this::onPressed, (k, m) -> {
            if (allowRepeat)
                onPressed(k, m);
        }, this::onReleased);
    }

    public void setAllowRepeat(boolean allowRepeat) {
       this.allowRepeat = allowRepeat;
    }
    public boolean getAllowRepeat() {
        return allowRepeat;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Text getTranslation() {
        return Text.translatable(id);
    }

    public abstract void onPressed(net.minecraft.client.util.InputUtil.Key key, List<Modifier> modifiers);
    public abstract void onReleased(net.minecraft.client.util.InputUtil.Key key, List<Modifier> modifiers);

    public net.minecraft.client.util.InputUtil.Key getBoundKey() {
        return boundKey;
    }
    public void setBoundKey(net.minecraft.client.util.InputUtil.Key key) {
        active = false;
        boundKey = key;
    }
    public boolean equalsBoundKey(net.minecraft.client.util.InputUtil.Key key) {
        return boundKey.equals(key);
    }
    @Override
    public Text getValueTranslation() {
        return boundKey.getLocalizedText();
    }
    @Override
    public Text getKeyTranslation() {
        return boundKey.getLocalizedText();
    }
    @Override
    public Set<net.minecraft.client.util.InputUtil.Key> getKeys() {
        return Set.of(boundKey);
    }

    public void reset() {
        boundKey = defaultKey;
    }

    @Override
    public void close() {
        InputUtil2.unregisterListeners(id);
    }
}
