package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Consumer;

public class KeyBindingUtil {

    private static final Map<String, Action> onActions = new HashMap<>();
    public static void onAction(String id, Action action) {
        onActions.put(id, action);
    }
    private static void triggerAction(String id) {
        RebindMyKeys.DEBUG.info("Triggering action: " + id);
        if (!onActions.containsKey(id))
            return;
        try {
            onActions.get(id).run();
        } catch (RuntimeException e) { // We don't want Minecraft to crash if a KeyBinding bugged out or something TODO: add toast for error
            RebindMyKeys.LOG.error("KeyBinding " + id + " failed action", e);
        }
    }

    private static final Map<String, Consumer<@NotNull Boolean>> onToggles = new HashMap<>();
    public static void onToggle(String id, Consumer<@NotNull Boolean> action) {
        onToggles.put(id, action);
    }
    private static void triggerToggle(String id, boolean newState) {
        RebindMyKeys.DEBUG.info("Triggering toggle: " + id + " | " + newState);
        if (!onToggles.containsKey(id))
            return;
        try {
            onToggles.get(id).accept(newState);
        } catch (RuntimeException e) { // TODO: add toast for error
            RebindMyKeys.LOG.error("KeyBinding " + id + " failed toggle to " + newState, e);
        }
    }

    public static void onKey(InputUtil.Key key, boolean press) {
        // If recording for a KeyCombo don't execute actions
        if (KeyUtil.isRecording()) {
            if (press) {
                KeyUtil.addRecordedKey(key);
                return;
            } else if (KeyUtil.isInRecording(key)) {
                KeyUtil.stopRecording();
                return;
            }
        }
        // Send press/release to every KeyBinding
        KeyUtil.getAll().forEach(keyBinding -> {
            if (press) {
                keyBinding.onKeyDown(key);
            } else {
                keyBinding.onKeyUp(key);
            }
        });
        // Updates cached pressed state
        KeyUtil.getAll().forEach(KeyBinding::updatePressed);
    }

    public static void update() {
        // Update cached active state
        KeyUtil.getAll().forEach(KeyBinding::updateActive);
        // Filter results
        filter();
        // Trigger KeyBinding if necessary
        KeyUtil.getAll().forEach(KeyBindingUtil::tryTrigger);
        // Tell KeyBindings that the update is done
        KeyUtil.getAll().forEach(KeyBinding::done);
    }

    private static void filter() {
        // Apply some filter rules to only keep valid ones (example: when "CRTL + Q -> Drop Stack" triggers "Q -> Drop Item" shouldn't trigger)
        Set<UUID> invalidUUIDs = CollectionUtil.joinCollection(HashSet::new, CollectionUtil.run(KeyUtil.getAll(), KeyBinding::getIncompatibleUUIDs));
        //RebindMyKeys.DEBUG.info("invalidUUIDs='{}'", CollectionUtil.toString(invalidUUIDs));
        KeyUtil.getAll().forEach(binding -> binding.filter(invalidUUIDs));
    }

    private static void tryTrigger(KeyBinding binding) {
        // Checks if KeyBinding was Triggered
        if (binding.wasTriggered()) {
            // Triggers corresponding function
            if (binding.isAction()) {
                triggerAction(binding.getId());
            } else {
                triggerToggle(binding.getId(), binding.isToggled());
            }
        }
    }

    public static void checkContext() {
        // Checks if context changed for any KeyBinding, updates them if it did
        if (CollectionUtil.oneCondition(KeyUtil.getAll(), KeyBinding::checkContext)) {
            update();
        }
    }

    public static void calcIncompatibleUUIDs() {
        // Collects all KeyCombos in a HashSet
        Set<KeyCombo> keyCombos = new HashSet<>();
        KeyUtil.getAll().forEach(binding -> keyCombos.addAll(binding.getKeyCombos()));
        // Calculates all incompatible UUIDs
        KeyUtil.getAll().forEach(binding -> binding.calcIncompatibleUUIDs(keyCombos));
    }

    /*
    public static void updateAll() {
        updateAll(false);
    }

    public static void updateAll(boolean isOnKeyPress) {
        // Update KeyCombos
        for (Collection<KeyCombo> keyCombos : KeyUtil.getCombosByLength().values()) {
            boolean shouldReturn = false;
            for (KeyCombo keyCombo : keyCombos) {
                keyCombo.updateActive();
                if (keyCombo.wasTriggered())
                    shouldReturn = true;
            }

            if (shouldReturn && isOnKeyPress)
                break;
        }

        // Check if KeyBinding should trigger action
        KeyUtil.getAll().forEach(keyBinding -> {
            if (keyBinding.isAction()) {
                if (keyBinding.wasPressed()) {
                    triggerAction(keyBinding.getId());
                }
            } else {
                if (keyBinding.stateChanged()) {
                    triggerToggle(keyBinding.getId(), keyBinding.isToggled());
                }
            }
        });
    }

    public static void checkContextAll() {
        // Checks if context changed, if that's the case updates all KeyBindings
        if (CollectionUtil.oneCondition(KeyUtil.getAll(), KeyBinding::checkContext)) {
            updateAll();
        }
    }
    */
}
