package de.luludodo.rebindmykeys.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import de.luludodo.rebindmykeys.util.RenderUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

public class IconButtonWidget extends ButtonWidget {
    public static Builder builder(Identifier icon, PressAction onPress) {
        return new Builder(icon, onPress);
    }

    private final Identifier icon;
    private final int iconX;
    private final int iconY;
    private final int iconWidth;
    private final int iconHeight;
    private final int u;
    private final int v;
    private final int textureWidth;
    private final int textureHeight;
    protected IconButtonWidget(int x, int y, int width, int height, Identifier icon, int iconX, int iconY, int iconWidth, int iconHeight, int u, int v, int textureWidth, int textureHeight, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, Text.empty(), onPress, narrationSupplier);
        this.icon = icon;
        this.iconX = iconX;
        this.iconY = iconY;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        this.u = u;
        this.v = v;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
        RenderUtil.setShaderColor(context, color);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawTexture(this.icon, getX() + iconX, getY() + iconY, u, v, iconWidth, iconHeight, textureWidth, textureHeight);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Environment(value= EnvType.CLIENT)
    public static class Builder {
        private final Identifier icon;
        private final PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 20;
        private int height = 20;
        private int iconX = 1;
        private int iconY = 1;
        private Integer iconWidth = null;
        private Integer iconHeight = null;
        private int u = 0;
        private int v = 0;
        private Integer textureWidth = null;
        private Integer textureHeight = null;
        private NarrationSupplier narrationSupplier = DEFAULT_NARRATION_SUPPLIER;

        public Builder(Identifier icon, PressAction onPress) {
            this.icon = icon;
            this.onPress = onPress;
        }

        public Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder dimensions(int x, int y, int width, int height) {
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

        public Builder iconPosition(int x, int y) {
            iconX = x;
            iconY = y;
            return this;
        }

        public Builder iconSize(int width, int height) {
            iconWidth = width;
            iconHeight = height;
            return this;
        }

        public Builder iconDimensions(int x, int y, int width, int height) {
            return iconPosition(x, y).iconSize(width, height);
        }

        public Builder icon(int u, int v, int textureWidth, int textureHeight) {
            this.u = u;
            this.v = v;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        public IconButtonWidget build() {
            if (iconWidth == null) iconWidth = width - 2;
            if (iconHeight == null) iconHeight = width - 2;
            if (textureWidth == null) textureWidth = iconWidth;
            if (textureHeight == null) textureHeight = iconHeight;
            IconButtonWidget buttonWidget = new IconButtonWidget(x, y, width, height, icon, iconX, iconY, iconWidth, iconHeight, u, v, textureWidth, textureHeight, onPress, narrationSupplier);
            buttonWidget.setTooltip(this.tooltip);
            return buttonWidget;
        }
    }
}
