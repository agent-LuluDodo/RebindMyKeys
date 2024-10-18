package de.luludodo.rebindmykeys;

import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.basic.BasicKey;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.modifier.ModifierKey;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.reference.KeyReference;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActionMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold.HoldMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.toggle.ToggleMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.Context;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.IContextRegistry;
import de.luludodo.rebindmykeys.keybindings.registry.LuluRegistries;
import de.luludodo.rebindmykeys.modSupport.operationMode.vanilla.VanillaMode;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class RebindMyKeysPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        LuluRegistries.OPERATION_MODE.register(ActionMode::new);
        LuluRegistries.OPERATION_MODE.register(HoldMode::new);
        LuluRegistries.OPERATION_MODE.register(ToggleMode::new);
        LuluRegistries.OPERATION_MODE.register(VanillaMode::new);

        LuluRegistries.KEY.register(BasicKey::new);
        LuluRegistries.KEY.register(ModifierKey::new);
        LuluRegistries.KEY.register(KeyReference::new);

        IContextRegistry.register(Context.values());
    }
}
