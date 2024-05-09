package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActionMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold.HoldMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.toggle.ToggleMode;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.JsonLoadable;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;

public interface OperationMode extends JsonSavable, JsonLoadable {
    void onKeyDown();
    void onKeyUp();
    boolean isActive();

    /**
     * Promises to always return the same value on consecutive calls without other state changes.
     */
    boolean wasTriggered();

    /**
     * Resets {@link OperationMode#wasTriggered()}.
     */
    void done();

    static JsonElement save(OperationMode mode) {
        return JsonUtil.object()
                .add("type", OperationModeTypes.get(mode))
                .add("settings", mode.save())
                .build();
    }

    static OperationMode create(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        JsonElement settings = loader.get("settings", JsonElement.class);
        return switch (loader.get("type", OperationModeTypes.class)) {
            case ACTION -> new ActionMode(settings);
            case TOGGLE -> new ToggleMode(settings);
            case HOLD -> new HoldMode(settings);
        };
    }
}
