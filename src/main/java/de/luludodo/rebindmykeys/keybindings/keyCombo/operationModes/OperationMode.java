package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActionMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold.HoldMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.toggle.ToggleMode;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import net.minecraft.util.Identifier;

public interface OperationMode extends JsonSavable {
    void onKeyDown();
    void onKeyUp();
    void activate();
    void deactivate();
    boolean isActive();

    /**
     * Promises to always return the same value on consecutive calls without other state changes.
     */
    boolean wasTriggered();

    int getPressCount();
    void setPressCount(int pressCount);

    Identifier getIcon();
    String getTranslation();

    /**
     * Resets {@link OperationMode#wasTriggered()}.
     */
    void done();

    static JsonElement save(OperationMode mode) {
        return JsonUtil.object()
                .add("type", OperationModeRegistry.getIdOptional(mode.getClass()).orElseThrow())
                .add("pressCount", mode.getPressCount())
                .add("settings", mode.save())
                .build();
    }

    static OperationMode create(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        JsonElement settings = loader.get("settings", JsonElement.class);
        OperationMode mode = OperationModeRegistry.getOptional(loader.get("type", Identifier.class))
                .orElseThrow().construct(settings);
        mode.setPressCount(loader.get("pressCount", Integer.class));
        return mode;
    }
}
