package de.luludodo.rebindmykeys.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.luludodo.rebindmykeys.gui.widget.resizable.HeightCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.WidthCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.XCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.YCalculator;
import de.luludodo.rebindmykeys.util.ColorUtil;
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
    private boolean topPopup = true;
    protected PopupScreen(@Nullable Screen parent, Text title) {
        super(title);
        this.parent = parent;

        if (parent instanceof PopupScreen popupParent)
            popupParent.topPopup = false;
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        textRenderer = client.textRenderer; // So that renderTitle can calc its height
        calcDimensions(width, height);
        super.init(client, this.width, this.height);
    }

    protected void calcDimensions(final int width, final int height) {
        calcWidth(width);
        calcHeight(height);
    }

    protected void calcWidth(final int width) {
        this.width = widthCalc.calc(width);
        x = xCalc.calc(width);
    }
    protected void calcHeight(final int height) {
        int heightModifier = (getRenderTitle()? getTitleHeight() : 0);
        this.height = heightCalc.calc(height - heightModifier) + heightModifier;
        y = yCalc.calc(height);
    }

    private XCalculator xCalc = width -> width / 2 - this.width / 2;
    private int x;
    public void setX(XCalculator x) {
        xCalc = x;
    }

    /**
     * <b>Don't use this to render content inside of the popup. Use {@link PopupScreen#getPopupLeft()} for that</b>
     * @return The x position of the Screen for the Popup
     * @see PopupScreen#getPopupLeft()
     * @see PopupScreen#getPopupCenterX()
     * @see PopupScreen#getPopupRight()
     */
    @Contract(pure = true)
    public int getX() {
        return x;
    }

    private YCalculator yCalc = height -> height / 2 - this.height / 2;
    private int y;
    public void setY(YCalculator y) {
        yCalc = y;
    }

    /**
     * <b>Don't use this to render content inside of the popup. Use {@link PopupScreen#getPopupTop()} for that</b>
     * @return The y position of the Screen for the Popup
     * @see PopupScreen#getPopupTop()
     * @see PopupScreen#getPopupCenterY()
     * @see PopupScreen#getPopupBottom()
     */
    @Contract(pure = true)
    public int getY() {
        return y;
    }

    private WidthCalculator widthCalc = width -> 200;
    private int width;
    public void setWidth(WidthCalculator width) {
        widthCalc = width;
    }

    /**
     * <b>Don't use this to render content inside of the popup. Use {@link PopupScreen#getPopupWidth()} for that</b>
     * @return The width of the Screen for the Popup
     * @see PopupScreen#getPopupWidth()
     */
    @Contract(pure = true)
    public int getWidth() {
        return width;
    }

    /**
     * @return The width of the Popup itself use this for rendering stuff inside the Popup.
     * @see PopupScreen#getWidth()
     */
    @Contract(pure = true)
    public int getPopupWidth() {
        return getWidth();
    }

    @Override
    public int getDefaultResizeWidth() {
        return getPopupWidth();
    }

    private HeightCalculator heightCalc = height -> 100;
    private int height;
    public void setHeight(HeightCalculator height) {
        heightCalc = height;
    }

    /**
     * <b>Don't use this to render content inside of the popup. Use {@link PopupScreen#getPopupHeight()} for that</b>
     * @return The height of the Screen for the Popup.
     * @see PopupScreen#getPopupHeight()
     */
    @Contract(pure = true)
    public int getHeight() {
        return height;
    }

    /**
     * @return The height of the Popup itself use this for rendering stuff inside the Popup.
     * @see PopupScreen#getHeight()
     */
    @Contract(pure = true)
    public int getPopupHeight() {
        return getHeight() - (getRenderTitle()? getTitleHeight() : 0);
    }

    @Override
    public int getDefaultResizeHeight() {
        return getPopupHeight();
    }

    /**
     * @return The leftmost position for content inside the Popup.
     * @see PopupScreen#getPopupCenterX()
     * @see PopupScreen#getPopupRight()
     */
    @Contract(pure = true)
    public int getPopupLeft() {
        return getX();
    }

    /**
     * @return The centered x position for content inside the Popup.
     * @see PopupScreen#getPopupLeft()
     * @see PopupScreen#getPopupRight()
     */
    @Contract(pure = true)
    public int getPopupCenterX() {
        return getPopupLeft() + getPopupWidth() / 2;
    }

    /**
     * @return The rightmost position for content inside the Popup.
     * @see PopupScreen#getPopupLeft()
     * @see PopupScreen#getPopupCenterX()
     */
    @Contract(pure = true)
    public int getPopupRight() {
        return getPopupLeft() + getPopupWidth();
    }

    /**
     * @return The topmost position for content inside the Popup.
     * @see PopupScreen#getPopupCenterY()
     * @see PopupScreen#getPopupBottom()
     */
    @Contract(pure = true)
    public int getPopupTop() {
        return getY() + (getRenderTitle()? getTitleHeight() : 0);
    }

    /**
     * @return The centered y position for content inside the Popup.
     * @see PopupScreen#getPopupTop()
     * @see PopupScreen#getPopupBottom()
     */
    @Contract(pure = true)
    public int getPopupCenterY() {
        return getPopupTop() + getPopupHeight() / 2;
    }

    /**
     * @return The lowest position for content inside the Popup.
     * @see PopupScreen#getPopupTop()
     * @see PopupScreen#getPopupCenterY()
     */
    @Contract(pure = true)
    public int getPopupBottom() {
        return getPopupTop() + getPopupHeight();
    }

    public Screen getParent() {
        return parent;
    }

    private boolean closeButtonEnabled = true;
    public void setCloseButtonEnabled(boolean closeButtonEnabled) {
        this.closeButtonEnabled = closeButtonEnabled;
    }
    public boolean getCloseButtonEnabled() {
        return closeButtonEnabled;
    }

    private boolean renderTitle = true;
    public void setRenderTitle(boolean renderTitle) {
        this.renderTitle = renderTitle;
    }
    public boolean getRenderTitle() {
        return renderTitle;
    }
    public int getTitleHeight() {
        if (textRenderer == null) {
            return 0;
        }
        return textRenderer.fontHeight + 11;
    }

    private int titleColor = 0xFFFFFFFF;
    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }
    public int getTitleColor() {
        return titleColor;
    }

    private static final Identifier POPUP = Identifier.of("rebindmykeys", "textures/gui/popup.png");
    private Identifier popup = POPUP;
    public void setPopup(Identifier popup) {
        this.popup = popup;
    }
    public Identifier getPopup() {
        return popup;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        renderPopup(context, mouseX, mouseY, delta);
        context.getMatrices().push();
        context.getMatrices().translate(getPopupLeft(), getPopupTop(), 0);
        ScissorUtil.push();
        ScissorUtil.translate(getPopupLeft(), getPopupTop());
        for (Drawable drawable : drawables) {
            drawable.render(context, mouseX - getPopupLeft(), mouseY - getPopupTop(), delta);
        }
        ScissorUtil.pop();
        context.getMatrices().pop();
        if (getCloseButtonEnabled())
            renderCloseButton(context, mouseX, mouseY, delta);
        if (getRenderTitle())
            renderTitle(context, mouseX, mouseY, delta);
    }

    private static final Identifier CLOSE_BUTTON = Identifier.of("rebindmykeys", "textures/gui/popup_close.png");
    public void renderCloseButton(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtil.setShaderColor(context, closeButtonHovered(mouseX, mouseY)? 0xFFC09090 : 0xFFFFFFFF);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawTexture(
                CLOSE_BUTTON,
                getPopupRight() - 16,
                getPopupTop() + 5 - (getRenderTitle()? getTitleHeight() : 0),
                0, 0, 11, 11, 11, 11
        );
        context.setShaderColor(1, 1, 1, 1);
    }

    public void renderTitle(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtil.drawScrollableText(
                context,
                textRenderer,
                title,
                getPopupCenterX(),
                getPopupLeft() + 1,
                getPopupRight() - (getCloseButtonEnabled()? 17 : 1),
                getPopupTop() - getTitleHeight() + 7,
                getTitleColor()
        );
        context.drawHorizontalLine(
                getPopupLeft() + 8,
                getPopupRight() - 9, // why is x2 one more to the right than it should be ?!?!??! vanilla implementation is wierd
                getPopupTop() - getTitleHeight() + 18,
                getTitleColor()
        );
        context.drawHorizontalLine(
                getPopupLeft() + 9,
                getPopupRight() - 8,
                getPopupTop() - getTitleHeight() + 19,
                ColorUtil.multiplyColor(getTitleColor(), 0.25f) // darkens the color to have a fitting underline color
        );
    }

    public boolean closeButtonHovered(double mouseX, double mouseY) {
        return Mouse.isHovered(
                getPopupRight() - 16,
                getPopupTop() + 5 - (getRenderTitle()? getTitleHeight() : 0),
                11, 11, mouseX, mouseY
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX - getPopupLeft(), mouseY - getPopupTop(), horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX - getPopupLeft(), mouseY - getPopupTop(), button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX - getPopupLeft(), mouseY - getPopupTop(), button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (getCloseButtonEnabled() && closeButtonHovered(mouseX, mouseY)) {
            close();
            return true;
        }
        return super.mouseClicked(mouseX - getPopupLeft(), mouseY - getPopupTop(), button);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (getParent() != null) {
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, -100);
            getParent().render(context, -1, -1, delta);
            if (topPopup) {
                context.fillGradient(
                        0, 0,
                        MinecraftClient.getInstance().getWindow().getScaledWidth(),
                        MinecraftClient.getInstance().getWindow().getScaledHeight(),
                        100, 0xC0101010, 0xD0101010
                );
            }
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

    public void renderPopup(DrawContext context, int mouseX, int mouseY, float delta) {
        if (getWidth() < 8 || getHeight() < 8) return;
        RenderUtil.render9PartTexture(
                context, getPopup(),
                getX(), getY(),
                getWidth(), getHeight(),
                4, 2
        );
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        calcDimensions(width, height);
        super.resize(client, getPopupWidth(), getPopupHeight());
        if (getParent() != null) {
            getParent().resize(client, width, height);
        }
    }

    @Override
    public void close() {
        if (parent instanceof PopupScreen popupParent)
            popupParent.topPopup = true;

        client.currentScreen = getParent(); // screen has already been initialized
    }
}
