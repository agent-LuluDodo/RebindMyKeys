package de.luludodo.rebindmykeys.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.luludodo.rebindmykeys.gui.widgets.resizable.HeightCalculator;
import de.luludodo.rebindmykeys.gui.widgets.resizable.WidthCalculator;
import de.luludodo.rebindmykeys.gui.widgets.resizable.XCalculator;
import de.luludodo.rebindmykeys.gui.widgets.resizable.YCalculator;
import de.luludodo.rebindmykeys.util.RenderUtil;
import de.luludodo.rebindmykeys.util.ScissorUtil;
import de.luludodo.rebindmykeys.util.enums.Mouse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public abstract class PopupScreen extends ResizableScreen {
    private final Screen parent;
    protected PopupScreen(@Nullable Screen parent, Text title) {
        super(title);
        this.parent = parent;
        calcDimensions(width, height);
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        calcDimensions(width, height);
        super.init(client, this.width, this.height);
    }

    protected void calcDimensions(final int width, final int height) {
        this.width = widthCalc.calc(width);
        this.height = heightCalc.calc(height);
        x = xCalc.calc(width);
        y = yCalc.calc(height);
    }

    private XCalculator xCalc = width -> width / 2 - this.width / 2;
    private int x;
    public void setX(XCalculator x) {
        xCalc = x;
    }
    @Contract(pure = true)
    public int getX() {
        return x;
    }

    private YCalculator yCalc = height -> height / 2 - this.height / 2;
    private int y;
    public void setY(YCalculator y) {
        yCalc = y;
    }
    @Contract(pure = true)
    public int getY() {
        return y;
    }

    private WidthCalculator widthCalc = width -> 200;
    private int width;
    public void setWidth(WidthCalculator width) {
        widthCalc = width;
    }
    @Contract(pure = true)
    public int getWidth() {
        return width;
    }

    private HeightCalculator heightCalc = height -> 100;
    private int height;
    public void setHeight(HeightCalculator height) {
        heightCalc = height;
    }
    @Contract(pure = true)
    public int getHeight() {
        return height;
    }

    @Contract(pure = true)
    public int getPopupTop() {
        return getY();
    }
    @Contract(pure = true)
    public int getPopupCenterY() {
        return getY() + getHeight() / 2;
    }
    @Contract(pure = true)
    public int getPopupBottom() {
        return getY() + getHeight();
    }

    @Contract(pure = true)
    public int getPopupLeft() {
        return getX();
    }
    @Contract(pure = true)
    public int getPopupCenterX() {
        return getX() + getWidth() / 2;
    }
    @Contract(pure = true)
    public int getPopupRight() {
        return getX() + getWidth();
    }

    public Screen getParent() {
        return parent;
    }

    private boolean closeButtonEnabled = true;
    public void setCloseButtonEnabled(boolean closeButtonEnabled) {
        this.closeButtonEnabled = closeButtonEnabled;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        renderPopup(context, mouseX, mouseY, delta);
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        ScissorUtil.push();
        ScissorUtil.translate(x, y);
        for (Drawable drawable : drawables) {
            drawable.render(context, mouseX - x, mouseY - y, delta);
        }
        ScissorUtil.pop();
        context.getMatrices().pop();
        if (closeButtonEnabled)
            renderCloseButton(context, mouseX, mouseY, delta);
    }

    private static final Identifier CLOSE_BUTTON = Identifier.of("rebindmykeys", "textures/gui/popup_close.png");
    public void renderCloseButton(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtil.setShaderColor(context, closeButtonHovered(mouseX, mouseY)? 0xFFC09090 : 0xFFFFFFFF);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawTexture(CLOSE_BUTTON, getPopupRight() - 16, getPopupTop() + 5, 0, 0, 11, 11, 11, 11);
        context.setShaderColor(1, 1, 1, 1);
    }

    public boolean closeButtonHovered(double mouseX, double mouseY) {
        return Mouse.isHovered(getPopupRight() - 16, getPopupTop() + 5, 11, 11, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX - x, mouseY - y, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX - x, mouseY - y, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX - x, mouseY - y, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (closeButtonEnabled && closeButtonHovered(mouseX, mouseY)) {
            close();
            return true;
        }
        return super.mouseClicked(mouseX - x, mouseY - y, button);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (parent != null) {
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, -100);
            parent.render(context, -1, -1, delta);
            context.fillGradient(0, 0, parent.width, parent.height, 100, 0xC0101010, 0xD0101010);
            context.getMatrices().pop();
        } else {
            int correctWidth = width;
            int correctHeight = height;
            width = MinecraftClient.getInstance().getWindow().getScaledWidth();
            height = MinecraftClient.getInstance().getWindow().getScaledHeight();
            super.renderBackground(context, mouseX, mouseY, delta);
            width = correctWidth;
            height = correctHeight;
        }
    }

    private static final Identifier POPUP = Identifier.of("rebindmykeys", "textures/gui/popup.png");
    public void renderPopup(DrawContext context, int mouseX, int mouseY, float delta) {
        if (width < 8 || height < 8) return;
        RenderUtil.render9PartTexture(
                context, POPUP,
                x, y,
                width, height,
                4, 2
        );
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        calcDimensions(width, height);
        super.resize(client, this.width, this.height);
        if (parent != null) {
            parent.resize(client, width, height);
        }
    }

    @Override
    public void close() {
        client.currentScreen = parent; // screen has already been initialized
    }
}
