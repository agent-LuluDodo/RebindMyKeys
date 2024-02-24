package de.luludodo.rebindmykeys.potential.binding;

import com.google.gson.*;
import de.luludodo.rebindmykeys.potential.binding.button.Button;
import de.luludodo.rebindmykeys.potential.binding.button.ButtonManager;
import de.luludodo.rebindmykeys.potential.binding.mode.ActionMode;
import de.luludodo.rebindmykeys.potential.binding.mode.ToggleMode;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.IBuilder;
import de.luludodo.rebindmykeys.util.interfaces.InputListener;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import de.luludodo.rebindmykeys.util.interfaces.Resetable;
import de.luludodo.rebindmykeys.potential.binding.mode.Mode;
import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Binding<M extends Mode> implements InputListener, Resetable, JsonSavable {
    public static class Builder<M extends Mode> implements IBuilder<Binding<M>> {
        private final String id;
        private Collection<Button> buttons = new ArrayList<>();
        private M mode;
        private Builder(String id) {
            this.id = id;
        }

        public void addKey(Button key) {
            buttons.add(key);
        }

        public void setButtons(Collection<Button> buttons) {
            this.buttons = buttons;
        }

        public void setMode(M mode) {
            this.mode = mode;
        }

        @Override
        public Binding<M> build() {
            return new Binding<>(id, buttons, mode);
        }
    }
    public static Builder<ToggleMode> toggle(String id) {
        return new Builder<>(id);
    }
    public static Builder<ActionMode> action(String id) {
        return new Builder<>(id);
    }
    public static Builder<Mode> general(String id) {
        return new Builder<>(id);
    }
    public static Binding<? extends Mode> fromJson(final String id, JsonElement json) {
        JsonObject jsonObject = JsonUtil.require(json, JsonObject.class);
        String type = JsonUtil.requireString(jsonObject.get("type"));
        JsonElement modeJson = jsonObject.get("mode");
        final Mode mode = switch (type) {
            case "toggle" -> JsonUtil.requireEnum(modeJson, ToggleMode.class);
            case "action" -> JsonUtil.requireEnum(modeJson, ActionMode.class);
            default -> throw new JsonParseException("Expected type to be either 'toggle' or 'action' but found " + type);
        };
        JsonArray buttonArray = JsonUtil.require(json, JsonArray.class);
        List<Button> buttons = new ArrayList<>(buttonArray.size());
        buttonArray.forEach(buttonJson -> buttons.add(ButtonManager.fromJson(buttonJson)));
        return new Binding<>(id, buttons, mode);
    }

    private final String id;
    private List<Button> buttons;
    private final List<Button> defaultButtons;
    private M mode;
    private final M defaultMode;
    public Binding(String id, Collection<Button> buttons, M mode) {
        this.id = id;
        this.buttons = new ArrayList<>(buttons);
        this.defaultButtons = Collections.unmodifiableList(this.buttons);
        this.mode = mode;
        this.defaultMode = mode;
    }

    public String getId() {
        return id;
    }

    @Override
    public void onKeyDown(InputUtil.Key key, List<Modifier> modifiers) {

    }

    @Override
    public void onKeyUp(InputUtil.Key key, List<Modifier> modifiers) {

    }

    @Override
    public JsonElement save() {
        return null;
    }

    @Override
    public void load(JsonElement json) {

    }

    @Override
    public void reset() {
        this.buttons = defaultButtons;
        this.mode = defaultMode;
    }
}
