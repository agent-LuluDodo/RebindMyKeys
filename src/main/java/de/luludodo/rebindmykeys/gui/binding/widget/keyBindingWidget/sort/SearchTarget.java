package de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget.sort;

import net.minecraft.text.Text;

public enum SearchTarget {
    KEY("key"),
    NAME("name"),
    CATEGORY("category"),
    MOD("mod");

    private final String translationKey;
    SearchTarget(String translationKey) {
        this.translationKey = "rebindmykeys.gui.keyBindings.searchTarget." + translationKey;
    }

    public Text getTranslation() {
        return Text.translatable(translationKey);
    }
}
