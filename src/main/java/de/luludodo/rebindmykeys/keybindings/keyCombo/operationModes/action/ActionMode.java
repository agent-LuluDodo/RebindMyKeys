package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.PressCountOperationMode;
import de.luludodo.rebindmykeys.util.JsonUtil;
import net.minecraft.util.Identifier;

public class ActionMode extends PressCountOperationMode {
    private ActivateOn activateOn;
    private boolean active = false;
    private boolean wasTriggered = false;
    public ActionMode() {
        this(ActivateOn.PRESS);
    }
    public ActionMode(ActivateOn activateOn) {
        this.activateOn = activateOn;
    }
    public ActionMode(JsonElement json) {
        load(json);
    }

    @Override
    public boolean updateKeyDown() {
        return switch (activateOn) {
            case PRESS, BOTH, UNKNOWN -> true;
            default -> false;
        };
    }

    @Override
    public boolean updateKeyUp() {
        return switch (activateOn) {
            case RELEASE, BOTH -> true;
            default -> false;
        };
    }

    @Override
    public void activate() {
        active = true;
        wasTriggered = true;
    }

    @Override
    public void deactivate() {
        active = false;
        wasTriggered = false;
    }

    @Override
    public boolean isActive() {
        if (!active) return false;
        active = false;
        return true;
    }

    @Override
    public boolean wasTriggered() {
        return wasTriggered;
    }

    public void done() {
        wasTriggered = false;
    }

    public void setActivateOn(ActivateOn activateOn) {
        this.activateOn = activateOn;
        active = false;
    }

    public ActivateOn getActivateOn() {
        return activateOn;
    }

    @Override
    protected void load(JsonUtil.ObjectLoader loader) {
        activateOn = loader.get("activateOn", ActivateOn.class);
        active = false;
    }

    @Override
    protected void save(JsonUtil.ObjectBuilder builder) {
        builder.add("activateOn", activateOn);
    }

    @Override
    public Identifier getIcon() {
        return new Identifier("rebindmykeys", "textures/gui/action.png");
    }

    @Override
    public String getTranslation() {
        return "rebindmykeys.operationMode.action";
    }
}

