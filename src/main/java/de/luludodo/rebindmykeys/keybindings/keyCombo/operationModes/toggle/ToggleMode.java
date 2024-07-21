package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.toggle;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationModeRegistry;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.PressCountOperationMode;
import de.luludodo.rebindmykeys.util.JsonUtil;
import net.minecraft.util.Identifier;

public class ToggleMode extends PressCountOperationMode {
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
    public boolean updateKeyDown() {
        return toggleOnPress;
    }

    @Override
    public boolean updateKeyUp() {
        return !toggleOnPress;
    }

    @Override
    public void update(boolean active) {
        if (applyMulti(active)) {
            toggled = !toggled;
            wasTriggered = !wasTriggered;
        }
    }

    @Override
    public void deactivate() {
        wasTriggered = toggled && !wasTriggered;
        toggled = false;
    }
    @Override
    public void activate() {
        wasTriggered = !toggled && !wasTriggered;
        toggled = true;
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
    public void load(JsonUtil.ObjectLoader loader) {
        initialState = loader.get("initialState", Boolean.class);
        toggleOnPress = loader.get("toggleOnPress", Boolean.class);
        toggled = initialState;
    }

    @Override
    public void save(JsonUtil.ObjectBuilder builder) {
        builder.add("initialState", initialState);
        builder.add("toggleOnPress", toggleOnPress);
    }

    @Override
    public Identifier getIcon() {
        return new Identifier("rebindmykeys", "textures/gui/toggle.png");
    }

    @Override
    public String getTranslation() {
        return "rebindmykeys.operationMode.toggle";
    }

    public boolean getInitialState() {
        return initialState;
    }
    public void setInitialState(boolean initialState) {
        this.initialState = initialState;
    }
    public boolean getToggleOn() {
        return toggleOnPress;
    }
    public void setToggleOn(boolean toggleOnPress) {
        this.toggleOnPress = toggleOnPress;
    }
}
