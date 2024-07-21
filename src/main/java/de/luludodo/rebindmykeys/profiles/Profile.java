package de.luludodo.rebindmykeys.profiles;

import de.luludodo.rebindmykeys.config.GlobalConfig;
import de.luludodo.rebindmykeys.config.KeyBindingConfig;
import de.luludodo.rebindmykeys.util.KeyBindingUtil;
import de.luludodo.rebindmykeys.util.KeyUtil;

/**
 * Allows for easy switching between settings.
 * @see ProfileManager
 */
public class Profile {
    private final KeyBindingConfig config;
    private final GlobalConfig global;
    private final String id;

    /**
     * Initializes a new profile. <br>
     * <b>Use {@link ProfileManager#create(String)} instead of this.</b>
     * @param id The id of this profile
     * @see ProfileManager#create(String)
     */
    protected Profile(String id) {
        config = new KeyBindingConfig(id);
        global = new GlobalConfig(id);
        this.id = id;
    }

    /**
     * @return The id of this profile
     */
    public String getId() {
        return id;
    }

    /**
     * @return The instance of {@link KeyBindingConfig} used by this profile
     */
    public KeyBindingConfig getConfig() {
        return config;
    }

    /**
     * @return The instance of {@link GlobalConfig} used by this profile
     */
    public GlobalConfig getGlobal() {
        return global;
    }

    /**
     * Saves this profile
     */
    public void save() {
        config.save();
        global.save();
    }

    /**
     * Reloads this profile
     */
    public void reload() {
        config.reload();
        global.reload();
    }
}
