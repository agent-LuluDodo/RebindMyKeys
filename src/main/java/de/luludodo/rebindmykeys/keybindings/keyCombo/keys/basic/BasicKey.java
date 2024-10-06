package de.luludodo.rebindmykeys.keybindings.keyCombo.keys.basic;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.reference.KeyReference;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.KeyUtil;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.UUID;

public class BasicKey implements Key {
    //private final UUID uuid = UUID.randomUUID(); If you ever need to differentiate Key use this
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

    public InputUtil.Key getKey() {
        return key;
    }

    @Override
    public Text getText() {
        return key.getLocalizedText();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BasicKey bk) {
            return bk.key.equals(key);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }
}
