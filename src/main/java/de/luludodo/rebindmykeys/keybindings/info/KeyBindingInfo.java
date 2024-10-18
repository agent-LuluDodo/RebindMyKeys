package de.luludodo.rebindmykeys.keybindings.info;

import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class KeyBindingInfo {
    private static String currentKeyPrefix = "";
    private static String currentCategoryPrefix = "";

    @Contract(pure = true)
    public static String processPrefix(String prefix) {
        return (prefix == null || prefix.isBlank())? "" : prefix.strip() + ".";
    }

    @Contract(pure = true)
    private static String parseCategory(String category) {
        category = category.strip();
        if (category.isEmpty())
            throw new IllegalArgumentException("category is empty");
        if (category.charAt(0) == '#') {
            category = category.substring(1).strip();
            if (category.isEmpty())
                throw new IllegalArgumentException("category is empty");
            return category;
        } else {
            return currentCategoryPrefix + category;
        }
    }

    @Contract(pure = true)
    public static String getKey(String key) {
        key = key.strip();
        if (key.isEmpty())
            throw new IllegalArgumentException("key is empty");
        if (key.charAt(0) == '#') {
            key = key.substring(1).strip();
            if (key.isEmpty())
                throw new IllegalArgumentException("key is empty");
            return key;
        } else {
            return currentKeyPrefix + key;
        }
    }

    public static void setMod(@Nullable String modId, @Nullable String keyPrefix, @Nullable String categoryPrefix) {
        ModInfo.setMod(modId);
        currentKeyPrefix = processPrefix(keyPrefix);
        currentCategoryPrefix = processPrefix(categoryPrefix);
    }

    public static void setCategory(String category) {
        CategoryInfo.setCategory(parseCategory(category));
    }

    /**
     * Smaller values are further up
     */
    public static void setIndex(double index) {
        CategoryInfo.setIndex(index);
    }

    /**
     * The category needs to exist, otherwise nothing will happen
     */
    public static void moveAfter(String category) {
        Double index = CategoryInfo.getIndex(parseCategory(category));
        if (index != null)
            CategoryInfo.setIndex(index + 0.01d);
    }

    /**
     * The category needs to exist, otherwise nothing will happen
     */
    public static void moveBefore(String category) {
        Double index = CategoryInfo.getIndex(parseCategory(category));
        if (index != null)
            CategoryInfo.setIndex(index - 0.01d);
    }

    public static void addId(String id) {
        CategoryInfo.addId(id);
        ModInfo.addId(id);
    }

    public static void add(String id, String category, ModContainer mod) {
        CategoryInfo.add(id, category);
        ModInfo.add(id, mod);
    }
}
