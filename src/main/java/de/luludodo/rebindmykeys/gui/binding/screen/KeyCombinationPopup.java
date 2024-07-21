package de.luludodo.rebindmykeys.gui.binding.screen;

import de.luludodo.rebindmykeys.gui.screen.PopupScreen;
import de.luludodo.rebindmykeys.gui.screen.SubPopupScreen;
import net.minecraft.text.Text;

public class KeyCombinationPopup extends SubPopupScreen {
    public KeyCombinationPopup(PopupScreen parent) {
        super(parent, Text.translatable("rebindmykeys.gui.keyCombination.title"));
    }
}
