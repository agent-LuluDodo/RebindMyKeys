package de.luludodo.rebindmykeys.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.api.config.serializer.MapSerializer;
import de.luludodo.rebindmykeys.gui.toasts.LuluToast;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A json-based config. The config is standalone except for the {@link MapSerializer}. Logs use the name {@code Lulu/JsonMapConfig}.
 * @param <K> The key class
 * @param <V> The value class
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public abstract class JsonMapConfig<K,V> {
    private static final Logger LOG = LoggerFactory.getLogger("Lulu/JsonMapConfig");

    /**
     * This exception is thrown if the config which is being read contains nothing.
     * This usually indicates a failure while previously saving the file.
     */
    @SuppressWarnings("unused")
    public static class EmptyFileException extends InvalidJsonException {
        public EmptyFileException() {
            super();
        }
        public EmptyFileException(String message) {
            super(message);
        }
        public EmptyFileException(Throwable cause) {
            super(cause);
        }
        public EmptyFileException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * This exception is thrown if the config which is being read contains malformed json.
     * This shouldn't happen unless the file corrupted or was edited manually.
     */
    public static class InvalidJsonException extends IOException {
        public InvalidJsonException() {
            super();
        }
        public InvalidJsonException(String message) {
            super(message);
        }
        public InvalidJsonException(Throwable cause) {
            super(cause);
        }
        public InvalidJsonException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private final Gson gson;
    private final File file;
    private final String filename;
    private final String[] oldFilenames;
    private final Type type;
    private boolean loaded = false;
    protected Map<K, V> content;
    /**
     * The constructor for JsonMapConfig. This is used to do one-time configurations such as the filename.
     * @param filename The filename which is used to save and read the config
     * @param serializer The serializer which is used to parse the json
     * @param oldFilenames A list of old filenames which will be moved to the new location and replace the current file if found
     */
    public JsonMapConfig(String filename, int version, MapSerializer<K, V> serializer, String... oldFilenames) {
        this(filename, version, serializer, true, oldFilenames);
    }

    /**
     * The constructor for JsonMapConfig. This is used to do one-time configurations such as the filename.
     * @param filename The filename which is used to save and read the config
     * @param load If the config should be loaded by the construction (using {@link JsonMapConfig#reload()}
     * @param serializer The serializer which is used to parse the json
     * @param oldFilenames A list of old filenames which will be moved to the new location and replace the current file if found
     */
    public JsonMapConfig(String filename, int version, MapSerializer<K, V> serializer, boolean load, String... oldFilenames) {
        this.filename = Path.of(filename).toString();
        serializer.setVersion(version);
        this.oldFilenames = oldFilenames;
        type = new TypeToken<Map<K, V>>(){}.getType();
        gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(type, serializer).create();
        file = FabricLoader.getInstance().getConfigDir().resolve(filename + ".json").toFile();

        if (load) {
            reload();
        }
    }

    /**
     * Returns the {@link Gson} instance used by this {@link JsonMapConfig}.
     * @return The {@link Gson} instance used by this {@link JsonMapConfig}.
     */
    protected Gson getGson() {
        return gson;
    }

    /**
     * Returns the {@link File} under which the config gets saved.
     * @return The {@link File} under which the config gets saved.
     */
    protected File getFile() {
        return file;
    }

    /**
     * Returns the {@code filename} under which the config is saved.
     * @return The {@code filename} under which the config is saved.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Returns old {@code filenames} which the config tries to load.
     * @return Old {@code filenames} which the config tries to load.
     */
    public String[] getOldFilenames() {
        return oldFilenames;
    }

    /**
     * Returns the {@link Type} used for the {@link Gson} instance.
     * @return The {@link Type} used for the {@link Gson} instance.
     */
    protected Type getType() {
        return type;
    }

    /**
     * Checks if the config has been loaded.
     * @return If the config has been loaded.
     */
    public boolean isLoaded() {
        return loaded;
    }

    private void fillWithDefaults(Map<K, V> map) {
        getDefaults().forEach(map::putIfAbsent);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void tryRestoreOldSettings() {
        if (getFile().exists()) return;
        for (String oldFilename : getOldFilenames()) {
            File oldFile = FabricLoader.getInstance().getConfigDir().resolve(oldFilename + ".json").toFile();
            if (!oldFile.exists()) continue;
            try {
                getFile().getParentFile().mkdirs();
                FileUtils.moveFile(oldFile, getFile());
                LOG.info("Restored old settings from {}.json to {}.json!", oldFilename, getFilename());
                break;
            } catch (IOException e) {
                LuluToast.showAndLogError(LOG, "Cannot restore old settings for " + getFilename() + ".json from " + oldFilename + ".json!", null);
            }
        }
    }

    private Map<K, V> read(File file) throws IOException {
        String contentToParse = FileUtils.readFileToString(file, Charset.defaultCharset());
        if (contentToParse.isBlank()) {
            throw new EmptyFileException();
        }
        try {
            return getGson().fromJson(contentToParse, getType());
        } catch (Exception e) {
            throw new InvalidJsonException(e);
        }
    }

    protected abstract Map<K, V> getDefaults();

    /**
     * Resets the config to its default state using {@link JsonMapConfig#getDefaults()} to get the default values.
     * This will save the config if editMode isn't enabled.
     */
    public void reset() {
        if (editMode) {
            editedContent = new HashMap<>(getDefaults());
            editModeChanges = true;
        } else {
            content = new HashMap<>(getDefaults());
            save();
        }
    }

    /**
     * Reloads the config. Tries to restore old configs first and will override the current config if one is found.
     */
    public boolean reload() {
        tryRestoreOldSettings();
        boolean successful = false;
        try {
            content = read(getFile());
            fillWithDefaults(content);
            LOG.info("Loaded {}.json!", getFilename());
            successful = true;
        } catch (EmptyFileException e) {
            LuluToast.showAndLogError(LOG, "Couldn't read config " + getFilename() + ".json, because the file is empty!", null);
            content = new HashMap<>(getDefaults());
        } catch (InvalidJsonException e) {
            LuluToast.showAndLogError(LOG, "Couldn't parse config " + getFilename() + ".json!", e);
            content = new HashMap<>(getDefaults());
        } catch (NoSuchFileException | FileNotFoundException e) {
            LOG.warn("Couldn't find config {}.json!", getFilename()); // Warning since it could be the first time launching
            content = new HashMap<>(getDefaults());
        } catch (IOException e) {
            LuluToast.showAndLogError(LOG, "Couldn't read config " + getFilename() + ".json!", e);
            content = new HashMap<>(getDefaults());
        }
        LOG.info("Content: {} entries", content.size());
        loaded = true;
        return successful;
    }

    /**
     * Saves the config to the file specified in the initializer. Logs errors in case of failure.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean save() {
        try {
            if (!getFile().exists()) {
                getFile().getParentFile().mkdirs();
                getFile().createNewFile();
            }
        } catch (IOException e) {
            LuluToast.showAndLogError(LOG, "Couldn't create config " + getFilename() + ".json!", e);
            return false;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFile(), false))) {
            writer.write(getGson().toJson(content, getType()));
            LOG.info("Saved config {}.json!", getFilename());
            return true;
        } catch (IOException e) {
            LuluToast.showAndLogError(LOG, "Couldn't save config " + getFilename() + ".json!", e);
            return false;
        }
    }

    /**
     * Deletes the config and any empty parent directories up to the fabric config directory.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean delete() {
        if (!getFile().exists()) {
            LOG.info("Can't delete config {}.json because it doesn't exist!", getFilename());
            return true;
        }

        if (!getFile().delete()) {
            LuluToast.showAndLogWarn(LOG, "Failed to delete config " + getFilename() + ".json!", null);
            return false;
        }
        LOG.info("Deleted config {}.json!", getFilename());
        Path path = getFile().toPath().getParent();
        Path configDir = FabricLoader.getInstance().getConfigDir();
        while (path.startsWith(configDir) && !path.equals(configDir)) {
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
                if (!dirStream.iterator().hasNext()) {
                    path.toFile().delete();
                    LOG.info("Deleted empty parent directory {}!", configDir.relativize(path));
                    path = path.getParent();
                } else {
                    break;
                }
            } catch (IOException e) {
                LuluToast.showAndLogError(LOG, "Couldn't read directory data while deleting config " + getFilename() + ".json!", e);
                return false;
            }
        }
        return true;
    }

    private Map<K, V> editedContent;
    private boolean editMode = false;
    private boolean editModeChanges = false;
    /**
     * Activates editMode allowing changes to be made using {@link JsonMapConfig#set(Object, Object)} and {@link JsonMapConfig#reset()}.
     * These changes won't affect the real content but can be applied using {@link JsonMapConfig#applyEditModeChanges()}.
     * You can deactivate editMode using {@link JsonMapConfig#deactivateEditMode()}.
     * @see JsonMapConfig#deactivateEditMode()
     * @see JsonMapConfig#isInEditMode()
     * @see JsonMapConfig#hasEditModeChanges()
     * @see JsonMapConfig#setEditModeChanges(boolean)
     * @see JsonMapConfig#setPreviewChanges(boolean)
     * @see JsonMapConfig#getPreviewChanges()
     */
    public void activateEditMode() {
        if (editMode)
            throw new IllegalStateException("Cannot activate edit mode because it is already activated!");

        RebindMyKeys.DEBUG.info("activateEditMode | {}", getFilename());

        editedContent = new HashMap<>(content);
        editMode = true;
        editModeChanges = false;
    }

    /**
     * Applies changes made in editMode to the real content.
     */
    public void applyEditModeChanges() {
        if (!editMode)
            throw new IllegalStateException("Cannot save edit mode changes because edit mode is deactivated!");

        content = editedContent;
        editModeChanges = false;
        save();
    }

    /**
     * Use this to override editModeChanges in cases where editedContent is edited externally,
     * or if it's certain no changes were made.
     * @param editModeChanges The new value
     * @see JsonMapConfig#hasEditModeChanges()
     */
    public void setEditModeChanges(boolean editModeChanges) {
        this.editModeChanges = editModeChanges;
    }

    /**
     * Tracks changes made in editMode using {@link JsonMapConfig#setEditModeChanges(boolean)},
     * {@link JsonMapConfig#set(Object, Object)} and {@link JsonMapConfig#reset()}.
     * @return If any changes were tracked
     * @see JsonMapConfig#setEditModeChanges(boolean)
     */
    public boolean hasEditModeChanges() {
        return editModeChanges;
    }

    /**
     * Deactivates editMode and deletes all changes which haven't been applied using {@link JsonMapConfig#applyEditModeChanges()}.
     * @see JsonMapConfig#applyEditModeChanges()
     * @see JsonMapConfig#activateEditMode()
     */
    public void deactivateEditMode() {
        if (!editMode)
            throw new IllegalStateException("Cannot deactivate edit mode because it is already deactivated!");

        RebindMyKeys.DEBUG.info("deactivateEditMode | {}", getFilename());

        editMode = false;
        editedContent = null;
        editModeChanges = false;
    }

    /**
     * Checks if editMode is active.
     * @return If editMode is active
     * @see JsonMapConfig#activateEditMode()
     * @see JsonMapConfig#deactivateEditMode()
     */
    public boolean isInEditMode() {
        return editMode;
    }

    private boolean previewChanges = false;
    /**
     * Allows you to preview changes made in editMode.
     * @param previewChanges {@code true} to active the preview and {@code false} to deactivate it
     * @see JsonMapConfig#getPreviewChanges()
     * @see JsonMapConfig#activateEditMode()
     */
    public void setPreviewChanges(boolean previewChanges) {
        this.previewChanges = previewChanges;
    }

    /**
     * Checks if changes are being previewed. Returns {@code true} even if editMode isn't active.
     * @return if changes are being previewed.
     * @see JsonMapConfig#setPreviewChanges(boolean)
     */
    public boolean getPreviewChanges() {
        return previewChanges;
    }

    private Map<K, V> getContentForPreview() {
        if (previewChanges && editMode) {
            return editedContent;
        } else {
            return content;
        }
    }

    /**
     * Gets a value from the config using a key.
     * @param key The key
     * @return The value
     */
    public V get(K key) {
        return getContentForPreview().get(key);
    }

    /**
     * Checks if the config contains a specific key.
     * @param key The key
     * @return If the config contains said key
     */
    public boolean contains(K key) {
        return getContentForPreview().containsKey(key);
    }

    /**
     * Sets the value associated with the key. This will replace old values and create new ones.
     * This will save the config if editMode isn't enabled.
     * @param key The key
     * @param value The value
     */
    public void set(K key, V value) {
        if (editMode) {
            editedContent.put(key, value);
            editModeChanges = true;
        } else {
            content.put(key, value);
            save();
        }
    }

    /**
     * Returns all keys present in this config.
     * @return All keys present in this config.
     */
    public Set<K> options() {
        return getContentForPreview().keySet();
    }

    /**
     * Does the specified action for every entry in the config.
     * @param action The action
     */
    public void forEach(BiConsumer<K, V> action) {
        getContentForPreview().forEach(action);
    }
}
