package de.luludodo.rebindmykeys.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class IconButtonWidget extends ResizableButtonWidget {
    public static Builder builder(ResizableScreen parent, Identifier icon, PressAction onPress) {
        return new Builder(parent, icon, onPress);
    }
    public static Builder builder(Identifier icon, PressAction onPress) {
        return new Builder(null, icon, onPress);
    }

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
    protected IconButtonWidget(@Nullable ResizableScreen parent, @NotNull XCalculator x, @NotNull YCalculator y, @NotNull WidthCalculator width, @NotNull HeightCalculator height, Identifier icon, @NotNull XCalculator iconX, @NotNull YCalculator iconY, @NotNull WidthCalculator iconWidth, @NotNull HeightCalculator iconHeight, int u, int v, int textureWidth, int textureHeight, boolean renderBackground, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(parent, x, y, width, height, Text.empty(), onPress, narrationSupplier);
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

    public void setIcon(Identifier icon) {
        this.icon = icon;
    }

    public void setTextureSize(int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (renderBackground) {
            super.renderWidget(context, mouseX, mouseY, delta);
        } else {
            drawMessage(context, null, (this.active ? (isSelected() ? 0xAAAAAA : 0xFFFFFF) : 0xA0A0A0) | MathHelper.ceil(this.alpha * 255.0f) << 24);
        }
    }

    @Override
    public void drawMessage(DrawContext context, @Nullable TextRenderer textRenderer, int color) {
        RenderUtil.setShaderColor(context, color);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawTexture(this.icon, getX() + iconX.calc(getWidth()), getY() + iconY.calc(getHeight()), u, v, iconWidth.calc(getWidth()), iconHeight.calc(getHeight()), textureWidth, textureHeight);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @SuppressWarnings("unused")
    @Environment(value= EnvType.CLIENT)
    public static class Builder {
        private final @Nullable ResizableScreen parent;
        private final Identifier icon;
        private final PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private XCalculator x = x -> 0;
        private YCalculator y = y -> 0;
        private WidthCalculator width = width -> 20;
        private HeightCalculator height = height -> 20;
        private XCalculator iconX = width -> 1;
        private YCalculator iconY = height -> 1;
        private WidthCalculator iconWidth = width -> width - 2;
        private HeightCalculator iconHeight = height -> height - 2;
        private int u = 0;
        private int v = 0;
        private int textureWidth = 18;
        private int textureHeight = 18;
        private boolean renderBackground = true;
        private NarrationSupplier narrationSupplier = DEFAULT_NARRATION_SUPPLIER;

        public Builder(@Nullable ResizableScreen parent, Identifier icon, PressAction onPress) {
            this.parent = parent;
            this.icon = icon;
            this.onPress = onPress;
        }

        public Builder position(int x, int y) {
            this.x = w -> x;
            this.y = h -> y;
            return this;
        }

        public Builder position(XCalculator x, YCalculator y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = w -> width;
            this.height = h -> height;
            return this;
        }

        public Builder size(WidthCalculator width, HeightCalculator height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder dimensions(int x, int y, int width, int height) {
            return position(x, y).size(width, height);
        }

        public Builder dimensions(XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height) {
            return position(x, y).size(width, height);
        }

        public Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder narrationSupplier(NarrationSupplier narrationSupplier) {
            this.narrationSupplier = narrationSupplier;
            return this;
        }

        public Builder iconPosition(int x, int y) {
            iconX = w -> x;
            iconY = h -> y;
            return this;
        }

        public Builder iconPosition(XCalculator x, YCalculator y) {
            iconX = x;
            iconY = y;
            return this;
        }

        public Builder iconSize(int width, int height) {
            iconWidth = w -> width;
            iconHeight = h -> height;
            return this;
        }

        public Builder iconSize(WidthCalculator width, HeightCalculator height) {
            iconWidth = width;
            iconHeight = height;
            return this;
        }

        public Builder iconDimensions(int x, int y, int width, int height) {
            return iconPosition(x, y).iconSize(width, height);
        }

        public Builder iconDimensions(XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height) {
            return iconPosition(x, y).iconSize(width, height);
        }

        public Builder iconUV(int u, int v) {
            this.u = u;
            this.v = v;
            return this;
        }

        public Builder iconTextureSize(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        public Builder icon(int u, int v, int textureWidth, int textureHeight) {
            return iconUV(u, v).iconTextureSize(textureWidth, textureHeight);
        }

        public Builder setRenderBackground(boolean renderBackground) {
            this.renderBackground = renderBackground;
            return this;
        }

        public IconButtonWidget build() {
            IconButtonWidget buttonWidget = new IconButtonWidget(parent, x, y, width, height, icon, iconX, iconY, iconWidth, iconHeight, u, v, textureWidth, textureHeight, renderBackground, onPress, narrationSupplier);
            buttonWidget.setTooltip(this.tooltip);
            return buttonWidget;
        }
    }
}
