package de.luludodo.rebindmykeys.keybindings.keyCombo.settings;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.IContext;
import de.luludodo.rebindmykeys.util.ArrayUtil;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;

import java.util.Arrays;

public record ComboSettings(OperationMode operationMode, IContext[] context, boolean orderSensitive) implements JsonSavable {
    public boolean contextValid() {
        return ArrayUtil.oneCondition(this.context(), IContext::isCurrent);
    }

    public JsonElement save() {
        return JsonUtil.object()
                .add("operationMode", OperationMode.save(operationMode))
                .add("context", context)
                .add("orderSensitive", orderSensitive)
                .build();
    }

    public static ComboSettings load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        return new ComboSettings(
                loader.get("operationMode", OperationMode::create),
                loader.get("context", IContext[].class),
                loader.get("orderSensitive", Boolean.class)
        );
    }
}