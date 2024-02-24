package de.luludodo.rebindmykeys.potential.binding.button;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;

import java.util.List;

public class Key extends BasicButton {
    public static Key fromJson(JsonElement json) {
        JsonObject keyJson = JsonUtil.require(json, JsonObject.class);
        return new Key(
                JsonUtil.requireEnum(
                        keyJson.get("type"),
                        InputUtil.Type.class
                ).createFromCode(
                        JsonUtil.requireNumber(
                                keyJson.get("code")
                        ).intValue()
                )
        );
    }

    private InputUtil.Key key;
    public Key(InputUtil.Key key) {
        this.key = key;
    }

    public void setKey(InputUtil.Key key) {
        this.key = key;
    }
    public InputUtil.Key getKey() {
        return key;
    }

    @Override
    public boolean validate(InputUtil.Key key, List<Modifier> modifiers) {
        return this.key.equals(key);
    }
}
