package de.luludodo.rebindmykeys.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import de.luludodo.rebindmykeys.gui.keyCombo.widget.KeyComboWidget;
import de.luludodo.rebindmykeys.gui.screen.ResizableScreen;
import de.luludodo.rebindmykeys.gui.widget.resizable.HeightCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.WidthCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.XCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.YCalculator;
import de.luludodo.rebindmykeys.util.RenderUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class CyclingIconButtonWidget<T> extends ResizableCyclingButtonWidget<T> {
    private static final List<Boolean> BOOLEAN_VALUES = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);

    private final Function<T, Identifier> valueToIcon;
    private T oldValue;
    private Identifier icon;
    private final XCalculator iconX;
    private final YCalculator iconY;
    private final WidthCalculator iconWidth;
    private final HeightCalculator iconHeight;
    private int u;
    private int v;
    private int textureWidth;
    private int textureHeight;
    private final boolean renderBackground;
    CyclingIconButtonWidget(@Nullable ResizableScreen parent, XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height, Identifier icon, @NotNull XCalculator iconX, @NotNull YCalculator iconY, @NotNull WidthCalculator iconWidth, @NotNull HeightCalculator iconHeight, int u, int v, int textureWidth, int textureHeight, boolean renderBackground, int index, T value, Values<T> values, Function<T, Identifier> valueToIcon, Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory, UpdateCallback<T> callback, SimpleOption.TooltipFactory<T> tooltipFactory) {
        super(parent, x, y, width, height, Text.empty(), Text.empty(), index, value, values, o -> Text.empty(), narrationMessageFactory, callback, tooltipFactory, true);
        this.valueToIcon = valueToIcon;
        this.oldValue = value;
        this.icon = icon;
        this.iconX = iconX;
        this.iconY = iconY;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        this.u = u;
        this.v = v;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.renderBackground = renderBackground;
    }

    public void setUV(int u, int v) {
        this.u = u;
        this.v = v;
    }

    public void setTextureSize(int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void resize(int totalWidth, int totalHeight) {
        super.resize(totalWidth, totalHeight);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (renderBackground) {
            super.renderWidget(context, mouseX, mouseY, delta);
        } else {
            drawMessage(context, null, (this.active ? (isSelected() ? 0xAAAAAA : 0xFFFFFF) : 0xA0A0A0) | MathHelper.ceil(this.alpha * 255.0f) << 24);
        }
    }

    @Override
    public void drawMessage(DrawContext context, @Nullable TextRenderer textRenderer, int color) {
        T value = getValue();
        if (oldValue != value) {
            icon = valueToIcon.apply(value);
            oldValue = value;
        }
        RenderUtil.setShaderColor(context, color);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawTexture(icon, getX() + iconX.calc(getWidth()), getY() + iconY.calc(getHeight()), u, v, iconWidth.calc(getWidth()), iconHeight.calc(getHeight()), textureWidth, textureHeight);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static <T> Builder<T> iconBuilder(Function<T, Identifier> valueToIcon) {
        return new Builder<>(valueToIcon);
    }

    public static Builder<Boolean> iconOnOffBuilder(Identifier on, Identifier off) {
        return new Builder<Boolean>((value) -> value ? on : off).values(BOOLEAN_VALUES);
    }

    @Environment(EnvType.CLIENT)
    public static class Builder<T> {
        private int initialIndex;
        @Nullable
        private T value;
        private final Function<T, Identifier> valueToIcon;
        private SimpleOption.TooltipFactory<T> tooltipFactory = (value) -> {
            return null;
        };
        private Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory = CyclingButtonWidget::getGenericNarrationMessage;
        private Values<T> values = Values.of(ImmutableList.of());
        private XCalculator iconX = width -> 1;
        private YCalculator iconY = height -> 1;
        private WidthCalculator iconWidth = width -> width - 2;
        private HeightCalculator iconHeight = height -> height - 2;
        private int u = 0;
        private int v = 0;
        private int textureWidth = 18;
        private int textureHeight = 18;
        private boolean renderBackground = true;

        public Builder(Function<T, Identifier> valueToIcon) {
            this.valueToIcon = valueToIcon;
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

        public Builder<T> iconPosition(int x, int y) {
            iconX = w -> x;
            iconY = h -> y;
            return this;
        }

        public Builder<T> iconPosition(XCalculator x, YCalculator y) {
            iconX = x;
            iconY = y;
            return this;
        }

        public Builder<T> iconSize(int width, int height) {
            iconWidth = w -> width;
            iconHeight = h -> height;
            return this;
        }

        public Builder<T> iconSize(WidthCalculator width, HeightCalculator height) {
            iconWidth = width;
            iconHeight = height;
            return this;
        }

        public Builder<T> iconDimensions(int x, int y, int width, int height) {
            return iconPosition(x, y).iconSize(width, height);
        }

        public Builder<T> iconDimensions(XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height) {
            return iconPosition(x, y).iconSize(width, height);
        }

        public Builder<T> iconUV(int u, int v) {
            this.u = u;
            this.v = v;
            return this;
        }

        public Builder<T> iconTextureSize(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        public Builder<T> icon(int u, int v, int textureWidth, int textureHeight) {
            return iconUV(u, v).iconTextureSize(textureWidth, textureHeight);
        }

        public Builder<T> setRenderBackground(boolean renderBackground) {
            this.renderBackground = renderBackground;
            return this;
        }

        public CyclingIconButtonWidget<T> build(@Nullable ResizableScreen parent, XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height) {
            return this.build(parent, x, y, width, height, (button, value) -> {
            });
        }

        public CyclingIconButtonWidget<T> build(@Nullable ResizableScreen parent, XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height, UpdateCallback<T> callback) {
            List<T> list = this.values.getDefaults();
            if (list.isEmpty()) {
                throw new IllegalStateException("No values for cycle button");
            } else {
                T object = this.value != null ? this.value : list.get(this.initialIndex);
                Identifier icon = this.valueToIcon.apply(object);
                return new CyclingIconButtonWidget<>(parent, x, y, width, height, icon, iconX, iconY, iconWidth, iconHeight, u, v, textureWidth, textureHeight, renderBackground, this.initialIndex, object, this.values, this.valueToIcon, this.narrationMessageFactory, callback, this.tooltipFactory);
            }
        }
    }
}
