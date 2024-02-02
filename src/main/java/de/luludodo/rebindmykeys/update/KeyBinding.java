package de.luludodo.rebindmykeys.update;

import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public abstract class KeyBinding implements Binding {
    protected boolean active = false;
    private final String id;
    private InputUtil.Key boundKey;
    private final InputUtil.Key defaultKey;

    public KeyBinding(String id, InputUtil.Key key) {
        this.id = id;
        boundKey = key;
        defaultKey = key;
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

    public abstract void onPressed(InputUtil.Key key);

    public abstract void onReleased(InputUtil.Key key);

    public InputUtil.Key getBoundKey() {
        return boundKey;
    }

    public void setBoundKey(InputUtil.Key key) {
        active = false;
        boundKey = key;
    }

    public boolean equalsBoundKey(InputUtil.Key key) {
        return boundKey.equals(key);
    }

    public void reset() {
        boundKey = defaultKey;
    }
}
