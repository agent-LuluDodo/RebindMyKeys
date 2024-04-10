package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActivateOn;
import de.luludodo.rebindmykeys.util.JsonUtil;

public class ActionMode implements OperationMode {
    private ActivateOn activateOn;
    private int timesPressed = 0;
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
            case PRESS, BOTH, UNKNOWN -> timesPressed++;
        }
    }

    @Override
    public void onKeyUp() {
        switch (activateOn) {
            case RELEASE, BOTH -> timesPressed++;
        }
    }

    @Override
    public boolean isActive() {
        if (timesPressed == 0)
            return false;
        timesPressed--;
        return true;
    }

    public void setActivateOn(ActivateOn activateOn) {
        this.activateOn = activateOn;
        timesPressed = 0;
    }

    public ActivateOn getActivateOn() {
        return activateOn;
    }

    @Override
    public void load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        activateOn = loader.get("activateOn", ActivateOn.class);
        timesPressed = 0;
    }

    @Override
    public JsonElement save() {
        return JsonUtil.object()
                .add("activateOn", activateOn)
                .build();
    }
}

