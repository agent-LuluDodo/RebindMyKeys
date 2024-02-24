package de.luludodo.rebindmykeys.old.bindings.modifier;

import de.luludodo.rebindmykeys.old.bindings.Binding;
import de.luludodo.rebindmykeys.old.bindings.BindingManager;
import de.luludodo.rebindmykeys.old.bindings.key.KeyBinding;
import de.luludodo.rebindmykeys.util.Modifier;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import de.luludodo.rebindmykeys.util.InputUtil2;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModifierBinding implements Binding {
    private final KeyBinding keyBinding;
    private List<Modifier> modifiers;
    private final List<Modifier> defaultModifiers;
    private boolean matchExact = false;
    public ModifierBinding(KeyBinding keyBinding, List<Modifier> modifiers) {
        this.keyBinding = keyBinding;
        this.modifiers = modifiers;
        defaultModifiers = modifiers;
        InputUtil2.unregisterListeners(keyBinding.getId());
        InputUtil2.registerListeners(keyBinding.getId(), this::onPressed, (k, m) -> {
            if (keyBinding.getAllowRepeat())
                onPressed(k, m);
        }, this::onReleased);
        BindingManager.markForUpdate(keyBinding.getId());
        BindingManager.register(keyBinding.getId(), this);
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    public void setMatchExact(boolean matchExact) {
        this.matchExact = matchExact;
    }
    public boolean getMatchExact() {
        return matchExact;
    }

    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
    }
    public void removeModifier(Modifier modifier) {
        modifiers.remove(modifier);
    }
    public void setModifiers(List<Modifier> modifiers) {
        this.modifiers = modifiers;
    }
    public List<Modifier> getModifiers() {
        return Collections.unmodifiableList(modifiers);
    }

    public void onPressed(net.minecraft.client.util.InputUtil.Key key, List<Modifier> modifiers) {
        if (matchModifiers(modifiers))
            keyBinding.onPressed(key, modifiers);
    }
    public void onReleased(net.minecraft.client.util.InputUtil.Key key, List<Modifier> modifiers) {
        if (matchModifiers(modifiers))
            keyBinding.onReleased(key, modifiers);
    }

    public boolean matchModifiers(List<Modifier> modifiers) {
        if (matchExact) {
            return CollectionUtil.equalsIgnoreOrder(modifiers, this.modifiers);
        } else {
            return CollectionUtil.containsAllOf(modifiers, this.modifiers);
        }
    }

    @Override
    public boolean isActive() {
        return keyBinding.isActive();
    }

    @Override
    public String getId() {
        return keyBinding.getId();
    }

    @Override
    public Text getTranslation() {
        return keyBinding.getTranslation();
    }

    @Override
    public Text getValueTranslation() {
        MutableText translation = Text.empty();
        modifiers.forEach(modifier -> translation.append(modifier.getTranslation()).append(" + "));
        return translation.append(keyBinding.getValueTranslation());
    }

    @Override
    public Text getKeyTranslation() {
        MutableText translation = Text.empty();
        modifiers.forEach(modifier -> translation.append(modifier.getTranslation()).append(" + "));
        return translation.append(keyBinding.getValueTranslation());
    }

    @Override
    public void close() {
        keyBinding.close();
    }
    @Override
    public Set<net.minecraft.client.util.InputUtil.Key> getKeys() {
        Set<net.minecraft.client.util.InputUtil.Key> keys = new HashSet<>();
        modifiers.forEach(modifier -> keys.addAll(modifier.getKeys()));
        keys.addAll(keyBinding.getKeys());
        return keys;
    }

    @Override
    public void reset() {
        modifiers = defaultModifiers;
    }
}
