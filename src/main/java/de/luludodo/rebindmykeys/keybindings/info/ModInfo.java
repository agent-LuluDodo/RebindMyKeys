package de.luludodo.rebindmykeys.keybindings.info;

import de.luludodo.rebindmykeys.util.FabricUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.*;

public final class ModInfo {
    private static ModContainer mod = null;
    private static final Map<ModContainer, List<String>> modToIds = new HashMap<>();
    private static final Map<String, ModContainer> idToMod = new HashMap<>();

    private static ModContainer toModContainer(String modId) {
        return modId.equals("minecraft") ? FabricUtil.VANILLA : FabricLoader.getInstance().getModContainer(modId).orElse(FabricUtil.UNKNOWN);
    }

    public static void setMod(String mod) {
        ModInfo.mod = toModContainer(mod);
    }

    public static void addId(String id) {
        add(id, mod);
    }

    public static void add(String id, ModContainer mod) {
        if (!modToIds.containsKey(mod)) {
            modToIds.put(mod, new ArrayList<>());
        }
        modToIds.get(mod).add(id);
        idToMod.put(id, mod);
    }

    public static ModContainer getMod(String id) {
        return idToMod.get(id);
    }

    public static String getModName(ModContainer mod) {
        return mod.getMetadata().getName();
    }
}
