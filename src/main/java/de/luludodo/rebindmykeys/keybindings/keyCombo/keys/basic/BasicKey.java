package de.luludodo.rebindmykeys.keybindings.keyCombo.keys.basic;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.KeyUtil;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class BasicKey implements Key {
    private final InputUtil.Key key;
    private boolean pressed = false;
    public BasicKey(InputUtil.Key key) {
        this.key = key;
    }
    public BasicKey(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        this.key = loader.get("type", InputUtil.Type.class).createFromCode(loader.get("code", Integer.class));
    }

    @Override
    public void onKeyDown(InputUtil.Key key) {
        if (this.key.equals(key)) pressed = true;
    }

    @Override
    public void onKeyUp(InputUtil.Key key) {
        if (this.key.equals(key)) pressed = false;
    }

    @Override
    public void release() {
        pressed = false;
    }

    @Override
    public boolean isPressed() {
        return pressed;
    }

    @Override
    public JsonElement save() {
        return JsonUtil.object()
                .add("type", key.getCategory())
                .add("code", key.getCode())
                .build();
    }
}
