package de.luludodo.rebindmykeys.keybindings;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.config.KeyBindingConfig;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActionMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.ComboSettings;
import de.luludodo.rebindmykeys.profiles.ProfileManager;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import de.luludodo.rebindmykeys.util.InitialKeyBindings;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.JsonLoadable;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class KeyBinding implements JsonSavable, JsonLoadable {
    private final String id;
    private final ModContainer mod;
    private final List<KeyCombo> defaultKeyCombos;
    private List<KeyCombo> keyCombos;
    private final ComboSettings defaultSettings;
    private final boolean isAction;
    public KeyBinding(String id, ModContainer mod, List<KeyCombo> keyCombos, ComboSettings defaultSettings) {
        this.id = id;
        this.mod = mod;
        defaultKeyCombos = keyCombos;
        this.keyCombos = keyCombos;
        this.defaultSettings = defaultSettings;
        isAction = defaultSettings.operationMode() instanceof ActionMode;
    }

    public boolean isAction() {
        return isAction;
    }

    public boolean wasTriggered() {
        return CollectionUtil.oneCondition(keyCombos, KeyCombo::wasTriggered);
    }

    public boolean isToggled() {
        if (isAction) throw new IllegalArgumentException("KeyBinding is an Action");
        return isActive();
    }

    public void done() {
        keyCombos.forEach(KeyCombo::done);
    }

    public String getId() {
        return id;
    }

    public ModContainer getMod() {
        return mod;
    }

    public String getModName() {
        return mod == null ? "Minecraft" : mod.getMetadata().getName();
    }

    public void reset() {
        keyCombos = defaultKeyCombos;
    }

    public void addKeyCombo(KeyCombo keyCombo) {
        keyCombos.add(keyCombo);
    }

    public void removeKeyCombo(KeyCombo keyCombo) {
        keyCombos.remove(keyCombo);
    }

    public ComboSettings getDefaultSettings() {
        return defaultSettings;
    }

    public List<KeyCombo> getKeyCombos() {
        return Collections.unmodifiableList(keyCombos);
    }

    public void calcIncompatibleUUIDs(Set<KeyCombo> keyCombos) {
        keyCombos.forEach(combo -> combo.calcIncompatibleUUIDs(keyCombos));
    }

    public Set<UUID> getIncompatibleUUIDs() {
        return CollectionUtil.joinCollection(HashSet::new, CollectionUtil.run(keyCombos, KeyCombo::getIncompatibleUUIDs));
    }

    public void filter(final Set<UUID> invalidUUIDs) {
        keyCombos.forEach(combo -> combo.filter(invalidUUIDs));
    }

    public boolean isPressed() {
        return CollectionUtil.oneCondition(keyCombos, KeyCombo::isPressed);
    }

    public boolean isActive() {
        return CollectionUtil.oneCondition(keyCombos, KeyCombo::isActive);
    }

    public void onKeyDown(InputUtil.Key key) {
        keyCombos.forEach(keyCombo -> keyCombo.onKeyDown(key));
    }
    public void onKeyUp(InputUtil.Key key) {
        keyCombos.forEach(keyCombo -> keyCombo.onKeyUp(key));
    }

    @Override
    public JsonElement save() {
        return JsonUtil.object()
                .add("keyCombos", keyCombos)
                .build();
    }

    @Override
    public void load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        keyCombos = loader.array("keyCombos").toList(KeyCombo::load);
    }

    /**
     * Gets the KeyBinding with the specified id.
     * @param id The id of the KeyBinding
     * @return The KeyBinding with the specified id
     * @throws IllegalStateException If neither {@link InitialKeyBindings#isActive()} nor {@link KeyBindingConfig#isLoaded()} return {@code true}
     * @throws IllegalArgumentException If no KeyBinding with the specified id exists.
     * @see InitialKeyBindings#get(String)
     * @see KeyBindingConfig#get(String)
     */
    public static @Nullable KeyBinding get(String id) {
        boolean initialActive = InitialKeyBindings.isActive();
        boolean configLoaded = ProfileManager.getCurrentProfile().getConfig().isLoaded();
        if (!initialActive && !configLoaded) {
            throw new IllegalStateException("Neither InitialKeyBindings nor KeyBindingConfig is active");
        }
        if (initialActive && configLoaded) {
            RebindMyKeys.LOG.error("Both InitialKeyBindings and KeyBindingConfig are active -> disabling InitialKeyBindings");
            InitialKeyBindings.disable();
        }
        KeyBinding binding;
        if (initialActive) {
            binding = InitialKeyBindings.get(id);
        } else {
            binding = ProfileManager.getCurrentProfile().getConfig().get(id);
        }
        return binding;
    }

    public void release() {
        keyCombos.forEach(KeyCombo::release);
    }

    public void updatePressed() {
        keyCombos.forEach(KeyCombo::updatePressed);
    }

    public void updateActive() {
        keyCombos.forEach(KeyCombo::updateActive);
    }

    public boolean checkContext() {
        return CollectionUtil.oneCondition(keyCombos, KeyCombo::checkContext);
    }
}
