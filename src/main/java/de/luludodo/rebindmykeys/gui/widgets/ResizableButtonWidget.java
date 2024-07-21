package de.luludodo.rebindmykeys.gui.widgets;

import de.luludodo.rebindmykeys.gui.widgets.resizable.HeightCalculator;
import de.luludodo.rebindmykeys.gui.widgets.resizable.WidthCalculator;
import de.luludodo.rebindmykeys.gui.widgets.resizable.XCalculator;
import de.luludodo.rebindmykeys.gui.widgets.resizable.YCalculator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class ResizableButtonWidget extends ButtonWidget implements Resizable {

    private final Screen parent;
    private final XCalculator x;
    private final YCalculator y;
    private final WidthCalculator width;
    private final HeightCalculator height;
    protected ResizableButtonWidget(Screen parent, XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x.calc(parent.width), y.calc(parent.height), width.calc(parent.width), height.calc(parent.height), message, onPress, narrationSupplier);
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

    @Environment(value= EnvType.CLIENT)
    public static class Builder {
        private final Screen parent;
        private final Text message;
        private final PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private XCalculator x = width -> 0;
        private YCalculator y = height -> 0;
        private WidthCalculator width = width -> 150;
        private HeightCalculator height = height -> 20;
        private NarrationSupplier narrationSupplier = DEFAULT_NARRATION_SUPPLIER;

        public Builder(Screen parent, Text message, PressAction onPress) {
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

    public static Builder builder(Screen parent, Text message, PressAction onPress) {
        return new Builder(parent, message, onPress);
    }
}
