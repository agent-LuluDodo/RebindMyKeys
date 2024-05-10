package de.luludodo.rebindmykeys.gui;

import de.luludodo.rebindmykeys.gui.widgets.KeyBindingsWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class KeyBindingScreen extends Screen {
    private final Screen parent;
    public KeyBindingScreen(Screen parent) {
        super(Text.literal("Test"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addDrawableChild(new KeyBindingsWidget(this, client));
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
