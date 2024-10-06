package de.luludodo.rebindmykeys.gui.screen;

import de.luludodo.rebindmykeys.gui.widget.Resizable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class ResizableScreen extends Screen {
    private final List<Resizable> resizables = new ArrayList<>();
    protected ResizableScreen(Text title) {
        super(title);
    }

    protected <T extends Element & Resizable & Drawable & Selectable> T addResizableChild(T resizeableElement) {
        return addDrawableChild(addResizable(resizeableElement));
    }

    public void init(MinecraftClient client, int width, int height) {
        super.init(client, width, height);
    }

    protected <T extends Resizable> T addResizable(T resizable) {
        resizables.add(resizable);
        return resizable;
    }

    @Override
    public void clearChildren() {
        super.clearChildren();
        resizables.clear();
    }

    @Override
    public void resize(MinecraftClient client, final int width, final int height) {
        this.width = width;
        this.height = height;
        resizables.forEach(resizable -> resizable.resize(width, height));
    }

    public int getDefaultResizeWidth() {
        return width;
    }

    public int getDefaultResizeHeight() {
        return height;
    }
}
