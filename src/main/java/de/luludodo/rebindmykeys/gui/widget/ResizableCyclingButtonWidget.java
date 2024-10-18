package de.luludodo.rebindmykeys.gui.widget;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class ResizableCyclingButtonWidget<T> extends CyclingButtonWidget<T> implements Resizable, KeyComboWidget.KeyEntry.Button {
    private static final Function<Integer, Double> SIZE_TO_WIDTH_PERCENT = count -> {
        if (count == 1) {
            return 0.33d;
        } else if (count == 2) {
            return 0.5d;
        } else if (count == 3) {
            return 0.66d;
        } else if (count >= 4) {
            return 0.75d;
        } else {
            return -1d;
        }
    };
    private static final int GAP_SIZE = 2;
    private static final List<Boolean> BOOLEAN_VALUES;
    private final Values<T> values;

    private final Screen parent;
    private final XCalculator x;
    private final YCalculator y;
    private final WidthCalculator width;
    private final HeightCalculator height;

    private T originalValue;
    ResizableCyclingButtonWidget(@Nullable ResizableScreen parent, XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height, Text message, Text optionText, int index, T value, Values<T> values, Function<T, Text> valueToText, Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory, UpdateCallback<T> callback, SimpleOption.TooltipFactory<T> tooltipFactory, boolean optionTextOmitted) {
        super(
                x.calc(parent == null ? -1 : parent.getDefaultResizeWidth()),
                y.calc(parent == null ? -1 : parent.getDefaultResizeHeight()),
                width.calc(parent == null ? -1 : parent.getDefaultResizeWidth()),
                height.calc(parent == null ? -1 : parent.getDefaultResizeHeight()),
                message, optionText, index, value, values, valueToText, narrationMessageFactory, callback, tooltipFactory, optionTextOmitted
        );
        this.values = values;

        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.originalValue = value;
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
    public boolean hasChanges() {
        return originalValue != getValue();
    }

    @Override
    public void setNoChanges() {
        originalValue = getValue();
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        double size = values.getCurrent().size();
        if (size == 0)
            return;
        double width = Math.round(getWidth() * SIZE_TO_WIDTH_PERCENT.apply((int)size));
        double startX = getX() + (getWidth() - width) / 2d;
        double elementWidth = (width - GAP_SIZE * (size - 1)) / size;
        int y = getY() + getHeight() - 2;
        int index = values.getCurrent().indexOf(getValue());
        double curX = startX;
        for (int i = 0; i < size; i++) {
            context.fill((int)Math.round(curX), y, (int)Math.round(curX + elementWidth), y + 1, (this.active ? (i == index ? 0xFFFFFF : 0xAAAAAA) : 0xA0A0A0) | MathHelper.ceil(this.alpha * 255.0f) << 24);
            curX += elementWidth + GAP_SIZE;
        }
    }

    public static <T> Builder<T> luluBuilder(Function<T, Text> valueToText) {
        return new Builder<>(valueToText);
    }

    public static Builder<Boolean> luluOnOffBuilder(Text on, Text off) {
        return new Builder<Boolean>((value) -> value ? on : off).values(BOOLEAN_VALUES);
    }

    public static Builder<Boolean> luluOnOffBuilder() {
        return new Builder<Boolean>((value) -> value ? ScreenTexts.ON : ScreenTexts.OFF).values(BOOLEAN_VALUES);
    }

    public static Builder<Boolean> luluOnOffBuilder(boolean initialValue) {
        return luluOnOffBuilder().initially(initialValue);
    }

    static {
        BOOLEAN_VALUES = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
    }

    @Environment(EnvType.CLIENT)
    public static class Builder<T> {
        private int initialIndex;
        @Nullable
        private T value;
        private final Function<T, Text> valueToText;
        private SimpleOption.TooltipFactory<T> tooltipFactory = (value) -> {
            return null;
        };
        private Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory = CyclingButtonWidget::getGenericNarrationMessage;
        private Values<T> values = Values.of(ImmutableList.of());
        private boolean optionTextOmitted;

        public Builder(Function<T, Text> valueToText) {
            this.valueToText = valueToText;
        }

        public Builder<T> values(Collection<T> values) {
            return this.values(Values.of(values));
        }

        @SafeVarargs
        public final Builder<T> values(T... values) {
            return this.values(ImmutableList.copyOf(values));
        }

        public Builder<T> values(List<T> defaults, List<T> alternatives) {
            return this.values(Values.of(CyclingButtonWidget.HAS_ALT_DOWN, defaults, alternatives));
        }

        public Builder<T> values(BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
            return this.values(Values.of(alternativeToggle, defaults, alternatives));
        }

        public Builder<T> values(Values<T> values) {
            this.values = values;
            return this;
        }

        public Builder<T> tooltip(SimpleOption.TooltipFactory<T> tooltipFactory) {
            this.tooltipFactory = tooltipFactory;
            return this;
        }

        public Builder<T> initially(T value) {
            this.value = value;
            int i = this.values.getDefaults().indexOf(value);
            if (i != -1) {
                this.initialIndex = i;
            }

            return this;
        }

        public Builder<T> narration(Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory) {
            this.narrationMessageFactory = narrationMessageFactory;
            return this;
        }

        public Builder<T> omitKeyText() {
            this.optionTextOmitted = true;
            return this;
        }

        public ResizableCyclingButtonWidget<T> build(@Nullable ResizableScreen parent, XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height, Text optionText) {
            return this.build(parent, x, y, width, height, optionText, (button, value) -> {
            });
        }

        public ResizableCyclingButtonWidget<T> build(@Nullable ResizableScreen parent, XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height, Text optionText, UpdateCallback<T> callback) {
            List<T> list = this.values.getDefaults();
            if (list.isEmpty()) {
                throw new IllegalStateException("No values for cycle button");
            } else {
                T object = this.value != null ? this.value : list.get(this.initialIndex);
                Text text = this.valueToText.apply(object);
                Text text2 = this.optionTextOmitted ? text : ScreenTexts.composeGenericOptionText(optionText, text);
                return new ResizableCyclingButtonWidget<T>(parent, x, y, width, height, text2, optionText, this.initialIndex, object, this.values, this.valueToText, this.narrationMessageFactory, callback, this.tooltipFactory, this.optionTextOmitted);
            }
        }
    }
}
