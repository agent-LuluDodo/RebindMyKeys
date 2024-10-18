package de.luludodo.rebindmykeys.gui.widget;

import de.luludodo.rebindmykeys.gui.keyCombo.widget.KeyComboWidget;
import de.luludodo.rebindmykeys.gui.screen.ResizableScreen;
import de.luludodo.rebindmykeys.gui.widget.resizable.HeightCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.WidthCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.XCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.YCalculator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ResizableButtonWidget extends ButtonWidget implements Resizable, KeyComboWidget.KeyEntry.Button {
    private final Screen parent;
    private final XCalculator x;
    private final YCalculator y;
    private final WidthCalculator width;
    private final HeightCalculator height;
    private boolean hasChanges = false;
    protected ResizableButtonWidget(@Nullable ResizableScreen parent, XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(
                x.calc(parent == null ? -1 : parent.getDefaultResizeWidth()),
                y.calc(parent == null ? -1 : parent.getDefaultResizeHeight()),
                width.calc(parent == null ? -1 : parent.getDefaultResizeWidth()),
                height.calc(parent == null ? -1 : parent.getDefaultResizeHeight()),
                message, onPress, narrationSupplier
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
    public void onPress() {
        hasChanges = true;
        super.onPress();
    }

    @Override
    public boolean hasChanges() {
        return hasChanges;
    }

    @Override
    public void setNoChanges() {
        hasChanges = false;
    }

    @SuppressWarnings("unused")
    @Environment(value= EnvType.CLIENT)
    public static class Builder {
        @Nullable private final ResizableScreen parent;
        private final Text message;
        private final PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private XCalculator x = width -> 0;
        private YCalculator y = height -> 0;
        private WidthCalculator width = width -> 150;
        private HeightCalculator height = height -> 20;
        private NarrationSupplier narrationSupplier = DEFAULT_NARRATION_SUPPLIER;

        public Builder(@Nullable ResizableScreen parent, Text message, PressAction onPress) {
            this.parent = parent;
            this.message = message;
            this.onPress = onPress;
        }

        public Builder position(XCalculator x, YCalculator y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(WidthCalculator width) {
            this.width = width;
            return this;
        }

        public Builder size(WidthCalculator width, HeightCalculator height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder dimensions(XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height) {
            return this.position(x, y).size(width, height);
        }

        public Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder narrationSupplier(NarrationSupplier narrationSupplier) {
            this.narrationSupplier = narrationSupplier;
            return this;
        }

        public ResizableButtonWidget build() {
            ResizableButtonWidget buttonWidget = new ResizableButtonWidget(parent, this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier);
            buttonWidget.setTooltip(this.tooltip);
            return buttonWidget;
        }
    }

    public static Builder builder(@Nullable ResizableScreen parent, Text message, PressAction onPress) {
        return new Builder(parent, message, onPress);
    }
}
