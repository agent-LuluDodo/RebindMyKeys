package de.luludodo.rebindmykeys.config;

import de.luludodo.rebindmykeys.api.config.JsonMapConfig;
import de.luludodo.rebindmykeys.config.serializer.KeyBindingConfigSerializer;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.profiles.ProfileManager;
import de.luludodo.rebindmykeys.util.InitialKeyBindings;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class KeyBindingConfig extends JsonMapConfig<String, KeyBinding> {
    public KeyBindingConfig(String name) {
        super(ProfileManager.getProfilesDir().resolve(name).resolve("bindings").toString(), new KeyBindingConfigSerializer());
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

    @Override
    public Set<String> options() {
        Set<String> options = new HashSet<>();
        forEach((key, value) -> options.add(key));
        return options;
    }

    @Override
    public void forEach(BiConsumer<String, KeyBinding> action) {
        super.forEach((key, value) -> {
            if (value instanceof KeyBindingConfigSerializer.FakeKeyBinding) return;
            action.accept(key, value);
        });
    }
}
