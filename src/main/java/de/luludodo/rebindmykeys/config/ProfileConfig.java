package de.luludodo.rebindmykeys.config;

import de.luludodo.rebindmykeys.api.config.JsonMapConfig;
import de.luludodo.rebindmykeys.config.serializer.ProfileConfigSerializer;
import de.luludodo.rebindmykeys.profiles.Profile;
import de.luludodo.rebindmykeys.profiles.ProfileManager;

import java.util.Map;

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
        super("rebindmykeys/profiles", new ProfileConfigSerializer());
    }

    @Override
    protected Map<String, Object> getDefaults() {
        return Map.of(
                CURRENT_PROFILE, "default"
        );
    }

    /**
     * @return The currently active profile's id.
     * @see ProfileManager#getCurrentProfile()
     */
    public String getCurrentProfile() {
        return (String) get(CURRENT_PROFILE);
    }

    /**
     * Sets the currently active profile's id. <br>
     * <b>Use {@link ProfileManager#setCurrentProfile(Profile)} to update the active profile, this only changes the value saved to disk on {@link ProfileConfig#save()}.</b>
     * @param profile The new id.
     * @see ProfileManager#setCurrentProfile(Profile)
     */
    public void setCurrentProfile(String profile) {
        set(CURRENT_PROFILE, profile);
    }
}
