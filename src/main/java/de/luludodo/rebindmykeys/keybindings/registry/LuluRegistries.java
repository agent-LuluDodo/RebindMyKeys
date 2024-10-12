package de.luludodo.rebindmykeys.keybindings.registry;

import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;

import static de.luludodo.rebindmykeys.keybindings.registry.JsonRegistry.createJson;

public class LuluRegistries {
    public static final JsonRegistry<OperationMode> OPERATION_MODE = createJson();
    public static final JsonRegistry<Key> KEY = createJson();
}
