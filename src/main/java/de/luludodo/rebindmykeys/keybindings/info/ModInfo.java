package de.luludodo.rebindmykeys.keybindings.info;

import de.luludodo.rebindmykeys.util.MapUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;

import java.util.*;

public final class ModInfo {
    private static ModContainer mod = null;
    private static Map<ModContainer, List<String>> modToIds = new HashMap<>();
    private static final Map<String, ModContainer> idToMod = new HashMap<>();
    private static boolean sorted = true;

    private static final Comparator<ModContainer> SORT_MODS = Comparator.comparing(mod -> mod.getMetadata().getName(), String::compareToIgnoreCase);
    private static final Comparator<String> SORT_BINDINGS = Comparator.comparing(s -> Text.translatable(s).getString(), String::compareToIgnoreCase);

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
        sorted = false;
    }

    public static ModContainer getMod(String id) {
        return idToMod.get(id);
    }

    public static Map<ModContainer, List<String>> getMods() {
        if (!sorted) {
            sorted = true;
            modToIds = MapUtil.sortByKey(modToIds, SORT_MODS);
            modToIds.values().forEach(ids -> ids.sort(SORT_BINDINGS));
        }
        return Collections.unmodifiableMap(modToIds);
    }
}
