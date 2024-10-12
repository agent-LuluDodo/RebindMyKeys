package de.luludodo.rebindmykeys.gui.widget;

import de.luludodo.rebindmykeys.gui.keyCombo.widget.KeyComboWidget;
import de.luludodo.rebindmykeys.gui.screen.ResizableScreen;
import de.luludodo.rebindmykeys.gui.widget.resizable.HeightCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.WidthCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.XCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.YCalculator;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ResizableTextFieldWidget extends TextFieldWidget implements Resizable, KeyComboWidget.KeyEntry.Button {
    private final Screen parent;
    private final XCalculator x;
    private final YCalculator y;
    private final WidthCalculator width;
    private final HeightCalculator height;
    private String originalText = "";
    public ResizableTextFieldWidget(TextRenderer textRenderer, @Nullable ResizableScreen parent, XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height, Text text) {
        super(
                textRenderer,
                x.calc(parent == null ? -1 : parent.getDefaultResizeWidth()),
                y.calc(parent == null ? -1 : parent.getDefaultResizeHeight()),
                width.calc(parent == null ? -1 : parent.getDefaultResizeWidth()),
                height.calc(parent == null ? -1 : parent.getDefaultResizeHeight()),
                text
        );
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parent = parent;
    }

    public ResizableTextFieldWidget(TextRenderer textRenderer, @Nullable ResizableScreen parent, XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height, @Nullable TextFieldWidget copyFrom, Text text) {
        super(
                textRenderer,
                x.calc(parent == null ? -1 : parent.getDefaultResizeWidth()),
                y.calc(parent == null ? -1 : parent.getDefaultResizeHeight()),
                width.calc(parent == null ? -1 : parent.getDefaultResizeWidth()),
                height.calc(parent == null ? -1 : parent.getDefaultResizeHeight()),
                copyFrom,
                text
        );
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parent = parent;
    }

    @Contract(pure = true)
    public Screen getParent() {
        return parent;
    }

    @Override
    public void resize(int totalWidth, int totalHeight) {
        setX(x.calc(totalWidth));
        setY(y.calc(totalHeight));
        setWidth(width.calc(totalWidth));
        setHeight(height.calc(totalHeight));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        render(context, mouseX, mouseY, delta);
    }

    @Override
    public void setText(String text) {
        originalText = text;
        super.setText(text);
    }

    @Override
    public boolean hasChanges() {
        return !Objects.equals(originalText, getText());
    }

    @Override
    public void onSave() {
        originalText = getText();
    }
}
