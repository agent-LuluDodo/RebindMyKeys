package de.luludodo.rebindmykeys.update;

import net.minecraft.text.Text;

public interface Binding {
    boolean isActive();
    String getId();
    Text getTranslation();
}
