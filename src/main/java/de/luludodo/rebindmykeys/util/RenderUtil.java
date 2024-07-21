package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.RebindMyKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class RenderUtil {
    private static final boolean TEST = false;
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

    public static void setShaderColor(DrawContext context, int color) {
        context.setShaderColor(
                ColorHelper.Argb.getRed(color) / (float) 0xFF,
                ColorHelper.Argb.getGreen(color) / (float) 0xFF,
                ColorHelper.Argb.getBlue(color) / (float) 0xFF,
                ColorHelper.Argb.getAlpha(color) / (float) 0xFF
        );
    }
}
