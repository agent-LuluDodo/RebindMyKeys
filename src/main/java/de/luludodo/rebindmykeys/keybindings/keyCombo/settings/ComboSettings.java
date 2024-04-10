package de.luludodo.rebindmykeys.keybindings.keyCombo.settings;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.Context;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.IContext;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;

public record ComboSettings(OperationMode operationMode, IContext[] context, boolean orderSensitive) implements JsonSavable {
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