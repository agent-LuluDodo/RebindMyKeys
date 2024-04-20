package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class KeyBindingUtil {
    private static final Map<String, Action> onActions = new HashMap<>();
    public static void onAction(String id, Action action) {
        onActions.put(id, action);
    }
    private static void triggerAction(String id) {
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
        try {
            onToggles.get(id).accept(newState);
        } catch (RuntimeException e) { // TODO: add toast for error
            RebindMyKeys.LOG.error("KeyBinding " + id + " failed toggle to " + newState, e);
        }
    }

    public static void onKeyAll(InputUtil.Key key, boolean press) {
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

    public static void updateAll() {
        // 1. Update KeyBinding
        // 2. Check if KeyBinding should trigger action
        KeyUtil.getAll().forEach(keyBinding -> {
            keyBinding.updateActive();

            if (keyBinding.isAction()) {
                if (keyBinding.wasPressed())
                    triggerAction(keyBinding.getId());
            } else {
                if (keyBinding.stateChanged())
                    triggerToggle(keyBinding.getId(), keyBinding.isToggled());
            }
        });
    }

    public static void checkContextAll() {
        // Checks if context changed, if that's the case updates all KeyBindings
        if (CollectionUtil.oneCondition(KeyUtil.getAll(), KeyBinding::checkContext)) {
            updateAll();
        }
    }
}
