package de.luludodo.rebindmykeys.keybindings.info;

import de.luludodo.rebindmykeys.util.MapUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class CategoryInfo {
    private static String category = "";
    private static Map<String, List<String>> categoryToIds = new HashMap<>();
    private static final Map<String, String> idToCategory = new HashMap<>();
    private static final Map<String, Double> categoryOrder = new HashMap<>();
    private static double currentOrder = 0;
    private static boolean sorted = true;

    private static final Comparator<String> SORT_CATEGORIES = Comparator.comparingDouble(categoryOrder::get);
    private static final Comparator<String> SORT_BINDINGS = Comparator.comparing(s -> Text.translatable(s).getString(), String::compareToIgnoreCase);

    public static void setCategory(String category) {
        CategoryInfo.category = category;
    }

    /**
     * Smaller values are further up
     */
    public static void setIndex(double index) {
        categoryOrder.put(category, index);
        sorted = false;
    }

    public static @Nullable Double getIndex(String category) {
        return categoryOrder.get(category);
    }

    public static void addId(String id) {
        if (!categoryToIds.containsKey(category)) {
            categoryToIds.put(category, new ArrayList<>());
            if (!categoryOrder.containsKey(category))
                categoryOrder.put(category, currentOrder);
            currentOrder++;
        }
        categoryToIds.get(category).add(id);
        idToCategory.put(id, category);
        sorted = false;
    }

    public static String getCategory(String id) {
        return idToCategory.get(id);
    }

    public static Map<String, List<String>> getCategories() {
        if (!sorted) {
            sorted = true;
            categoryToIds = MapUtil.sortByKey(categoryToIds, SORT_CATEGORIES);
            categoryToIds.values().forEach(ids -> ids.sort(SORT_BINDINGS));
        }
        return Collections.unmodifiableMap(categoryToIds);
    }
}
