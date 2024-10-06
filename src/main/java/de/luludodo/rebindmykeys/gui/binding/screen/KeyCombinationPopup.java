package de.luludodo.rebindmykeys.gui.binding.screen;

import de.luludodo.rebindmykeys.gui.screen.PopupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class KeyCombinationPopup extends PopupScreen {
    public KeyCombinationPopup(@Nullable Screen parent) {
        super(parent, Text.translatable("rebindmykeys.gui.keyCombination.title"));
    }
}
