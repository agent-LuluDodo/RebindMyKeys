package de.luludodo.rebindmykeys.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Adds:
 * <ul>
 *     <li>itemHeight per Entry</li>
 *     <li>rowWidth</li>
 *     <li>rowMargin</li>
 *     <li>scrollbarMargin</li>
 *     <li>topMargin</li>
 *     <li>bottomMargin</li>
 *     <li>Resizable</li>
 * </ul>
 */
public abstract class VariableElementListWidget<E extends VariableElementListWidget.Entry<E>> extends ElementListWidget<E> implements Resizable {
    private int rowWidth;
    private int scrollbarMargin = 14;
    private int rowMargin = 4;
    private final Screen parent;
    private final int widthDifference;
    private final int heightDifference;
    private int topMargin = 2;
    private int bottomMargin = 2;
    public VariableElementListWidget(MinecraftClient client, Screen parent, int width, int height, int x, int y, int rowWidth) {
        super(client, width, height, y, -1);
        setX(x);
        this.parent = parent;
        widthDifference = parent.width - width;
        heightDifference = parent.height - height;
        this.rowWidth = rowWidth;
    }
    public VariableElementListWidget(MinecraftClient client, Screen parent, int width, int height, int x, int y) {
        this(client, parent, width, height, x, y, 220);
    }

    @Contract(pure = true)
    public Screen getParent() {
        return parent;
    }

    public void setRowWidth(int width) {
        rowWidth = width;
    }
    public void fitRowWidth() {
        setRowWidth(Math.min(getRowWidth(), getWidth() - (getMaxScroll() > 0? (getScrollbarMargin() + getScrollbarWidth()) : 0)));
    }
    public void setScrollBarMargin(int margin) {
        scrollbarMargin = margin;
    }
    public void setRowMargin(int margin) {
        rowMargin = margin;
    }
    public void setTopMargin(int margin) {
        topMargin = margin;
    }
    public void setBottomMargin(int margin) {
        bottomMargin = margin;
    }

    @Override
    @Contract(pure = true)
    public int getRowWidth() {
        return rowWidth;
    }
    @Contract(pure = true)
    public int getScrollbarMargin() {
        return scrollbarMargin;
    }
    @Contract(pure = true)
    public int getRowMargin() {
        return rowMargin;
    }
    @Contract(pure = true)
    public int getTopMargin() {
        return topMargin;
    }
    @Contract(pure = true)
    public int getBottomMargin() {
        return bottomMargin;
    }

    public int getMaxScroll() {
        return Math.max(0, getMaxPosition() - (height - (getTopMargin() + getBottomMargin())));
    }

    @Contract(pure = true)
    public int getContentWidth() {
        return getRowWidth() + (getMaxScroll() > 0? (getScrollbarMargin() + getScrollbarWidth()) : 0);
    }

    @Contract(pure = true)
    public int getTargetHeight() {
        int targetHeight = getTopMargin() + getBottomMargin() + getRowMargin() * (getEntryCount() - 1);
        for (int index = 0; index < getEntryCount(); index++) {
            targetHeight += getEntry(index).getHeight();
        }
        return targetHeight;
    }

    @Override
    @Contract(pure = true)
    public int getScrollbarPositionX() {
        return getRowRight() + getScrollbarMargin();
    }

    @Contract(pure = true)
    public int getScrollbarWidth() {
        return 6;
    }

    @Override
    @Contract(pure = true)
    public int getRowLeft() {
        return getWidth() / 2 - getContentWidth() / 2 + getX();
    }

    @Override
    @Nullable
    @Contract(pure = true)
    protected E getEntryAtPosition(double x, double y) { // <- Incorrectly flagged as error
        int halfRowWidth = getRowWidth() / 2;
        int center = getX() + getWidth() / 2;
        int left = center - halfRowWidth;
        int right = center + halfRowWidth;
        if (x >= getScrollbarPositionX() || x < left || x > right) return null;

        int absoluteY = MathHelper.floor(y - (double) getY() - headerHeight + (int) getScrollAmount());
        if (absoluteY < 0) return null;

        absoluteY -= getRowMargin() / 2;
        int height = 0;
        for (int index = 0; index < getEntryCount(); index++) {
            E entry = getEntry(index);
            height += entry.getHeight();
            height += getRowMargin();
            if (height > absoluteY) return entry;
        }
        return null;
    }

    @Override
    @Contract(pure = true)
    protected int getMaxPosition() {
        return getTotalHeight() + headerHeight;
    }

    @Contract(pure = true)
    private int getTotalHeight() {
        return getAbsoluteY(getEntryCount());
    }

    @Contract(pure = true)
    protected int getAbsoluteY(E entry) {
        int absoluteY = getTopMargin();
        for (int index = 0; index < getEntryCount(); index++) {
            E e = getEntry(index);
            absoluteY += getRowMargin();
            if (e == entry) return absoluteY;
            absoluteY += e.getHeight();
        }
        throw new IllegalArgumentException("No such element: " + entry);
    }

    @Contract(pure = true)
    protected int getAbsoluteY(int finalIndex) {
        int absoluteY = getTopMargin();
        for (int index = 0; index < finalIndex; index++) {
            E e = getEntry(index);
            absoluteY += getRowMargin();
            absoluteY += e.getHeight();
        }
        return absoluteY;
    }

    @Override
    protected void centerScrollOn(E entry) {
        setScrollAmount(getAbsoluteY(entry) + (double) entry.getHeight() / 2 - (double) getHeight() / 2);
    }

    @Override
    protected void ensureVisible(E entry) {
        int top = getAbsoluteY(entry);
        int visibleTop = getY() + getRowMargin();
        int topOffset = visibleTop - top;
        if (topOffset < 0) {
            scroll(topOffset);
        }

        int bottom = top - entry.getHeight();
        int visibleBottom = getBottom() - getRowMargin();
        int bottomOffset = visibleBottom - bottom;
        if (bottomOffset < 0) {
            scroll(-bottomOffset);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        setScrollAmount(getScrollAmount() - verticalAmount * getTotalHeight() / (getEntryCount() * 2d));
        return true;
    }

    @Override
    protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        int left = getRowLeft();
        int width = getRowWidth();

        for (int index = 0; index < getEntryCount(); index++) {
            int height = getEntry(index).getHeight();
            int top = getRowTop(index);
            int bottom = top + height;
            if (bottom >= getY() && top <= getBottom()) {
                renderEntry(context, mouseX, mouseY, delta, index, left, top, width, height);
            }
        }
    }

    @Override
    @Contract(pure = true)
    protected int getRowTop(int index) {
        return getY() - (int) getScrollAmount() + getAbsoluteY(index) + headerHeight;
    }

    @Override
    @Contract(pure = true)
    protected int getRowBottom(int index) {
        return getRowTop(index) + getEntry(index).getHeight();
    }

    private void scroll(int amount) {
        this.setScrollAmount(this.getScrollAmount() + (double)amount);
    }

    @Override
    public void resize(int totalWidth, int totalHeight) {
        double oldMaxPosition = getMaxPosition();
        setWidth(totalWidth - widthDifference);
        setHeight(totalHeight - heightDifference);
        if (oldMaxPosition > 0) {
            setScrollAmount((getScrollAmount() + getHeight()) * (getMaxPosition() / oldMaxPosition) - getHeight());
        } else {
            setScrollAmount(getScrollAmount());
        }
    }

    public abstract static class Entry<E extends ElementListWidget.Entry<E>> extends ElementListWidget.Entry<E> {
        @Contract(pure = true)
        public abstract int getHeight();
    }
}
