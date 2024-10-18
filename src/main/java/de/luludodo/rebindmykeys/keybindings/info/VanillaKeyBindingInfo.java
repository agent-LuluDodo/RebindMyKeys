package de.luludodo.rebindmykeys.keybindings.info;

import de.luludodo.rebindmykeys.RebindMyKeys;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.option.KeyBinding;

import java.util.*;

public class VanillaKeyBindingInfo {
    private static final Map<ModContainer, Integer> modToCount = new HashMap<>();
    private static final List<VanillaKeyBinding> vanillaKeybindings = new ArrayList<>();
    public static void add(KeyBinding binding, ModContainer mod) {
        if (!modToCount.containsKey(mod)) {
            modToCount.put(mod, 1);
        } else {
            modToCount.put(mod, modToCount.get(mod) + 1);
        }
        vanillaKeybindings.add(new VanillaKeyBinding(binding, mod));
    }

    public static void printCountInfo() {
        modToCount.forEach((mod, count) -> RebindMyKeys.LOG.info("Loaded {} vanilla key bindings for {} ({})", count, mod.getMetadata().getName(), mod.getMetadata().getId()));
    }

    public static List<VanillaKeyBinding> getVanillaKeybindings() {
        return Collections.unmodifiableList(vanillaKeybindings);
    }
}
