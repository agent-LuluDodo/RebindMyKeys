package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;
import de.luludodo.rebindmykeys.util.JsonUtil;
import net.minecraft.util.Identifier;

public class ActionMode implements OperationMode {
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
    public void onKeyDown() {
        switch (activateOn) {
            case PRESS, BOTH, UNKNOWN -> {
                active = true;
                wasTriggered = true;
            }
            default -> {
                active = false;
            }
        }
    }

    @Override
    public void onKeyUp() {
        switch (activateOn) {
            case RELEASE, BOTH -> {
                active = true;
                wasTriggered = true;
            }
            default -> {
                active = false;
            }
        }
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
    public void load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        activateOn = loader.get("activateOn", ActivateOn.class);
        active = false;
    }

    @Override
    public JsonElement save() {
        return JsonUtil.object()
                .add("activateOn", activateOn)
                .build();
    }

    @Override
    public Identifier getIcon() {
        return new Identifier("rebindmykeys", "textures/gui/action.png");
    }

    @Override
    public String getName() {
        return "rebindmykeys.operationMode.action";
    }
}

