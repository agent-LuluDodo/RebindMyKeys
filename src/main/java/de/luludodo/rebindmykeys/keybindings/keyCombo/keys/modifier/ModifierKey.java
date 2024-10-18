package de.luludodo.rebindmykeys.keybindings.keyCombo.keys.modifier;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.gui.keyCombo.widget.KeyComboWidget;
import de.luludodo.rebindmykeys.gui.widget.ResizableButtonWidget;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.util.ArrayUtil;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.KeyUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModifierKey implements Key {
    public static final Identifier ID = Identifier.of("rebindmykeys", "modifier");

    private Modifier modifier = Modifier.CONTROL;
    private boolean forcePressed = false;
    private List<InputUtil.Key> pressedKeys;
    public ModifierKey(Modifier modifier) {
        this.modifier = modifier;
        pressedKeys = new ArrayList<>(modifier.getKeys().length);
    }

    public ModifierKey() {
        pressedKeys = new ArrayList<>(modifier.getKeys().length);
    }

    @Override
    public void load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        this.modifier = loader.get("modifier", Modifier.class);
        pressedKeys = new ArrayList<>(modifier.getKeys().length);
    }

    @Override
    public void onKeyDown(InputUtil.Key key) {
        if (ArrayUtil.contains(modifier.getKeys(), key))
            pressedKeys.add(key);
    }

    @Override
    public void onKeyUp(InputUtil.Key key) {
        if (ArrayUtil.contains(modifier.getKeys(),key))
            forcePressed = false;

        pressedKeys.remove(key);
    }

    @Override
    public void release() {
        pressedKeys.clear();
    }

    @Override
    public void press() {
        forcePressed = true;
    }

    @Override
    public boolean isPressed() {
        return forcePressed || !pressedKeys.isEmpty();
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

    @Override
    public int hashCode() {
        return Objects.hashCode(modifier);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getTranslation() {
        return "rebindmykeys.key.modifier";
    }

    @Override
    public List<KeyComboWidget.KeyEntry.Button> getButtons() {
        return List.of(ResizableButtonWidget.builder(null, getText(), button -> {
            button.setMessage(Text.translatable("rebindmykeys.gui.keyBindings.recording"));
            KeyUtil.startRecordingModifierKey(modifier -> {
                this.modifier = modifier;
                pressedKeys = new ArrayList<>(modifier.getKeys().length);
                button.setMessage(getText());
            });
        }).build());
    }
}
