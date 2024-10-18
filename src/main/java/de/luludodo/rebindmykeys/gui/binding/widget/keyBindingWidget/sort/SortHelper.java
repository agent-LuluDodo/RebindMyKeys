package de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget.sort;

import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.keybindings.info.CategoryInfo;
import de.luludodo.rebindmykeys.keybindings.info.ModInfo;
import de.luludodo.rebindmykeys.keybindings.info.VanillaKeyBinding;
import de.luludodo.rebindmykeys.keybindings.info.VanillaKeyBindingInfo;
import de.luludodo.rebindmykeys.util.MapUtil;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings("RedundantTypeArguments")
public class SortHelper {
    public static final Comparator<String> SORT_CATEGORY_ASCENDING = Comparator.comparingDouble(SortHelper::getCategoryOrder);
    public static final Comparator<String> SORT_CATEGORY_DESCENDING = Comparator.<String>comparingDouble(SortHelper::getCategoryOrder).reversed();
    private static final Comparator<ModContainer> SORT_MOD_ASCENDING = Comparator.comparing(ModInfo::getModName, String::compareToIgnoreCase);
    private static final Comparator<ModContainer> SORT_MOD_DESCENDING = Comparator.comparing(ModInfo::getModName, String::compareToIgnoreCase).reversed();
    private static final Comparator<KeyBindingOption> SORT_NAME_ASCENDING = Comparator.comparing(s -> Text.translatable(s.getId()).getString(), String::compareToIgnoreCase);
    private static final Comparator<KeyBindingOption> SORT_NAME_DESCENDING = Comparator.<KeyBindingOption, String>comparing(s -> Text.translatable(s.getId()).getString(), String::compareToIgnoreCase).reversed();

    private static Double getCategoryOrder(String category) {
        return Objects.requireNonNullElseGet(CategoryInfo.getOrder().get(category), () -> (double) net.minecraft.client.option.KeyBinding.CATEGORY_ORDER_MAP.get(category));
    }

    public static class KeyBindingOption {
        private final KeyBinding luluBinding;
        private final VanillaKeyBinding vanillaBinding;
        private final String id;
        private final String category;
        private final ModContainer mod;
        private final boolean isVanilla;
        KeyBindingOption(KeyBinding binding) {
            luluBinding = binding;
            id = binding.getId();
            category = CategoryInfo.getCategory(id);
            mod = ModInfo.getMod(id);

            vanillaBinding = null;
            isVanilla = false;
        }

        KeyBindingOption(VanillaKeyBinding binding) {
            vanillaBinding = binding;
            id = binding.getId();
            category = binding.vanilla().getCategory();
            mod = binding.mod();

            luluBinding = null;
            isVanilla = true;
        }

        public boolean isVanilla() {
            return isVanilla;
        }

        public KeyBinding getLuluBinding() {
            if (luluBinding == null)
                throw new UnsupportedOperationException();

            return luluBinding;
        }

        public VanillaKeyBinding getVanillaBinding() {
            if (vanillaBinding == null)
                throw new UnsupportedOperationException();

            return vanillaBinding;
        }

        public String getId() {
            return id;
        }

        public String getCategory() {
            return category;
        }

        public ModContainer getMod() {
            return mod;
        }

        public <E> E convert(Function<KeyBinding, E> lulu, Function<VanillaKeyBinding, E> vanilla) {
            if (isVanilla()) {
                return vanilla.apply(getVanillaBinding());
            } else {
                return lulu.apply(getLuluBinding());
            }
        }
    }

    public static Map<String, List<KeyBindingOption>> getCategories(SortOrder order) {
        Map<String, List<KeyBindingOption>> categories = new HashMap<>();
        for (KeyBindingOption option : getBaseSort(SortOrder.ASCENDING)) {
            String category = option.getCategory();
            if (!categories.containsKey(category))
                categories.put(category, new ArrayList<>());
            categories.get(category).add(option);
        }
        return MapUtil.sortByKey(
                categories,
                order == SortOrder.ASCENDING ?
                        SORT_CATEGORY_ASCENDING :
                        SORT_CATEGORY_DESCENDING
        );
    }

    public static Map<ModContainer, List<KeyBindingOption>> getMods(SortOrder order) {
        Map<ModContainer, List<KeyBindingOption>> mods = new HashMap<>();
        for (KeyBindingOption option : getBaseSort(SortOrder.ASCENDING)) {
            ModContainer mod = option.getMod();
            if (!mods.containsKey(mod))
                mods.put(mod, new ArrayList<>());
            mods.get(mod).add(option);
        }
        return MapUtil.sortByKey(
                mods,
                order == SortOrder.ASCENDING ?
                        SORT_MOD_ASCENDING :
                        SORT_MOD_DESCENDING
        );
    }

    public static List<KeyBindingOption> getNames(SortOrder order) {
        return getBaseSort(order);
    }

    private static List<KeyBindingOption> getBaseSort(SortOrder order) {
        Set<String> addedIds = new HashSet<>();
        List<KeyBindingOption> all = new ArrayList<>();
        for (KeyBinding binding : KeyBinding.getAll()) {
            addedIds.add(binding.getId());
            all.add(new KeyBindingOption(binding));
        }
        for (VanillaKeyBinding binding : VanillaKeyBindingInfo.getVanillaKeybindings()) {
            if (addedIds.contains(binding.getId()))
                continue;

            all.add(new KeyBindingOption(binding));
        }
        all.sort(order == SortOrder.ASCENDING ? SORT_NAME_ASCENDING : SORT_NAME_DESCENDING);
        return all;
    }
}
