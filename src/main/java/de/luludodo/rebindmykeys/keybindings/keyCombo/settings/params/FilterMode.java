package de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params;

import net.minecraft.text.Text;

public enum FilterMode {
    ALL("all", true, true),
    SELF("self", true, false),
    OTHERS("others", false, true),
    NONE("none", false, false);

    private final String translationKey;
    private final boolean self;
    private final boolean others;
    FilterMode(String id, boolean self, boolean others) {
        translationKey = "rebindmykeys.gui.settings.filter." + id;
        this.self = self;
        this.others = others;
    }

    public Text getTranslation() {
        return Text.translatable(translationKey);
    }

    public boolean self() {
        return self;
    }

    public boolean others() {
        return others;
    }
}
