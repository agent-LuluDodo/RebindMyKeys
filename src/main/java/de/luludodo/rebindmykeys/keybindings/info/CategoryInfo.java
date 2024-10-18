package de.luludodo.rebindmykeys.keybindings.info;

import java.util.*;

public final class CategoryInfo {
    private static String category = "";
    private static final Map<String, List<String>> categoryToIds = new HashMap<>();
    private static final Map<String, String> idToCategory = new HashMap<>();
    private static final Map<String, Double> categoryOrder = new HashMap<>();
    private static double currentOrder = 0;

    public static void setCategory(String category) {
        CategoryInfo.category = category;
    }

    /**
     * Smaller values are further up
     */
    public static void setIndex(double index) {
        categoryOrder.put(category, index);
    }

    public static Double getIndex(String category) {
        return categoryOrder.get(category);
    }

    public static void addId(String id) {
        add(id, category);
    }

    public static void add(String id, String category) {
        if (!categoryToIds.containsKey(category)) {
            categoryToIds.put(category, new ArrayList<>());
            if (!categoryOrder.containsKey(category))
                categoryOrder.put(category, currentOrder);
            currentOrder++;
        }
        categoryToIds.get(category).add(id);
        idToCategory.put(id, category);
    }

    public static String getCategory(String id) {
        return idToCategory.get(id);
    }

    public static Map<String, List<String>> getCategories() {
        return Collections.unmodifiableMap(categoryToIds);
    }

    public static Map<String, Double> getOrder() {
        return categoryOrder;
    }
}
