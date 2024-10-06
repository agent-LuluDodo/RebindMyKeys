package de.luludodo.rebindmykeys.profiles;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.config.ProfileConfig;
import de.luludodo.rebindmykeys.util.InitialKeyBindings;
import de.luludodo.rebindmykeys.util.KeyBindingUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Static class which manages instances of {@link Profile}.
 */
public final class ProfileManager {
    private ProfileManager() {}

    public static final UUID DEFAULT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private static Profile currentProfile = null;
    private static final ProfileConfig CONFIG = new ProfileConfig();
    private static final Map<UUID, String> registeredProfiles = new HashMap<>();
    private static Map<UUID, Profile> profiles = null; // TODO: option to hide specific KeyBindings in a profile

    /**
     * @return The instance of {@link ProfileConfig} used by the ProfileManager
     */
    public static ProfileConfig getConfig() {
        return CONFIG;
    }

    /**
     * Initializes the ProfileManager
     */
    public static void init() {
        registeredProfiles.put(DEFAULT_UUID, "Defaults");

        loadProfiles();

        currentProfile = profiles.get(CONFIG.getCurrentProfile());
        if (currentProfile == null) {
            currentProfile = profiles.get(DEFAULT_UUID);
        }

        InitialKeyBindings.disable();
    }

    /**
     * Reloads the ProfileManager
     */
    public static void reload() {
        CONFIG.reload();
        loadProfiles();
        KeyBindingUtil.calcIncompatibleUUIDs();
    }

    /**
     * Saves all profiles and the manager
     */
    public static void save() {
        CONFIG.save();
        profiles.values().forEach(Profile::save);
    }

    /**
     * Registers a profile with the specified uuid and name.
     * Registering forces a profile to appear as an option even if the files for the profile are missing.
     * @param uuid The uuid of the profile to register
     * @param name The name of the profile to register
     */
    public static void register(UUID uuid, String name) {
        registeredProfiles.put(uuid, name);
    }

    /**
     * Registers a profile with the specified name.
     * Registering forces a profile to appear as an option even if the files for the profile are missing.
     * @param name The name of the profile to register
     */
    public static void register(String name) {
        registeredProfiles.put(UUID.randomUUID(), name);
    }

    /**
     * Registers a profile and saves the files for that profile to disk for later loading.
     * Note that the instance of the {@link Profile} passed in this function isn't the same instance returned by {@link ProfileManager#get(UUID)}.
     * Registering forces a profile to appear as an option even if the files for the profile are missing.
     * @param profile The profile to register
     */
    public static void register(Profile profile) {
        register(profile.getUUID(), profile.getName());
        profile.save();
    }

    /**
     * @return The directory where profiles are saved.
     */
    public static Path getProfilesDir() {
        return FabricLoader.getInstance().getConfigDir().resolve(getProfilesDirRelative());
    }

    /**
     * @return The directory where profiles are saved as a relative path.
     */
    public static Path getProfilesDirRelative() {
        return Path.of("rebindmykeys", "profiles");
    }

    private static void loadProfiles() {
        profiles = new HashMap<>();

        Path path = getProfilesDir();
        File file = path.toFile();
        if (!file.exists()) {
            RebindMyKeys.LOG.warn("Couldn't find profiles at '{}'!", path);
        } else if (!file.isDirectory()) {
            RebindMyKeys.LOG.error("Expected directory but got file at '{}'!", path);
        } else {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    if (entry.toFile().isDirectory()) {
                        try {
                            UUID uuid = UUID.fromString(entry.getFileName().toString());
                            profiles.put(uuid, new Profile(uuid, !registeredProfiles.containsKey(uuid)));
                        } catch (IllegalArgumentException e) {
                            RebindMyKeys.LOG.error("Couldn't read profile '" + entry.getFileName().toString() + "' because its name isn't a valid UUID!", e);
                        }
                    }
                }
            } catch (IOException e) {
                RebindMyKeys.LOG.error("Couldn't load profiles!", e);
            }
        }

        registeredProfiles.forEach((uuid, name) -> {
            if (!profiles.containsKey(uuid)) {
                Profile profile = new Profile(uuid, false);
                profile.getGlobal().setName(name);
                profiles.put(uuid, profile);
            }
        });
    }

    /**
     * @return The currently active profile.
     */
    public static Profile getCurrentProfile() {
        return currentProfile;
    }

    /**
     * Updates the currently active profile.
     * @param profile The profile to activate.
     */
    public static void setCurrentProfile(Profile profile) {
        CONFIG.setCurrentProfile(profile.getUUID());
        profile.reload();
        currentProfile = profile;
        KeyBindingUtil.calcIncompatibleUUIDs();
    }

    /**
     * @return All options for profiles which are currently loaded.
     */
    public static Set<UUID> options() {
        return profiles.keySet();
    }

    /**
     * Creates a new {@link Profile} with the specified name.
     * @param name The name of the new {@link Profile}.
     * @return The newly created {@link Profile}.
     */
    public static Profile create(UUID uuid, String name) {
        Profile profile = new Profile(uuid, true);
        profile.getGlobal().setName(name);
        profiles.put(uuid, profile);
        return profile;
    }

    /**
     * @param uuid The uuid of the profile to search for
     * @return The profile with the specified id or {@code null} if no profile is found.
     */
    public static Profile get(UUID uuid) {
        return profiles.get(uuid);
    }

    public static void delete(UUID uuid) {
        Profile profile = profiles.remove(uuid);
        profile.delete();
    }
}
