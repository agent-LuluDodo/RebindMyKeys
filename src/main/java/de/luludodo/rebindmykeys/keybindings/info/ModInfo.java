package de.luludodo.rebindmykeys.keybindings.info;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.*;

public final class ModInfo {
    private static ModContainer mod = null;
    private static final Map<ModContainer, List<String>> modToIds = new HashMap<>();
    private static final Map<String, ModContainer> idToMod = new HashMap<>();

    private static ModContainer toModContainer(String modId) {
        return modId == null? null : FabricLoader.getInstance().getModContainer(modId).orElseThrow(() -> new IllegalArgumentException("No mod with id '" + modId + "' found"));
    }

    public static void setMod(String mod) {
        ModInfo.mod = toModContainer(mod);
    }

    public static void addId(String id) {
        if (!modToIds.containsKey(mod)) {
            modToIds.put(mod, new ArrayList<>());
        }
        modToIds.get(mod).add(id);
        idToMod.put(id, mod);
    }

    public static ModContainer getMod(String id) {
        return idToMod.get(id);
    }

    public static Map<ModContainer, List<String>> getMods() {
        return Collections.unmodifiableMap(modToIds);
    }

    public static String getModName(ModContainer mod) {
        return mod == null ? "Minecraft" : mod.getMetadata().getName();
    }
}
