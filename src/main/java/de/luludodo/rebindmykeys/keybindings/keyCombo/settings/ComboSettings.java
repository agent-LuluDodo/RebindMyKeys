package de.luludodo.rebindmykeys.keybindings.keyCombo.settings;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.FilterMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.IContext;
import de.luludodo.rebindmykeys.keybindings.registry.LuluRegistries;
import de.luludodo.rebindmykeys.util.ArrayUtil;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;

public record ComboSettings(OperationMode operationMode, IContext[] context, boolean orderSensitive, FilterMode filter) implements JsonSavable {
    public boolean contextValid() {
        return ArrayUtil.oneCondition(this.context(), IContext::isCurrent);
    }

    public JsonElement save() {
        return JsonUtil.object()
                .add("operationMode", LuluRegistries.OPERATION_MODE.save(operationMode))
                .add("context", context)
                .add("orderSensitive", orderSensitive)
                .add("filter", filter)
                .build();
    }

    public static ComboSettings load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        IContext[] contexts = loader.array("context").toArray(IContext::load, IContext.class);
        return new ComboSettings(
                loader.get("operationMode", LuluRegistries.OPERATION_MODE::load),
                contexts,
                loader.get("orderSensitive", Boolean.class),
                loader.get("filter", FilterMode.class)
        );
    }
}