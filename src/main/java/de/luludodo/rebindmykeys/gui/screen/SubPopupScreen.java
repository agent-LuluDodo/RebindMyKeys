package de.luludodo.rebindmykeys.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public abstract class SubPopupScreen extends PopupScreen {
    protected SubPopupScreen(PopupScreen parent, Text title) {
        super(parent, title);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        getParent().renderBackground(context, -1, -1, delta);
    }
}
