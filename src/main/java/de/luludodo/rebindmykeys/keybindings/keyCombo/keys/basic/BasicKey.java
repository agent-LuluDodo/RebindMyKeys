package de.luludodo.rebindmykeys.keybindings.keyCombo.keys.basic;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.gui.keyCombo.widget.KeyComboWidget;
import de.luludodo.rebindmykeys.gui.widget.ResizableButtonWidget;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.KeyUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

public class BasicKey implements Key {
    public static final Identifier ID = Identifier.of("rebindmykeys", "basic");

    //private final UUID uuid = UUID.randomUUID(); If you ever need to differentiate Key use this
    private InputUtil.Key key = InputUtil.UNKNOWN_KEY;
    private boolean pressed = false;
    public BasicKey(InputUtil.Key key) {
        this.key = key;
    }
    public BasicKey() {}

    @Override
    public void load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        this.key = loader.get("type", InputUtil.Type.class).createFromCode(loader.get("code", Integer.class));
        pressed = false;
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
    public void press() {
        pressed = true;
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

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getTranslation() {
        return "rebindmykeys.key.basic";
    }

    @Override
    public List<KeyComboWidget.KeyEntry.Button> getButtons() {
        return List.of(ResizableButtonWidget.builder(null, getText(), button -> {
            button.setMessage(Text.translatable("rebindmykeys.gui.keyBindings.recording"));
            KeyUtil.startRecordingBasicKey(key -> {
                this.key = key;
                pressed = false;
                button.setMessage(getText());
            });
        }).build());
    }
}
