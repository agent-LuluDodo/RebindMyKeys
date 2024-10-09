package de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget;

import de.luludodo.rebindmykeys.config.KeyBindingConfig;
import de.luludodo.rebindmykeys.keybindings.info.CategoryInfo;
import de.luludodo.rebindmykeys.keybindings.info.ModInfo;
import de.luludodo.rebindmykeys.util.MapUtil;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SortHelper {
    public static final Comparator<String> SORT_CATEGORY_ASCENDING = Comparator.comparingDouble(CategoryInfo.getOrder()::get);
    public static final Comparator<String> SORT_CATEGORY_DESCENDING = Comparator.<String>comparingDouble(CategoryInfo.getOrder()::get).reversed();
    private static final Comparator<ModContainer> SORT_MOD_ASCENDING = Comparator.comparing(ModInfo::getModName, String::compareToIgnoreCase);
    private static final Comparator<ModContainer> SORT_MOD_DESCENDING = Comparator.comparing(ModInfo::getModName, String::compareToIgnoreCase).reversed();
    private static final Comparator<String> SORT_NAME_ASCENDING = Comparator.comparing(s -> Text.translatable(s).getString(), String::compareToIgnoreCase);
    private static final Comparator<String> SORT_NAME_DESCENDING = Comparator.<String, String>comparing(s -> Text.translatable(s).getString(), String::compareToIgnoreCase).reversed();

    public static Map<String, List<String>> getSortedCategories(SortOrder order) {
        if (order == SortOrder.ASCENDING) {
            return MapUtil.sortByKey(CategoryInfo.getCategories(), SORT_CATEGORY_ASCENDING);
        } else {
            return MapUtil.sortByKey(CategoryInfo.getCategories(), SORT_CATEGORY_DESCENDING);
        }
    }

    public static Map<ModContainer, List<String>> getSortedMods(SortOrder order) {
        if (order == SortOrder.ASCENDING) {
            return MapUtil.sortByKey(ModInfo.getMods(), SORT_MOD_ASCENDING);
        } else {
            return MapUtil.sortByKey(ModInfo.getMods(), SORT_MOD_DESCENDING);
        }
    }

    public static List<String> getSortedNames(SortOrder order) {
        if (order == SortOrder.ASCENDING) {
            return KeyBindingConfig.getCurrent().options().stream().sorted(SORT_NAME_ASCENDING).toList();
        } else {
            return KeyBindingConfig.getCurrent().options().stream().sorted(SORT_NAME_DESCENDING).toList();
        }
    }
}
