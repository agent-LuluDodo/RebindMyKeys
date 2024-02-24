package de.luludodo.rebindmykeys.old.bindings.multi;

import de.luludodo.rebindmykeys.old.bindings.Binding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultiBinding implements Binding {
    private final String id;
    private Binding base;
    private final Binding defaultBase;
    private Binding[] extras;
    private final Binding[] defaultExtras;
    public MultiBinding(String id, Binding base, Binding[] extras) {
        this.id = id;
        this.base = base;
        defaultBase = base;
        this.extras = extras;
        defaultExtras = extras;
    }

    @Override
    public boolean isActive() {
        for (Binding extra : extras) {
            if (!extra.isActive())
                return false;
        }
        return base.isActive();
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
        MutableText text = Text.empty();
        for (Binding extra : extras) {
            text.append(extra.getValueTranslation()).append(" + ");
        }
        return text.append(base.getValueTranslation());
    }

    @Override
    public Text getKeyTranslation() {
        MutableText text = Text.empty();
        for (Binding extra : extras) {
            text.append(extra.getKeyTranslation()).append(" + ");
        }
        return text.append(base.getKeyTranslation());
    }

    @Override
    public Set<InputUtil.Key> getKeys() {
        Set<InputUtil.Key> keys = base.getKeys();
        for (Binding extra : extras) {
            keys.addAll(extra.getKeys());
        }
        return keys;
    }

    @Override
    public void close() throws Exception {
        base.close();
        for (Binding extra : extras) {
            extra.close();
        }
    }

    @Override
    public void reset() {
        defaultBase.reset();
        for (Binding defaultExtra : defaultExtras) {
            defaultExtra.reset();
        }
        base = defaultBase;
        extras = defaultExtras;
    }
}
