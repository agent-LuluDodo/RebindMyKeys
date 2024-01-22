package de.luludodo.rebindmykeys.meta;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    public static final Logger LOG = LoggerFactory.getLogger("RebindMyKeys/Mixin");
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    private static final String MIXIN_PATH = "de.luludodo.rebindmykeys.mixin";
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean shouldApply = true;

        if (equals("EntryListWidgetMixin", mixinClassName)) {
            shouldApply = Comparator.compareMc("<=1.20.3");
        }

        LOG.info((shouldApply? "applied" : "skipped") + " " + formatClassName(mixinClassName) + " (" + formatClassName(targetClassName) + ")");
        return shouldApply;
    }

    private static boolean equals(String name, String mixinClassName) {
        return Objects.equals(MIXIN_PATH + "." + name, mixinClassName);
    }

    private String formatClassName(String fullClassName) {
        return fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
