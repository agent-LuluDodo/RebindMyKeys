package de.luludodo.rebindmykeys.util;

import com.mojang.blaze3d.systems.RenderSystem;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.info.ModInfo;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;


public class FabricUtil {
    private static class ModMetadataImpl implements ModMetadata {
        private final String id;
        private final String name;
        public ModMetadataImpl(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String getType() {
            return "builtin";
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Collection<String> getProvides() {
            return List.of();
        }

        @Override
        public Version getVersion() {
            try {
                return Version.parse("1.0");
            } catch (VersionParsingException e) {
                throw new Error(e); // Version.parse("1.0") shouldn't cause VersionParsingException
            }
        }

        @Override
        public ModEnvironment getEnvironment() {
            return ModEnvironment.CLIENT;
        }

        @Override
        public Collection<ModDependency> getDependencies() {
            return List.of();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public Collection<Person> getAuthors() {
            return List.of();
        }

        @Override
        public Collection<Person> getContributors() {
            return List.of();
        }

        @Override
        public ContactInformation getContact() {
            return ContactInformation.EMPTY;
        }

        @Override
        public Collection<String> getLicense() {
            return List.of();
        }

        @Override
        public Optional<String> getIconPath(int size) {
            return Optional.empty();
        }

        @Override
        public boolean containsCustomValue(String key) {
            return false;
        }

        @Override
        public CustomValue getCustomValue(String key) {
            return null;
        }

        @Override
        public Map<String, CustomValue> getCustomValues() {
            return Map.of();
        }

        @Override
        public boolean containsCustomElement(String key) {
            return false;
        }
    }
    private static class ModContainerImpl implements ModContainer {
        private final ModMetadata metadata;
        private ModContainerImpl(String id, String name) {
            this.metadata = new ModMetadataImpl(id, name);
        }

        @Override
        public ModMetadata getMetadata() {
            return this.metadata;
        }

        @Override
        public List<Path> getRootPaths() {
            return List.of();
        }

        @Override
        public ModOrigin getOrigin() {
            return null;
        }

        @Override
        public Optional<ModContainer> getContainingMod() {
            return Optional.empty();
        }

        @Override
        public Collection<ModContainer> getContainedMods() {
            return List.of();
        }

        @Override
        public Path getRootPath() {
            return null;
        }

        @Override
        public Path getPath(String file) {
            return null;
        }
    }

    public static final ModContainer UNKNOWN = new ModContainerImpl("null", "Unknown");
    public static final ModContainer VANILLA = new ModContainerImpl("minecraft", "Minecraft");

    public static final ModIcon NO_ICON = new ModIcon();
    public static class ModIcon {
        private final Identifier lowResId;
        private final int lowResSize;
        private final Identifier highResId;
        private final int highResSize;
        private final boolean available;
        private ModIcon(Identifier lowResId, int lowResSize, Identifier highResId, int highResSize) {
            this.lowResId = lowResId;
            this.lowResSize = lowResSize;
            this.highResId = highResId;
            this.highResSize = highResSize;

            this.available = true;
        }
        private ModIcon() {
            this.lowResId = null;
            this.lowResSize = 0;
            this.highResId = null;
            this.highResSize = 0;

            this.available = false;
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

    public static ModIcon getIcon(ModContainer mod, int lowRes, int highRes) {
        if (mod == null || isVanilla(mod) || isUnknown(mod)) {
            return NO_ICON;
        }

        Identifier lowResId = loadIconForSize(mod, lowRes);
        Identifier highResId = loadIconForSize(mod, highRes);
        if (lowResId != null && highResId == null) {
            highResId = lowResId;
        } else if (highResId != null && lowResId == null) {
            lowResId = highResId;
        }

        return lowResId == null ? NO_ICON : new ModIcon(lowResId, lowRes, highResId, highRes);
    }

    private static final Map<Pair<ModContainer, Integer>, Identifier> iconCache = new HashMap<>();
    private static @Nullable Identifier loadIconForSize(ModContainer mod, int size) {
        Pair<ModContainer, Integer> key = new Pair<>(mod, size);
        if (!iconCache.containsKey(key))
            iconCache.put(key, loadIconForSizeUncached(mod, size));
        return iconCache.get(key);
    }

    private static Identifier loadIconForSizeUncached(ModContainer mod, int size) {
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
        return null;
    }

    private static final Map<Class<?>, ModContainer> getModCache = new HashMap<>();

    /**
     * Returns the mod which led to this line of code being executed. If mod A calls a function from mod B and this method is called inside of mod B mod A will be returned.
     * @return The mod which led to this function running.
     */
    public static ModContainer getMod() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        Class<?> cl;
        try {
            cl = Class.forName(stackTrace[stackTrace.length - 9].getClassName());
        } catch (ClassNotFoundException e) {
            RebindMyKeys.LOG.error("Couldn't get Class for name '" + stackTrace[stackTrace.length - 9].getClassName() + "'", e);
            return UNKNOWN;
        } catch (IndexOutOfBoundsException e) {
            RebindMyKeys.LOG.error("Invalid StackTrace (called before mod loading?)");
            return UNKNOWN;
        }

        if (!getModCache.containsKey(cl))
            getModCache.put(cl, getModForClass(cl));

        return getModCache.get(cl);
    }

    private static ModContainer getModForClass(Class<?> cl) {
        Path clPath;
        try {
            clPath = Path.of(cl.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            RebindMyKeys.LOG.error("Couldn't get URI for code source of class '{}'", cl.getName());
            return UNKNOWN;
        }

        // Check Root Paths
        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            for (Path path : modContainer.getRootPaths()) {
                if (checkPath(path, clPath))
                    return modContainer;
            }
        }

        // Check Origin Paths
        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            for (Path path : modContainer.getOrigin().getPaths()) {
                if (checkPath(path, clPath))
                    return modContainer;
            }
        }

        return VANILLA;
    }

    private static boolean checkPath(Path path1, Path path2) {
        if (path1.equals(path2)) {
            return true;
        } else if (path1.endsWith(Path.of("resources", "main")) && path2.endsWith(Path.of("classes", "java", "main"))) {
            return path1.subpath(0, path1.getNameCount() - 2).equals(path2.subpath(0, path2.getNameCount() - 3));
        }
        return false;
    }

    public static boolean isVanilla(ModContainer container) {
        return container == VANILLA; // Intentionally using == instead of equals since only the one reference stored in VANILLA is actually represents vanilla
    }

    public static boolean isUnknown(ModContainer container) {
        return container == UNKNOWN; // Intentionally using == instead of equals since only the one reference stored in UNKNOWN is actually represents an unknown mod
    }

    private static int tickCount = 0;
    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> tickCount++);
    }

    public static int getTickCount() {
        return tickCount;
    }
}
