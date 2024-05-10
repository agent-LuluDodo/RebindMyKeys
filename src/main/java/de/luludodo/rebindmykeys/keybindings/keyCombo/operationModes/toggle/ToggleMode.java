package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.toggle;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;
import de.luludodo.rebindmykeys.util.JsonUtil;
import net.minecraft.util.Identifier;

public class ToggleMode implements OperationMode {
    private boolean initialState;
    private boolean toggleOnPress;
    private boolean toggled;
    private boolean wasTriggered = false;
    public ToggleMode() {
        this(false, true);
    }
    public ToggleMode(boolean initialState, boolean toggleOnPress) {
        this.initialState = initialState;
        this.toggleOnPress = toggleOnPress;
        toggled = initialState;
    }
    public ToggleMode(JsonElement json) {
        load(json);
    }


    @Override
    public void onKeyDown() {
        if (toggleOnPress) {
            toggled = !toggled;
            wasTriggered = true;
        }
    }

    @Override
    public void onKeyUp() {
        if (!toggleOnPress) {
            toggled = !toggled;
            wasTriggered = true;
        }
    }

    @Override
    public boolean isActive() {
        return toggled;
    }

    @Override
    public boolean wasTriggered() {
        return wasTriggered;
    }

    public void done() {
        wasTriggered = false;
    }

    @Override
    public void load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        initialState = loader.get("initialState", Boolean.class);
        toggleOnPress = loader.get("toggleOnPress", Boolean.class);
        toggled = initialState;
    }

    @Override
    public JsonElement save() {
        return JsonUtil.object()
                .add("initialState", initialState)
                .add("toggleOnPress", toggleOnPress)
                .build();
    }

    @Override
    public Identifier getIcon() {
        return new Identifier("rebindmykeys", "textures/gui/toggle.png");
    }

    @Override
    public String getName() {
        return "rebindmykeys.operationMode.toggle";
    }
}
