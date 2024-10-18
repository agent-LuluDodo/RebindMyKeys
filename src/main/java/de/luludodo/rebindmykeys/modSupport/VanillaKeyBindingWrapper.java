package de.luludodo.rebindmykeys.modSupport;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.basic.BasicKey;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActionMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold.HoldMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.toggle.ToggleMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.ComboSettings;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.Context;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.FilterMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.IContext;
import de.luludodo.rebindmykeys.modSupport.operationMode.OriginalMode;
import de.luludodo.rebindmykeys.modSupport.operationMode.vanilla.VanillaMode;
import de.luludodo.rebindmykeys.util.InitialKeyBindings;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.KeyBindingUtil;
import de.luludodo.rebindmykeys.util.TickUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.util.InputUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class VanillaKeyBindingWrapper extends KeyBinding {
    private static final Map<String, TickUtil.ToggleQueue> idToToggleQueue = new HashMap<>();
    public static void wrapAndRegister(net.minecraft.client.option.KeyBinding register) {
        new VanillaKeyBindingWrapper(register);
        String id = register.getTranslationKey();
        KeyBindingUtil.onToggle(id, value -> {
            VanillaKeyBindingWrapper binding = ((VanillaKeyBindingWrapper) KeyBinding.get(id));
            OriginalMode mode = binding.getOriginalMode();
            if (mode == OriginalMode.TOGGLE) {
                if (!idToToggleQueue.containsKey(id)) {
                    TickUtil.ToggleQueue queue = new TickUtil.ToggleQueue(
                            TickUtil.entry(() -> VanillaKeyBindingHelper.toggle(id, true)),
                            TickUtil.entry(() -> VanillaKeyBindingHelper.trigger(id)),
                            TickUtil.entry(() -> VanillaKeyBindingHelper.toggle(id, false), binding.getToggleDelay()),
                            TickUtil.entry(binding.getToggleDelay())
                    );
                    TickUtil.addQueue(queue);
                    idToToggleQueue.put(id, queue);
                }

                idToToggleQueue.get(id).toggle();
            } else {
                VanillaKeyBindingHelper.toggle(id, value);
                if (value) {
                    VanillaKeyBindingHelper.trigger(id);
                }
            }
        });
        VanillaKeyBindingHelper.register(register);
    }

    private static final Supplier<ComboSettings> SETTINGS_SUPPLIER = () -> new ComboSettings(new VanillaMode(), new IContext[]{Context.EVERYWHERE}, true, FilterMode.ALL);
    private static ComboSettings getSettings(OriginalMode mode) {
        return new ComboSettings(switch (mode) {
            case UNKNOWN -> new VanillaMode();
            case ACTION -> new ActionMode();
            case HOLD -> new HoldMode();
            case TOGGLE -> new ToggleMode();
        }, new IContext[]{Context.EVERYWHERE}, true, FilterMode.ALL);
    }
    private final Map<OriginalMode, Map<KeyCombo, ComboSettings>> originalModeToComboSettings = new HashMap<>();
    private OriginalMode original = OriginalMode.UNKNOWN;
    private int toggleDelay = 0;
    private VanillaKeyBindingWrapper(net.minecraft.client.option.KeyBinding binding) {
        super(binding.getTranslationKey(), getKeyCombos(binding), SETTINGS_SUPPLIER.get());
        InitialKeyBindings.add(this);
        final String id = binding.getTranslationKey();
        KeyBindingUtil.onAction(id, () -> VanillaKeyBindingHelper.trigger(getId()));
    }
    private VanillaKeyBindingWrapper(String id, List<KeyCombo> keyCombos, ComboSettings defaultSettings, OriginalMode original) {
        super(id, keyCombos, defaultSettings);
        this.original = original;
    }

    @Override
    public void reset() {
        originalModeToComboSettings.clear();
        original = OriginalMode.UNKNOWN;
        super.reset();
    }

    @Override
    public ComboSettings getDefaultSettings() {
        return getSettings(original);
    }

    private void updateCombos() {
        Map<KeyCombo, ComboSettings> comboSettings = originalModeToComboSettings.get(original);
        for (KeyCombo combo : getKeyCombos()) {
            if (comboSettings != null && comboSettings.containsKey(combo)) {
                combo.setSettings(comboSettings.get(combo));
            } else {
                combo.setSettings(getDefaultSettings());
            }
        }
    }

    private Map<KeyCombo, ComboSettings> getComboSettingsBackup() {
        List<KeyCombo> combos = getKeyCombos();
        Map<KeyCombo, ComboSettings> backup = new HashMap<>(combos.size());
        for (KeyCombo combo : combos) {
            backup.put(combo, combo.getSettings());
        }
        return backup;
    }

    public OriginalMode getOriginalMode() {
        return original;
    }

    public void setOriginalMode(OriginalMode original) {
        originalModeToComboSettings.put(this.original, getComboSettingsBackup());
        if (this.original == OriginalMode.TOGGLE && idToToggleQueue.containsKey(getId())) {
            idToToggleQueue.remove(getId()).removeOnceDone();
        } else {
            VanillaKeyBindingHelper.toggle(getId(), false);
        }
        this.original = original;
        updateCombos();
    }

    public int getToggleDelay() {
        return toggleDelay;
    }

    public void setToggleDelay(int toggleDelay) {
        this.toggleDelay = toggleDelay;
        if (idToToggleQueue.containsKey(getId()))
            idToToggleQueue.remove(getId()).removeOnceDone();
    }

    private static List<KeyCombo> getKeyCombos(net.minecraft.client.option.KeyBinding binding) {
        InputUtil.Key boundKey = KeyBindingHelper.getBoundKeyOf(binding);
        return boundKey.equals(InputUtil.UNKNOWN_KEY) ? List.of() : List.of(
                new KeyCombo(
                        binding.getTranslationKey(),
                        List.of(
                                new BasicKey(
                                        boundKey
                                )
                        ),
                        SETTINGS_SUPPLIER.get()
                )
        );
    }

    @Override
    public JsonElement save() {
        JsonObject object = (JsonObject) super.save();
        object.add("original", JsonUtil.JEnum.toJson(original));
        if (original == OriginalMode.TOGGLE)
            object.add("toggleDelay", JsonUtil.toJson(toggleDelay));
        return object;
    }

    @Override
    public void load(JsonElement json) {
        JsonObject object = JsonUtil.require(json, JsonObject.class);
        original = JsonUtil.fromJson(object.get("original"), OriginalMode.class);
        if (original == OriginalMode.TOGGLE)
            toggleDelay = JsonUtil.fromJson(object.get("toggleDelay"), Integer.class);
        super.load(object);
    }

    @Override
    public VanillaKeyBindingWrapper copy() {
        return copy((id, defaultKeyCombos, defaultSettings) -> new VanillaKeyBindingWrapper(id, defaultKeyCombos, defaultSettings, original));
    }
}
