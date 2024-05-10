package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;
import de.luludodo.rebindmykeys.util.JsonUtil;
import net.minecraft.util.Identifier;

public class HoldMode implements OperationMode {
    private boolean inverted = false;
    private boolean pressed = false;
    private boolean wasTriggered = false;
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
        wasTriggered = true;
    }

    @Override
    public void onKeyUp() {
        pressed = false;
        wasTriggered = true;
    }

    public void done() {
        wasTriggered = false;
    }

    @Override
    public boolean isActive() {
        return inverted != pressed; // if inverted !pressed else pressed
    }

    @Override
    public boolean wasTriggered() {
        return wasTriggered;
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

    @Override
    public Identifier getIcon() {
        return new Identifier("rebindmykeys", "textures/gui/hold.png");
    }

    @Override
    public String getName() {
        return "rebindmykeys.operationMode.hold";
    }
}
