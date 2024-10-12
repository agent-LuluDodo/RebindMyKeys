package de.luludodo.rebindmykeys.keybindings.keyCombo.keys.reference;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.gui.keyCombo.widget.KeyComboWidget;
import de.luludodo.rebindmykeys.gui.widget.ResizableTextFieldWidget;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.util.JsonUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KeyReference implements Key {
    public static final Identifier ID = Identifier.of("rebindmykeys", "reference");

    private String reference = "key.jump";
    private KeyBinding binding = null;
    public KeyReference(String reference) {
        this.reference = reference;
    }

    public KeyReference() {}

    @Override
    public void load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        reference = loader.get("target", String.class);
        binding = null;
    }

    private KeyBinding binding() {
        if (binding == null) binding = KeyBinding.get(reference);
        if (binding == null) throw new IllegalArgumentException("Couldn't find KeyBinding with id " + reference);
        return binding;
    }

    @Override
    public void onKeyDown(InputUtil.Key key) {}

    @Override
    public void onKeyUp(InputUtil.Key key) {}


    private final List<Thread> loopProtection = new ArrayList<>(1);
    @Override
    public boolean isPressed() {
        Thread current = Thread.currentThread();
        if (loopProtection.contains(current)) {
            loopProtection.remove(current);
            return false;
        }

        loopProtection.add(current);
        boolean pressed = binding().isPressed();
        loopProtection.remove(current);
        return pressed;
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
    public Text getText() {
        return Text.translatable(reference);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof KeyReference kr) {
            return kr.reference.equals(reference);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reference);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getTranslation() {
        return "rebindmykeys.key.reference";
    }

    @Override
    public List<KeyComboWidget.KeyEntry.Button> getButtons() {
        ResizableTextFieldWidget widget = new ResizableTextFieldWidget(MinecraftClient.getInstance().textRenderer, null, w -> 0, h -> 0, w -> 0, h -> 0, Text.empty());
        widget.setEditableColor(0xFFFFFFFF);
        widget.setText(reference);
        widget.setCursor(0, false); // doesn't render the text until you click on it otherwise
        widget.setChangedListener(text -> {
            KeyBinding maybeBinding = KeyBinding.get(text);
            if (maybeBinding != null) {
                reference = text;
                binding = maybeBinding;
                widget.setEditableColor(0xFFFFFFFF);
            } else {
                widget.setEditableColor(0xFFFF5555);
            }
        });
        return List.of(widget);
    }
}
