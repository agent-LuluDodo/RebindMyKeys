package de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget.sort;

import net.minecraft.util.Identifier;

public enum SortOrder { // TODO: translations not needed turn into Identifiers for textures
    ASCENDING("arrow_down"),
    DESCENDING("arrow_up");

    private final Identifier icon;
    SortOrder(String iconKey) {
        this.icon = Identifier.of("rebindmykeys", "textures/gui/" + iconKey + ".png");
    }

    public Identifier getIcon() {
        return icon;
    }
}
