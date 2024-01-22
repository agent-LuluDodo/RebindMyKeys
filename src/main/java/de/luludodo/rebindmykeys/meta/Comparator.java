package de.luludodo.rebindmykeys.meta;

import de.luludodo.rebindmykeys.RebindMyKeys;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.client.MinecraftClient;

import java.util.Optional;

public abstract class Comparator {
    private static final FabricLoader FABRIC = FabricLoader.getInstance();
    public static boolean compareMc(String condition) {
        return compareMod("minecraft", condition);
    }

    public static boolean compare(String version, String condition) {
        return compare(parseVersion(version), condition);
    }

    public static boolean isModLoaded(String modId) {
        return FABRIC.isModLoaded(modId);
    }

    public static boolean compareMod(String modId, String condition) {
        Optional<ModContainer> mod = FABRIC.getModContainer(modId);
        if (mod.isEmpty()) {
            RebindMyKeys.LOG.error("Couldn't find mod with id " + modId);
            return false;
        }
        return compare(mod.get().getMetadata().getVersion(), condition);
    }

    public static boolean compare(Version version, String condition) {
        if (condition.startsWith("<=")) {
            return compare(version, parseVersion(condition.substring(2)), SMALLER_OR_EQUAL);
        } else if (condition.startsWith(">=")) {
            return compare(version, parseVersion(condition.substring(2)), LARGER_OR_EQUAL);
        } else if (condition.startsWith("<")) {
            return compare(version, parseVersion(condition.substring(1)), SMALLER);
        } else if (condition.startsWith(">")) {
            return compare(version, parseVersion(condition.substring(1)), LARGER);
        } else {
            return compare(version, parseVersion(condition), EQUAL);
        }
    }

    private static final int SMALLER = -2;
    private static final int SMALLER_OR_EQUAL = -1;
    private static final int EQUAL = 0;
    private static final int LARGER_OR_EQUAL = 1;
    private static final int LARGER = 2;
    public static boolean compare(Version version, Version compareTo, int operation) {
        if (version == null || compareTo == null) {
            return false;
        }
        int compareResult = version.compareTo(compareTo);
        return switch (operation) {
            case -2 -> compareResult < 0;
            case -1 -> compareResult <= 0;
            case 0 -> compareResult == 0;
            case 1 -> compareResult >= 0;
            case 2 -> compareResult > 0;
            default -> {
                RebindMyKeys.LOG.error("Invalid operation " + operation);
                yield false;
            }
        };
    }

    private static Version parseVersion(String version) {
        try {
            return Version.parse(version);
        } catch (VersionParsingException e) {
            RebindMyKeys.LOG.error("Invalid version", e);
            return null;
        }
    }
}
