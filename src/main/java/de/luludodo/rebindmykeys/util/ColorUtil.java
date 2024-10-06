package de.luludodo.rebindmykeys.util;

import net.minecraft.util.math.ColorHelper;

public class ColorUtil {
    /**
     * Multiplies the {@code red}, {@code green} and {@code blue} components but not the {@code alpha} component.
     * @param color The original color
     * @param multiplier The multiplier
     * @return The adjusted color
     */
    public static int multiplyColor(int color, float multiplier) {
        return /* alpha */ color & 0xFF000000 +
                /* red */ (Math.round(ColorHelper.Argb.getRed(color) * multiplier) & 0xFF) << 16 +
                /* green */ (Math.round(ColorHelper.Argb.getGreen(color) * multiplier) & 0xFF) << 8 +
                /* blue */ Math.round(ColorHelper.Argb.getBlue(color) * multiplier) & 0xFF;
    }
}
