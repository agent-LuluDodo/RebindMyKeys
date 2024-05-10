package de.luludodo.rebindmykeys.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

/**
 * Allows each entry to have a different itemHeight, rowWidth, rowMargin, scrollbarMargin
 */
public abstract class VariableElementListWidget<E extends VariableElementListWidget.Entry<E>> extends ElementListWidget<E> {
    private int rowWidth;
    private int scrollbarMargin = 14;
    private int rowMargin = 4;
    public VariableElementListWidget(MinecraftClient minecraftClient, int width, int height, int y, int rowWidth) {
        super(minecraftClient, width, height, y, -1);
        this.rowWidth = rowWidth;
    }
    public VariableElementListWidget(MinecraftClient minecraftClient, int width, int height, int y) {
        this(minecraftClient, width, height, y, 220);
    }

    public void setRowWidth(int width) {
        rowWidth = width;
    }
    public void fitRowWidth() {
        rowWidth = Math.min(rowWidth, width - getScrollbarMargin() - getScrollbarWidth());
    }
    public void setScrollBarMargin(int margin) {
        scrollbarMargin = margin;
    }
    public void setRowMargin(int margin) {
        rowMargin = margin;
    }

    @Override
    public int getRowWidth() {
        return rowWidth;
    }
    public int getScrollbarMargin() {
        return scrollbarMargin;
    }
    public int getRowMargin() {
        return rowMargin;
    }

    public int getContentWidth() {
        return getRowWidth() + getScrollbarMargin() + getScrollbarWidth();
    }

    @Override
    public int getScrollbarPositionX() {
        return getRowRight() + getScrollbarMargin();
    }

    public int getScrollbarWidth() {
        return 6;
    }

    @Override
    public int getRowLeft() {
        return width / 2 - getContentWidth() / 2;
    }

    @Override
    @Nullable
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
    protected int getMaxPosition() {
        return getTotalHeight() + headerHeight - 2;
    }

    private int getTotalHeight() {
        return getAbsoluteY(getEntryCount());
    }

    protected int getAbsoluteY(E entry) {
        int absoluteY = 2;
        for (int index = 0; index < getEntryCount(); index++) {
            E e = getEntry(index);
            absoluteY += getRowMargin();
            if (e == entry) return absoluteY;
            absoluteY += e.getHeight();
        }
        throw new IllegalArgumentException("No such element: " + entry);
    }

    protected int getAbsoluteY(int finalIndex) {
        int absoluteY = 2;
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
    protected int getRowTop(int index) {
        return getY() - (int) getScrollAmount() + getAbsoluteY(index) + headerHeight;
    }

    @Override
    protected int getRowBottom(int index) {
        return getRowTop(index) + getEntry(index).getHeight();
    }

    private void scroll(int amount) {
        this.setScrollAmount(this.getScrollAmount() + (double)amount);
    }

    public abstract static class Entry<E extends ElementListWidget.Entry<E>> extends ElementListWidget.Entry<E> {
        public abstract int getHeight();
    }
}
