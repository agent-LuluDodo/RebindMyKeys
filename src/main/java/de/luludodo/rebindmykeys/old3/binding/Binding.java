package de.luludodo.rebindmykeys.old3.binding;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.luludodo.rebindmykeys.old3.binding.mode.Mode;
import de.luludodo.rebindmykeys.old3.binding.wrapper.Condition;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.Modifier;
import de.luludodo.rebindmykeys.util.ObjectUtil;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Binding<M extends Mode> {
    public static Binding<?> load(String id, JsonElement json) {
        ObjectUtil.require(json, JsonObject.class);
    }

    private final String id;
    private List<Condition<M>> conditions;
    private final List<Condition<M>> defaultConditions;
    public Binding(String id, List<Condition<M>> conditions) {
        this.id = id;
        this.conditions = new ArrayList<>(conditions);
        this.defaultConditions = Collections.unmodifiableList(conditions);
    }

    public String getId() {
        return id;
    }

    public void onKeyDown(InputUtil.Key key, List<Modifier> modifiers) {
        conditions.forEach(condition -> condition.onKeyDown(key, modifiers));
    }
    public void onKeyUp(InputUtil.Key key, List<Modifier> modifiers) {
        conditions.forEach(condition -> condition.onKeyUp(key, modifiers));
    }

    public boolean valid(InputUtil.Key key, List<Modifier> modifiers) {
        AtomicBoolean valid = new AtomicBoolean(true);
        conditions.forEach(condition -> {
            if (!condition.valid(key, modifiers))
                valid.set(false);
        });
        return valid.get();
    }

    public void reset() {
        this.conditions = new ArrayList<>(defaultConditions);
    }
}
