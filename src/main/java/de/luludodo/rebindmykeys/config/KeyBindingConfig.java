package de.luludodo.rebindmykeys.config;

import de.luludodo.rebindmykeys.api.config.JsonMapConfig;
import de.luludodo.rebindmykeys.config.serializer.KeyBindingConfigSerializer;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.profiles.ProfileManager;
import de.luludodo.rebindmykeys.util.InitialKeyBindings;

import java.util.*;
import java.util.function.BiConsumer;

public class KeyBindingConfig extends JsonMapConfig<String, KeyBinding> {
    public static KeyBindingConfig getCurrent() {
        return ProfileManager.getCurrentProfile().getConfig();
    }

    public KeyBindingConfig(String uuid) {
        super(ProfileManager.getProfilesDirRelative().resolve(uuid).resolve("bindings").toString(), 1, new KeyBindingConfigSerializer());
    }
    private KeyBindingConfig(String uuid, Map<String, KeyBinding> content) {
        super(ProfileManager.getProfilesDirRelative().resolve(uuid).resolve("bindings").toString(), 1, new KeyBindingConfigSerializer(), false);
        this.content = new HashMap<>(content);
    }

    @Override
    protected Map<String, KeyBinding> getDefaults() {
        return InitialKeyBindings.getDefaults();
    }

    /**
     * <b>Use {@link KeyBinding#get(String)} instead of this!</b>
     */
    @Override
    public KeyBinding get(String id) {
        KeyBinding keyBinding = super.get(id);
        return keyBinding instanceof KeyBindingConfigSerializer.FakeKeyBinding? null : keyBinding;
    }

    @Override
    public boolean contains(String id) {
        return get(id) != null;
    }

    private Set<String> cachedOptions = null;
    @Override
    public Set<String> options() {
        if (cachedOptions == null) {
            cachedOptions = new HashSet<>();
            forEach((key, value) -> cachedOptions.add(key));
        }
        return cachedOptions;
    }

    private Set<KeyBinding> cachedBindings = null;

    /**
     * <b>Use {@link KeyBinding#getAll()} instead of this!</b>
     */
    public Set<KeyBinding> getAll() {
        if (cachedBindings == null) {
            cachedBindings = new HashSet<>();
            forEach((key, value) -> cachedBindings.add(value));
        }
        return cachedBindings;
    }

    @Override
    public boolean reload() {
        boolean result = super.reload();
        cachedOptions = null;
        cachedBindings = null;
        return result;
    }

    @Override
    public void forEach(BiConsumer<String, KeyBinding> action) {
        super.forEach((key, value) -> {
            if (value instanceof KeyBindingConfigSerializer.FakeKeyBinding) return;
            action.accept(key, value);
        });
    }

    public KeyBindingConfig duplicate(String name) {
        return new KeyBindingConfig(name, content);
    }
}
