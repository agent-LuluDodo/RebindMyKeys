package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.util.JsonUtil;

public class HoldMode implements OperationMode {
    private boolean inverted = false;
    private boolean pressed = false;
    public HoldMode() {
        this(false);
    }
    public HoldMode(boolean inverted) {
        this.inverted = inverted;
    }
    public HoldMode(JsonElement json) {
        load(json);
    }

    @Override
    public void onKeyDown() {
        pressed = true;
    }

    @Override
    public void onKeyUp() {
        pressed = false;
    }

    @Override
    public boolean isActive() {
        return inverted != pressed; // if inverted !pressed else pressed
    }

    @Override
    public void load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        inverted = loader.get("inverted", Boolean.class);
    }

    @Override
    public JsonElement save() {
        return JsonUtil.object()
                .add("inverted", inverted)
                .build();
    }
}
