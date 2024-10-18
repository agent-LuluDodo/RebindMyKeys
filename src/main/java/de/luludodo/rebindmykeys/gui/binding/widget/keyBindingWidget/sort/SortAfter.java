package de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget.sort;

import net.minecraft.text.Text;

public enum SortAfter {
    CATEGORY("category"),
    MOD("mod"),
    NAME("name");

    private final String translationKey;
    SortAfter(String translationKey) {
        this.translationKey = "rebindmykeys.gui.keyBindings.sort." + translationKey;
    }

    public Text getTranslation() {
        return Text.translatable(translationKey);
    }
}
