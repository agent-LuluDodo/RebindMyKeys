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
import de.luludodo.rebindmykeys.util.KeyBindingUtil;
import de.luludodo.rebindmykeys.util.interfaces.JsonLoadable;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import net.minecraft.client.util.InputUtil;

import java.util.*;

@SuppressWarnings("unused")
public class KeyBinding implements JsonSavable, JsonLoadable {
    private static boolean checkInitialActive() {
        boolean initialActive = InitialKeyBindings.isActive();
        boolean configLoaded = ProfileManager.getCurrentProfile().getConfig().isLoaded();
        if (!initialActive && !configLoaded) {
            throw new IllegalStateException("Neither InitialKeyBindings nor KeyBindingConfig is active");
        }
        if (initialActive && configLoaded) {
            RebindMyKeys.LOG.error("Both InitialKeyBindings and KeyBindingConfig are active -> disabling InitialKeyBindings");
            InitialKeyBindings.disable();
            initialActive = false;
        }
        return initialActive;
    }

    /**
     * Gets the KeyBinding with the specified id.
     * @param id The KeyBinding or {@code null} if no KeyBinding with the id was found.
     * @return The KeyBinding with the specified id
     * @throws IllegalStateException If neither {@link InitialKeyBindings#isActive()} nor {@link KeyBindingConfig#isLoaded()} return {@code true}
     * @see InitialKeyBindings#get(String)
     * @see KeyBindingConfig#get(String)
     */
    public static KeyBinding get(String id) {
        KeyBinding binding;
        if (checkInitialActive()) {
            binding = InitialKeyBindings.get(id);
        } else {
            binding = ProfileManager.getCurrentProfile().getConfig().get(id);
        }
        return binding;
    }

    /**
     * Gets all KeyBindings.
     * @return A {@link Collection} containing all KeyBindings
     * @throws IllegalStateException If neither {@link InitialKeyBindings#isActive()} nor {@link KeyBindingConfig#isLoaded()} return {@code true}
     * @see InitialKeyBindings#getAll()
     * @see KeyBindingConfig#getAll()
     */
    public static Collection<KeyBinding> getAll() {
        Collection<KeyBinding> bindings;
        if (checkInitialActive()) {
            bindings = InitialKeyBindings.getAll();
        } else {
            bindings = ProfileManager.getCurrentProfile().getConfig().getAll();
        }
        return bindings;
    }

    private final String id;
    private final List<KeyCombo> defaultKeyCombos;
    private List<KeyCombo> keyCombos;
    private final ComboSettings defaultSettings;
    private final boolean isAction;
    public KeyBinding(String id, List<KeyCombo> keyCombos, ComboSettings defaultSettings) {
        this.id = id;
        defaultKeyCombos = CollectionUtil.copy(keyCombos, ArrayList::new, KeyCombo::copy);
        this.keyCombos = CollectionUtil.copy(keyCombos, ArrayList::new, KeyCombo::copy);
        this.defaultSettings = defaultSettings;
        isAction = defaultSettings.operationMode() instanceof ActionMode;
    }

    public boolean isAction() {
        return isAction;
    }

    public boolean wasTriggered() {
        return CollectionUtil.any(keyCombos, KeyCombo::wasTriggered);
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

    public void reset() {
        keyCombos = CollectionUtil.copy(defaultKeyCombos, ArrayList::new, KeyCombo::copy);
    }

    public void isDefault() {
        // TODO: implement
    }

    protected void setKeyCombos(List<KeyCombo> keyCombos) {
        this.keyCombos = keyCombos;
    }

    public void addKeyCombo(KeyCombo keyCombo) {
        keyCombos.add(keyCombo);
        KeyBindingUtil.calcIncompatibleUUIDs();
    }

    public void removeKeyCombo(KeyCombo keyCombo) {
        keyCombos.remove(keyCombo);
        KeyBindingUtil.calcIncompatibleUUIDs();
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
        return CollectionUtil.any(keyCombos, KeyCombo::isPressed);
    }

    public boolean isActive() {
        return CollectionUtil.any(keyCombos, KeyCombo::isActive);
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
        keyCombos = loader.array("keyCombos").toList(comboJson -> KeyCombo.load(comboJson, getId()));
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
        return CollectionUtil.any(keyCombos, KeyCombo::checkContext);
    }

    public interface KeyBindingCopyConstructor<T> {
        T create(String id, List<KeyCombo> defaultKeyCombos, ComboSettings defaultSettings);
    }

    protected <T extends KeyBinding> T copy(KeyBindingCopyConstructor<T> constructor) {
        List<KeyCombo> defaultKeyCombosCopy = new ArrayList<>(defaultKeyCombos.size());
        for (KeyCombo combo : defaultKeyCombos) {
            defaultKeyCombosCopy.add(combo.copy());
        }
        List<KeyCombo> keyCombosCopy = new ArrayList<>(keyCombos.size());
        for (KeyCombo combo : keyCombos) {
            keyCombosCopy.add(combo.copy());
        }
        T copy = constructor.create(id, defaultKeyCombosCopy, defaultSettings);
        copy.setKeyCombos(keyCombosCopy);
        return copy;
    }

    public KeyBinding copy() {
        return copy(KeyBinding::new);
    }
}
