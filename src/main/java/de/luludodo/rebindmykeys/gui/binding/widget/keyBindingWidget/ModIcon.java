package de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.info.ModInfo;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

/**
 * Simple class for loading mod icons.
 * May fail in some edge cases, but should be good enough for most mods.
 */
public class ModIcon {
    private Identifier lowResId;
    private final int lowResSize;
    private Identifier highResId;
    private final int highResSize;
    private final boolean available;
    public ModIcon(ModContainer mod, int lowRes, int highRes) {
        lowResSize = lowRes;
        highResSize = highRes;

        if (mod == null) {
            available = false;
            return;
        }

        lowResId = load(mod, lowRes);
        highResId = load(mod, highRes);
        if (lowResId != null && highResId == null) {
            highResId = lowResId;
        } else if (highResId != null && lowResId == null) {
            lowResId = highResId;
        }
        available = lowResId != null;
    }

    private static @Nullable Identifier load(ModContainer mod, int size) {
        Optional<String> iconPath = mod.getMetadata().getIconPath(size);
        if (iconPath.isPresent()) {
            String[] parts = iconPath.get().replace("\\", "/").split("/", 3);
            if ("assets".equals(parts[0]) && parts.length == 3) {
                try {
                    NativeImage nativeImage = NativeImage.read(MinecraftClient.getInstance().getResourceManager().getResourceOrThrow(new Identifier(parts[1], parts[2])).getInputStream());
                    NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
                    return MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("rebindmykeys/icon_" + mod.getMetadata().getId() + "_" + size + "x" + size, texture);
                } catch (IOException e) {
                    RebindMyKeys.LOG.error("Error loading icon for mod '" + ModInfo.getModName(mod) + "' with size " + size, e);
                }
            }
        }
        RebindMyKeys.DEBUG.warn("No icon found for mod '{}' with size {}", ModInfo.getModName(mod), size);
        return null;
    }

    public boolean available() {
        return available;
    }

    public void renderLowRes(DrawContext context, int x, int y, int z) {
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, z);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawTexture(lowResId, x, y, 0, 0, lowResSize, lowResSize, lowResSize, lowResSize);
        context.getMatrices().pop();
    }

    public void renderHighRes(DrawContext context, int x, int y, int z) {
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, z);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawTexture(highResId, x, y, 0, 0, highResSize, highResSize, highResSize, highResSize);
        context.getMatrices().pop();
    }
}
