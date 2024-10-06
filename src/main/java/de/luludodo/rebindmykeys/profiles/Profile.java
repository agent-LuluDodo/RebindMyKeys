package de.luludodo.rebindmykeys.profiles;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.config.GlobalConfig;
import de.luludodo.rebindmykeys.config.KeyBindingConfig;

import java.util.UUID;

/**
 * Allows for easy switching between settings.
 * @see ProfileManager
 */
public class Profile {
    private final KeyBindingConfig config;
    private final GlobalConfig global;
    private final UUID uuid;
    private final boolean editable;

    /**
     * Initializes a new profile. <br>
     * <b>Use {@link ProfileManager#create(UUID, String)} instead of this.</b>
     * @param uuid The uuid of this profile
     * @see ProfileManager#create(UUID, String)
     */
    protected Profile(UUID uuid, boolean renameable) {
        this(uuid, renameable, new KeyBindingConfig(uuid.toString()), new GlobalConfig(uuid.toString()));
    }
    protected Profile(UUID uuid, boolean editable, KeyBindingConfig config, GlobalConfig global) {
        this.uuid = uuid;
        this.config = config;
        this.global = global;
        this.editable = editable;
    }

    /**
     * @return The filename of this profile
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * @return The name of this profile
     */
    public String getName() {
        return global.getName();
    }

    public boolean isEditable() {
        return editable;
    }

    /**
     * Sets the name of the profile.
     * <b>Note: this update the profile in the ProfileManager</b>
     * @param name The new name
     */
    public void rename(String name) {
        if (!editable) {
            RebindMyKeys.DEBUG.warn("Tried to rename non-editable profile {} ({})!", getName(), getUUID());
            return;
        }
        global.setName(name);
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

    public void delete() {
        if (!editable) {
            RebindMyKeys.DEBUG.warn("Tried to delete non-editable profile {}!", getUUID());
            return;
        }
        if (config != null)
            config.delete();
        if (global != null)
            global.delete();
    }
}
