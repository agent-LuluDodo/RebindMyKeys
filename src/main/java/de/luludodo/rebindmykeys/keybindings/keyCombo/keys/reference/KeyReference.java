package de.luludodo.rebindmykeys.keybindings.keyCombo.keys.reference;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.KeyUtil;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;

public class KeyReference implements Key {
    private final String reference;
    private KeyBinding binding = null;
    public KeyReference(String reference) {
        this.reference = reference;
    }

    public KeyReference(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        reference = loader.get("target", String.class);
    }

    private KeyBinding binding() {
        if (binding == null) binding = KeyUtil.get(reference);
        if (binding == null) throw new IllegalArgumentException("Couldn't find KeyBinding with id " + reference);
        return binding;
    }

    @Override
    public void onKeyDown(InputUtil.Key key) {}

    @Override
    public void onKeyUp(InputUtil.Key key) {}


    @Override
    public boolean isPressed() {
        return binding().isPressed();
    }

    @Override
    public void release() {}

    @Override
    public JsonElement save() {
        return JsonUtil.object()
                .add("target", reference)
                .build();
    }

    public String getReference() {
        return reference;
    }

    @Override
    public String toString() {
        return reference;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof KeyReference kr) {
            return kr.reference.equals(reference);
        }
        return false;
    }
}
