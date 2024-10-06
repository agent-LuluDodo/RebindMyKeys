package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold;

import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.pressCount.PressCountOperationMode;
import de.luludodo.rebindmykeys.util.JsonUtil;
import net.minecraft.util.Identifier;

public class HoldMode extends PressCountOperationMode {
    public static final Identifier ID = new Identifier("rebindmykeys", "hold");

    private boolean inverted = false;
    private boolean pressed = false;
    private boolean wasTriggered = false;
    public HoldMode() {
        this(false);
    }
    public HoldMode(boolean inverted) {
        this.inverted = inverted;
    }

    @Override
    public boolean updateKeyDown() {
        return !inverted;
    }

    @Override
    public boolean updateKeyUp() {
        return inverted;
    }

    @Override
    public void deactivate() {
        wasTriggered = pressed && !wasTriggered;
        pressed = false;
    }
    @Override
    public void activate() {
        wasTriggered = !pressed && !wasTriggered;
        pressed = true;
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
    protected void load(JsonUtil.ObjectLoader loader) {
        inverted = loader.get("inverted", Boolean.class);
    }

    @Override
    protected void save(JsonUtil.ObjectBuilder builder) {
        builder.add("inverted", inverted);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public HoldModeEditor getEditor() {
        return new HoldModeEditor();
    }

    @Override
    public Identifier getIcon() {
        return new Identifier("rebindmykeys", "textures/gui/hold.png");
    }

    @Override
    public String getTranslation() {
        return "rebindmykeys.operationMode.hold";
    }

    public boolean getInverted() {
        return inverted;
    }
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
}
