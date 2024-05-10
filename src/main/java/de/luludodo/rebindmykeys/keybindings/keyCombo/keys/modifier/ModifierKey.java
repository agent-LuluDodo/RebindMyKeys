package de.luludodo.rebindmykeys.keybindings.keyCombo.keys.modifier;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.reference.KeyReference;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModifierKey implements Key {
    private final Modifier modifier;
    private final List<InputUtil.Key> pressedKeys;
    public ModifierKey(Modifier modifier) {
        this.modifier = modifier;
        pressedKeys = new ArrayList<>(modifier.getKeys().length);
    }

    public ModifierKey(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        this.modifier = loader.get("modifier", Modifier.class);
        pressedKeys = new ArrayList<>(modifier.getKeys().length);
    }

    @Override
    public void onKeyDown(InputUtil.Key key) {
        for (InputUtil.Key modifierKey : modifier.getKeys()) {
            if (modifierKey.equals(key)) {
                pressedKeys.add(key);
                return;
            }
        }
    }

    @Override
    public void onKeyUp(InputUtil.Key key) {
        pressedKeys.remove(key);
    }

    @Override
    public void release() {
        pressedKeys.clear();
    }

    @Override
    public boolean isPressed() {
        return !pressedKeys.isEmpty();
    }

    @Override
    public JsonElement save() {
        return JsonUtil.object()
                .add("modifier", modifier)
                .build();
    }

    public Modifier getModifier() {
        return modifier;
    }

    @Override
    public Text getText() {
        return Text.translatable(modifier.getTranslationKey());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ModifierKey mk) {
            return mk.modifier.equals(modifier);
        }
        return false;
    }
}
