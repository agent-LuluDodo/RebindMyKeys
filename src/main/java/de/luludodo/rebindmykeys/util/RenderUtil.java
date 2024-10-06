package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.RebindMyKeys;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class RenderUtil {
    private static final boolean TEST = false;

    /**
     * Renders a 9-Part Texture.
     * All measurements are in pixels.
     *
     * @param context The {@link DrawContext} used to draw the textures.
     * @param texture The {@link Identifier} for the 9-Part Texture.
     * @param x The x-position to draw at.
     * @param y The y-position to draw at.
     * @param leftWidth (During drawing) <br/>
     *                  <img src="doc-files/leftWidth.png" width="320" alt="The width of the left section.">
     * @param centerWidth (During drawing) <br/>
     *                    <img src="doc-files/centerWidth.png" width="320" alt="The width of the center section.">
     * @param rightWidth (During drawing) <br/>
     *                   <img src="doc-files/rightWidth.png" width="320" alt="The width of the right section.">
     * @param topHeight (During drawing) <br/>
     *                  <img src="doc-files/topHeight.png" width="320" alt="The height of the top section.">
     * @param centerHeight (During drawing) <br/>
     *                     <img src="doc-files/centerHeight.png" width="320" alt="The height of the center section.">
     * @param bottomHeight (During drawing) <br/>
     *                     <img src="doc-files/bottomHeight.png" width="320" alt="The height of the right section.">
     * @param u The u-position of the texture. (basically just the x-position)
     * @param v The v-position of the texture. (basically just the y-position)
     * @param textureWidth The total width of the texture.
     * @param textureHeight The total height of the texture.
     * @param leftRegionWidth (On the texture) <br/>
     *                        <img src="doc-files/leftWidth.png" width="320" alt="The width of the left section.">
     * @param centerRegionWidth (On the texture) <br/>
     *                          <img src="doc-files/centerWidth.png" width="320" alt="The width of the center section.">
     * @param rightRegionWidth (On the texture) <br/>
     *                         <img src="doc-files/rightWidth.png" width="320" alt="The width of the right section.">
     * @param topRegionHeight (On the texture) <br/>
     *                        <img src="doc-files/topHeight.png" width="320" alt="The height of the top section.">
     * @param centerRegionHeight (On the texture) <br/>
     *                           <img src="doc-files/centerHeight.png" width="320" alt="The height of the center section.">
     * @param bottomRegionHeight (On the texture) <br/>
     *                           <img src="doc-files/bottomHeight.png" width="320" alt="The height of the bottom section.">
     */
    public static void render9PartTexture(
            DrawContext context, Identifier texture,
            int x, int y,
            int leftWidth, int centerWidth, int rightWidth,
            int topHeight, int centerHeight, int bottomHeight,
            int u, int v,
            int textureWidth, int textureHeight,
            int leftRegionWidth, int centerRegionWidth, int rightRegionWidth,
            int topRegionHeight, int centerRegionHeight, int bottomRegionHeight
    ) {
        if (TEST) {
            render9PartTextureTest(
                    context, texture,
                    x, y,
                    leftWidth, centerWidth, rightWidth,
                    topHeight, centerHeight, bottomHeight,
                    u, v,
                    textureWidth, textureHeight,
                    leftRegionWidth, centerRegionWidth, rightRegionWidth,
                    topRegionHeight, centerRegionHeight, bottomRegionHeight
            );
            return;
        }

        int[] widths = new int[]{leftWidth, centerWidth, rightWidth};
        int[] heights = new int[]{bottomHeight, centerHeight, topHeight};
        int[] regionWidths = new int[]{leftRegionWidth, centerRegionWidth, rightRegionWidth};
        int[] regionHeights = new int[]{bottomRegionHeight, centerRegionHeight, topRegionHeight};

        int curY = y;
        int curV = v;
        for (int heightIndex = 0; heightIndex < 3; heightIndex++) {
            int curX = x;
            int curU = u;
            for (int widthIndex = 0; widthIndex < 3; widthIndex++) {
                context.drawTexture(
                        texture,
                        curX, curY,
                        widths[widthIndex], heights[heightIndex],
                        curU, curV,
                        regionWidths[widthIndex], regionHeights[heightIndex],
                        textureWidth, textureHeight
                );
                curX += widths[widthIndex];
                curU += regionWidths[widthIndex];
            }
            curY += heights[heightIndex];
            curV += regionHeights[heightIndex];
        }
    }

    /**
     * Renders a 9-Part Texture using <a href="https://en.wikipedia.org/wiki/9-slice_scaling">9-Slice Scaling</a>.
     * All measurements are in pixels.
     *
     * @param context The {@link DrawContext} used to draw the textures.
     * @param texture The {@link Identifier} for the 9-Part Texture.
     * @param x The x-position to draw at.
     * @param y The y-position to draw at.
     * @param width The width to draw at.
     * @param height The height to draw at.
     * @param u The u-position of the texture. (basically just the x-position)
     * @param v The v-position of the texture. (basically just the y-position)
     * @param textureWidth The total width of the texture.
     * @param textureHeight The total height of the texture.
     * @param leftRegionWidth (On the texture) <br/>
     *                        <img src="doc-files/leftWidth.png" width="320" alt="The width of the left section.">
     * @param centerRegionWidth (On the texture) <br/>
     *                          <img src="doc-files/centerWidth.png" width="320" alt="The width of the center section.">
     * @param rightRegionWidth (On the texture) <br/>
     *                         <img src="doc-files/rightWidth.png" width="320" alt="The width of the right section.">
     * @param topRegionHeight (On the texture) <br/>
     *                        <img src="doc-files/topHeight.png" width="320" alt="The height of the top section.">
     * @param centerRegionHeight (On the texture) <br/>
     *                           <img src="doc-files/centerHeight.png" width="320" alt="The height of the center section.">
     * @param bottomRegionHeight (On the texture) <br/>
     *                           <img src="doc-files/bottomHeight.png" width="320" alt="The height of the bottom section.">
     */
    public static void render9PartTexture(
            DrawContext context, Identifier texture,
            int x, int y,
            int width, int height,
            int u, int v,
            int textureWidth, int textureHeight,
            int leftRegionWidth, int centerRegionWidth, int rightRegionWidth,
            int topRegionHeight, int centerRegionHeight, int bottomRegionHeight
    ) {
        render9PartTexture(
                context, texture,
                x, y,
                leftRegionWidth, width - leftRegionWidth - rightRegionWidth, rightRegionWidth,
                topRegionHeight, height - topRegionHeight - bottomRegionHeight, bottomRegionHeight,
                u, v,
                textureWidth, textureHeight,
                leftRegionWidth, centerRegionWidth, rightRegionWidth,
                topRegionHeight, centerRegionHeight, bottomRegionHeight
        );
    }

    /**
     * Renders a 9-Part Texture using <a href="https://en.wikipedia.org/wiki/9-slice_scaling">9-Slice Scaling</a>.
     * All measurements are in pixels.
     *
     * @param context The {@link DrawContext} used to draw the textures.
     * @param texture The {@link Identifier} for the 9-Part Texture.
     * @param x The x-position to draw at.
     * @param y The y-position to draw at.
     * @param width The width to draw at.
     * @param height The height to draw at.
     * @param u The u-position of the texture. (basically just the x-position)
     * @param v The v-position of the texture. (basically just the y-position)
     * @param textureWidth The total width of the texture.
     * @param textureHeight The total height of the texture.
     * @param outside (On the texture) <br/>
     *                        <img src="doc-files/outside.png" width="320" alt="The width of the left and right section as well as the height of the top and bottom sections.">
     * @param inside (On the texture) <br/>
     *                          <img src="doc-files/inside.png" width="320" alt="The width and height of the center section.">
     */
    public static void render9PartTexture(
            DrawContext context, Identifier texture,
            int x, int y,
            int width, int height,
            int u, int v,
            int textureWidth, int textureHeight,
            int outside, int inside
    ) {
        render9PartTexture(
                context, texture,
                x, y,
                outside, width - outside - outside, outside,
                outside, height - outside - outside, outside,
                u, v,
                textureWidth, textureHeight,
                outside, inside, outside,
                outside, inside, outside
        );
    }

    /**
     * Renders a 9-Part Texture using <a href="https://en.wikipedia.org/wiki/9-slice_scaling">9-Slice Scaling</a>.
     * All measurements are in pixels.
     * Assumes that the texture provided is entirely used for the 9-Part Texture.
     *
     * @param context The {@link DrawContext} used to draw the textures.
     * @param texture The {@link Identifier} for the 9-Part Texture.
     * @param x The x-position to draw at.
     * @param y The y-position to draw at.
     * @param width The width to draw at.
     * @param height The height to draw at.
     * @param outside (On the texture) <br/>
     *                        <img src="doc-files/outside.png" width="320" alt="The width of the left and right section as well as the height of the top and bottom sections.">
     * @param inside (On the texture) <br/>
     *                          <img src="doc-files/inside.png" width="320" alt="The width and height of the center section.">
     */
    public static void render9PartTexture(
            DrawContext context, Identifier texture,
            int x, int y,
            int width, int height,
            int outside, int inside
    ) {
        render9PartTexture(
                context, texture,
                x, y,
                outside, width - outside - outside, outside,
                outside, height - outside - outside, outside,
                0, 0,
                outside + inside + outside, outside + inside + outside,
                outside, inside, outside,
                outside, inside, outside
        );
    }

    private static long lastConsoleLog = Long.MAX_VALUE;
    public static void render9PartTextureTest(
            DrawContext context, Identifier texture,
            int x, int y,
            int leftWidth, int centerWidth, int rightWidth,
            int topHeight, int centerHeight, int bottomHeight,
            int u, int v,
            int textureWidth, int textureHeight,
            int leftRegionWidth, int centerRegionWidth, int rightRegionWidth,
            int topRegionHeight, int centerRegionHeight, int bottomRegionHeight
    ) {
        if ((lastConsoleLog - System.currentTimeMillis()) > 30000) {
            lastConsoleLog = System.currentTimeMillis();
            RebindMyKeys.DEBUG.info("### render9PartTextureTest ###");
            RebindMyKeys.DEBUG.info("- X: {}", x);
            RebindMyKeys.DEBUG.info("- Y: {}", y);
            RebindMyKeys.DEBUG.info("- leftWidth: {}", leftWidth);
            RebindMyKeys.DEBUG.info("- centerWidth: {}", centerWidth);
            RebindMyKeys.DEBUG.info("- rightWidth: {}", rightWidth);
            RebindMyKeys.DEBUG.info("- topHeight: {}", topHeight);
            RebindMyKeys.DEBUG.info("- centerHeight: {}", centerHeight);
            RebindMyKeys.DEBUG.info("- bottomHeight: {}", bottomHeight);
            RebindMyKeys.DEBUG.info("- u: {}", u);
            RebindMyKeys.DEBUG.info("- v: {}", v);
            RebindMyKeys.DEBUG.info("- textureWidth: {}", textureWidth);
            RebindMyKeys.DEBUG.info("- textureHeight: {}", textureHeight);
            RebindMyKeys.DEBUG.info("- leftRegionWidth: {}", leftRegionWidth);
            RebindMyKeys.DEBUG.info("- centerRegionWidth: {}", centerRegionWidth);
            RebindMyKeys.DEBUG.info("- rightRegionWidth: {}", rightRegionWidth);
            RebindMyKeys.DEBUG.info("- topRegionHeight: {}", topRegionHeight);
            RebindMyKeys.DEBUG.info("- centerRegionHeight: {}", centerRegionHeight);
            RebindMyKeys.DEBUG.info("- bottomRegionHeight: {}", bottomRegionHeight);
            RebindMyKeys.DEBUG.info("##############################");
        }

        int[] widths = new int[]{leftWidth, centerWidth, rightWidth};
        int[] heights = new int[]{bottomHeight, centerHeight, topHeight};
        int[] regionWidths = new int[]{leftRegionWidth, centerRegionWidth, rightRegionWidth};
        int[] regionHeights = new int[]{bottomRegionHeight, centerRegionHeight, topRegionHeight};

        int[] colors = new int[]{
                0xFFFF0000, 0xFFFFFF00, 0xFF00FF00,
                0xFFFFFF00, 0xFFFFFFFF, 0xFF00FFFF,
                0xFF00FF00, 0xFF00FFFF, 0xFF0000FF
        };
        int curColor = 0;

        int curY = y;
        int curV = v;
        for (int heightIndex = 0; heightIndex < 3; heightIndex++) {
            int curX = x;
            int curU = u;
            for (int widthIndex = 0; widthIndex < 3; widthIndex++) {
                context.fill(
                        curX,
                        curY,
                        curX + widths[widthIndex],
                        curY + heights[heightIndex],
                        colors[curColor]
                );
                curColor++;
                curX += widths[widthIndex];
                curU += regionWidths[widthIndex];
            }
            curY += heights[heightIndex];
            curV += regionHeights[heightIndex];
        }
    }

    /**
     * Changes the shader color. The color format is {@code ARGB}.
     *
     * @param context The current {@link DrawContext}.
     * @param color The new color.
     *
     * @see DrawContext#setShaderColor(float, float, float, float)
     * @see ColorHelper.Argb#getRed(int)
     * @see ColorHelper.Argb#getGreen(int)
     * @see ColorHelper.Argb#getBlue(int)
     * @see ColorHelper.Argb#getAlpha(int)
     */
    public static void setShaderColor(DrawContext context, int color) {
        context.setShaderColor(
                ColorHelper.Argb.getRed(color) / (float) 0xFF,
                ColorHelper.Argb.getGreen(color) / (float) 0xFF,
                ColorHelper.Argb.getBlue(color) / (float) 0xFF,
                ColorHelper.Argb.getAlpha(color) / (float) 0xFF
        );
    }

    /**
     * <p>
     *   <i>Stolen from {@link ClickableWidget#drawScrollableText(DrawContext, TextRenderer, Text, int, int, int, int, int, int)}.</i>
     * </p>
     * <strong>Modifications:</strong>
     * <ul>
     *   <li>Arguments are verified.</li>
     *   <li>{@code centerX} doesn't need to be equal to {@code (startX - endX) / 2 + startX}.</li>
     *   <li>{@code startY} and {@code endY} are replaced by {@code y} since their behaviour was inconsistent for small differences.</li>
     * </ul>
     *
     * @param context The {@link DrawContext} used for rendering.
     * @param textRenderer The {@link TextRenderer} used for rendering and width calculations.
     * @param text The {@link Text} which is rendered.
     * @param centerX The preferred centerX of the {@code text}.
     * @param startX The start position of the allowed area.
     * @param endX The end position of the allowed area.
     * @param y The y-position of the {@code text}.
     * @param color The color of the {@code text}.
     *
     * @see ClickableWidget#drawScrollableText(DrawContext, TextRenderer, Text, int, int, int, int, int, int)
     */
    public static void drawScrollableText(DrawContext context, TextRenderer textRenderer, Text text, int centerX, int startX, int endX, int y, int color) {
        if (startX > endX)
            throw new IllegalArgumentException("startX > endX");
        if (startX > centerX)
            throw new IllegalArgumentException("startX > centerX");
        if (centerX > endX)
            throw new IllegalArgumentException("centerX > endX");

        int textWidth = textRenderer.getWidth(text);

        int maxStaticWidth = endX - startX;
        if (textWidth > maxStaticWidth) {
            int overflowWidth = textWidth - maxStaticWidth;
            double scrollTime = (double) Util.getMeasuringTimeMs() / 1000.0;
            double perSideOverflowWidth = Math.max((double) overflowWidth * 0.5, 3.0);
            double smoothScroll = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * scrollTime / perSideOverflowWidth)) / 2.0 + 0.5;
            double textOffset = MathHelper.lerp(smoothScroll, 0.0, overflowWidth);

            context.enableScissor(startX, y, endX, y + textRenderer.fontHeight);
            context.drawTextWithShadow(textRenderer, text, startX - (int)textOffset, y, color);
            context.disableScissor();
            return;
        }

        int maxCenterWidth = Math.min(centerX - startX, endX - centerX) * 2;
        if (textWidth > maxCenterWidth) {
            boolean leftOrientation = centerX - startX < endX - centerX;
            int textX = leftOrientation? startX : endX - textWidth;

            context.drawTextWithShadow(textRenderer, text, textX, y, color);
            return;
        }

        context.drawCenteredTextWithShadow(textRenderer, text, centerX, y, color);
    }
}
