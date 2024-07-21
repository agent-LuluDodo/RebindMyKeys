package de.luludodo.rebindmykeys.profiles;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.config.ProfileConfig;
import net.fabricmc.loader.api.FabricLoader;

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

    private static Profile currentProfile = null;
    private static final ProfileConfig CONFIG = new ProfileConfig();

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
        String currentProfileId = CONFIG.getCurrentProfile();
        register(currentProfileId);
        loadProfiles();
        currentProfile = profiles.get(currentProfileId);
    }

    /**
     * Reloads the ProfileManager
     */
    public static void reload() {
        CONFIG.reload();
        loadProfiles();
    }

    /**
     * Saves all profiles and the manager
     */
    public static void save() {
        CONFIG.save();
        profiles.values().forEach(Profile::save);
    }

    private static final Set<String> registeredProfiles = new HashSet<>();

    /**
     * Registers a profile with the specified id.
     * Registering forces a profile to appear as an option even if the files for the profile are missing.
     * @param id The id of the profile to register
     */
    public static void register(String id) {
        registeredProfiles.add(id);
    }
    /**
     * Registers a profile and saves the files for that profile to disk for later loading.
     * Note that the instance of the {@link Profile} passed in this function isn't the same instance returned by {@link ProfileManager#get(String)}.
     * Registering forces a profile to appear as an option even if the files for the profile are missing.
     * @param profile The profile to register
     */
    public static void register(Profile profile) {
        register(profile.getId());
        profile.getConfig().save();
        profile.getGlobal().save();
    }

    /**
     * @return The directory where profiles are saved.
     */
    public static Path getProfilesDir() {
        return FabricLoader.getInstance().getConfigDir().resolve("rebindmykeys/profiles");
    }

    private static Map<String, Profile> profiles = null; // TODO: option to hide specific KeyBindings in a profile
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
                        String id = entry.getFileName().toString();
                        profiles.put(id, new Profile(id));
                    }
                }
            } catch (IOException e) {
                RebindMyKeys.LOG.error("Couldn't load profiles!", e);
            }
        }

        for (String id : registeredProfiles) {
            if (!profiles.containsKey(id)) {
                profiles.put(id, new Profile(id));
            }
        }
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
        CONFIG.setCurrentProfile(profile.getId());
        currentProfile = profile;
    }

    /**
     * @return All options for profiles which are currently loaded.
     */
    public static Set<String> options() {
        return profiles.keySet();
    }

    /**
     * Creates a new {@link Profile} with the specified id.
     * @param id The id of the new {@link Profile}.
     * @return The newly created {@link Profile}.
     */
    public static Profile create(String id) {
        if (profiles.containsKey(id)) throw new IllegalArgumentException("Duplicate profile id: " + id);
        Profile profile = new Profile(id);
        profiles.put(id, profile);
        return profile;
    }

    /**
     * @param id The id of the profile to search for
     * @return The profile with the specified id or {@code null} if no profile is found.
     */
    public static Profile get(String id) {
        return profiles.get(id);
    }
}
