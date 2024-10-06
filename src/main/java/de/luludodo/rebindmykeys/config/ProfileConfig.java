package de.luludodo.rebindmykeys.config;

import de.luludodo.rebindmykeys.api.config.JsonMapConfig;
import de.luludodo.rebindmykeys.config.serializer.ProfileConfigSerializer;
import de.luludodo.rebindmykeys.profiles.Profile;
import de.luludodo.rebindmykeys.profiles.ProfileManager;

import java.util.Map;
import java.util.UUID;

/**
 * Stores the currently active {@link Profile} for the {@link ProfileManager}
 */
public class ProfileConfig extends JsonMapConfig<String, Object> {
    public static final String CURRENT_PROFILE = "currentProfile";

    /**
     * Creates a new instance of the {@link ProfileConfig}.
     * @see ProfileManager#getConfig()
     */
    public ProfileConfig() {
        super("rebindmykeys/profiles", 1, new ProfileConfigSerializer());
    }

    @Override
    protected Map<String, Object> getDefaults() {
        return Map.of(
                CURRENT_PROFILE, ProfileManager.DEFAULT_UUID
        );
    }

    /**
     * @return The currently active profile's id.
     * @see ProfileManager#getCurrentProfile()
     */
    public UUID getCurrentProfile() {
        return (UUID) get(CURRENT_PROFILE);
    }

    /**
     * Sets the currently active profile's id. <br>
     * <b>Use {@link ProfileManager#setCurrentProfile(Profile)} to update the active profile, this only changes the value saved to disk on {@link ProfileConfig#save()}.</b>
     * @param profile The new id.
     * @see ProfileManager#setCurrentProfile(Profile)
     */
    public void setCurrentProfile(UUID profile) {
        set(CURRENT_PROFILE, profile);
    }
}
