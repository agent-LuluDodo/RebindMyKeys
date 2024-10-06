package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold.HoldModeEditor;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.JsonLoadable;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import net.minecraft.util.Identifier;

public interface OperationMode extends JsonSavable, JsonLoadable {
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

    /**
     * This method will always return the same {@link Identifier},
     * so that {@code mode1.getId().equals(mode2.getId())} will always return {@code true}
     * if {@code mode1} and {@code mode2} are both of the same class.
     * @return The Identifier of this object.
     */
    Identifier getId();
    OperationModeEditor<?> getEditor();

    static JsonElement save(OperationMode mode) {
        return JsonUtil.object()
                .add("type", mode.getId())
                .add("settings", mode.save())
                .build();
    }

    static OperationMode create(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        JsonElement settings = loader.get("settings", JsonElement.class);
        OperationMode mode = OperationModeRegistry.constructOptional(loader.get("type", Identifier.class)).orElseThrow();
        mode.load(settings);
        return mode;
    }
}
