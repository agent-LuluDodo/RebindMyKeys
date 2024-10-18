package de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.pressCount;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;
import de.luludodo.rebindmykeys.profiles.ProfileManager;
import de.luludodo.rebindmykeys.util.JsonUtil;

public abstract class PressCountOperationMode implements OperationMode {
    private int pressCount = 1;
    @Override
    public int getPressCount() {
        return pressCount;
    }
    @Override
    public void setPressCount(int pressCount) {
        this.pressCount = pressCount;
    }

    @Override
    public void onKeyDown() {
        update(updateKeyDown());
    }
    @Override
    public void onKeyUp() {
        update(updateKeyUp());
    }

    protected abstract boolean updateKeyDown();
    protected abstract boolean updateKeyUp();

    public void update(boolean active) {
        if (applyMulti(active)) {
            activate();
        } else {
            deactivate();
        }
    }

    private long lastTrigger = -1;
    private int triggerStreak = 0;
    protected boolean applyMulti(boolean active) { // FIXME: Deactivate if press was part of multi (eg. 1x SPACE -> JUMP 2x SPACE -> FLY [disable JUMP])
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastTrigger) > ProfileManager.getCurrentProfile().getGlobal().getMultiClickDelayMs()) {
            triggerStreak = 0;
        }

        if (active) {
            lastTrigger = currentTime;
            triggerStreak++;

            return triggerStreak >= getPressCount();
        }

        return false;
    }

    public void load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        pressCount = loader.get("pressCount", Integer.class);
        load(loader);
    }
    protected abstract void load(JsonUtil.ObjectLoader loader);

    @Override
    public JsonElement save() {
        JsonUtil.ObjectBuilder builder = JsonUtil.object();
        builder.add("pressCount", pressCount);
        save(builder);
        return builder.build();
    }
    protected abstract void save(JsonUtil.ObjectBuilder builder);

    @Override
    public abstract PressCountOperationModeEditor<?> getEditor();
}
