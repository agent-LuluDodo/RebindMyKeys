package de.luludodo.rebindmykeys.keybindings.keyCombo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.reference.KeyReference;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.ComboSettings;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import net.minecraft.client.util.InputUtil;

import java.util.*;

public class KeyCombo implements JsonSavable {
    private ComboSettings settings;
    private List<Key> keys;
    private boolean oldPressed = false;
    private boolean active;
    private final String id;
    private final UUID uuid = UUID.randomUUID();
    public KeyCombo(String id, List<Key> keys, ComboSettings settings) {
        this.id = id;
        this.keys = new ArrayList<>(keys);
        this.settings = settings;
        init();
    }

    public String getId() {
        return id;
    }

    public UUID getUUID() {
        return uuid; // Used to filter
    }

    /**
     * Promises to always return the same value on consecutive calls without other state changes.
     */
    public boolean wasTriggered() {
        return settings.operationMode().wasTriggered() && contextValid;
    }

    public void done() {
        settings.operationMode().done();
    }

    private void init() {
        contextValid = settings.contextValid();
        active = settings.operationMode().isActive() && contextValid;
    }

    public boolean move(Key key, int targetIndex) {
        return CollectionUtil.CList.move(keys, key, targetIndex);
    }

    public void addKey(Key key) {
        keys.add(0, key);
    }

    public boolean removeKey(Key key) {
        return keys.remove(key);
    }

    public List<Key> getKeys() {
        return Collections.unmodifiableList(keys);
    }

    public void setKeys(List<Key> keys) {
        this.keys = new ArrayList<>(keys);
    }

    public void onKeyDown(InputUtil.Key key) {
        if (settings.orderSensitive()) {
            int left = keys.size();
            boolean allPressed = true;
            for (Key k : keys) {
                if (--left == 0) {
                    if (allPressed) k.onKeyDown(key);
                } else {
                    if (!k.isPressed()) allPressed = false;
                    k.onKeyDown(key);
                }
            }
        } else {
            keys.forEach(k -> k.onKeyDown(key));
        }
    }

    public void onKeyUp(InputUtil.Key key) {
        keys.forEach(k -> k.onKeyUp(key));
    }

    public void updatePressed() {
        boolean pressed = isPressed();
        if (oldPressed == pressed) return;
        if (pressed) {
            settings.operationMode().onKeyDown();
        } else {
            settings.operationMode().onKeyUp();
        }
        oldPressed = pressed;
    }

    public int getLength() {
        return keys.size();
    }

    public void updateActive() {
        active = settings.operationMode().isActive();
    }

    private boolean contextValid = false;
    public boolean checkContext() {
        boolean oldContextValid = contextValid;
        contextValid = settings.contextValid();
        return contextValid != oldContextValid;
    }

    private Set<UUID> incompatibleUUIDs;
    public void calcIncompatibleUUIDs(Set<KeyCombo> keyCombos) {
        incompatibleUUIDs = new HashSet<>();
        Set<String> incompatibleIds = new HashSet<>();
        for (Key key : keys) {
            if (key instanceof KeyReference kr) {
                incompatibleIds.add(kr.getReference());
            }
        }
        for (KeyCombo keyCombo : keyCombos) {
            if (keyCombo == this) continue;
            if (incompatibleIds.contains(keyCombo.getId()) || (getLength() > keyCombo.getLength() && new HashSet<>(getKeys()).containsAll(keyCombo.getKeys()))) {
                incompatibleUUIDs.add(keyCombo.getUUID());
            }
        }
    }

    public Set<UUID> getIncompatibleUUIDs() {
        if (wasTriggered()) {
            return incompatibleUUIDs;
        } else {
            return new HashSet<>();
        }
    }

    //private static final Set<String> TARGET_IDS = Set.of("rebindmykeys.key.debug.menu", "rebindmykeys.key.debug.reloadChunks", "key.left");

    private boolean filtered = false;
    public void filter(Set<UUID> invalidUUIDs) {
        if (settings.skipFilter()) {
            filtered = false;
            //if (TARGET_IDS.contains(id))
            //    RebindMyKeys.DEBUG.info("filtered='{}' skipFilter='{}' id='{}' uuid='{}' incompatibleUUIDs='{}'", false, true, id, uuid, CollectionUtil.toString(incompatibleUUIDs));
            return;
        }

        filtered = invalidUUIDs.contains(uuid);
        if (filtered) {
            settings.operationMode().deactivate();
            active = false;
        }

        //if (TARGET_IDS.contains(id))
        //    RebindMyKeys.DEBUG.info("filtered='{}' skipFilter='{}' id='{}' uuid='{}' incompatibleUUIDs='{}'", filtered, false, id, uuid, CollectionUtil.toString(incompatibleUUIDs));
    }

    protected boolean isFiltered() {
        return filtered;
    }

    public boolean isPressed() { // Can't cache here cause of KeyReference's :)
        return CollectionUtil.allConditions(keys, Key::isPressed);
    }

    public boolean isActive() {
        return active && contextValid;
    }

    public ComboSettings getSettings() {
        return settings;
    }

    public void setSettings(ComboSettings settings) {
        this.settings = settings;
        init();
    }

    @Override
    public JsonElement save() {
        JsonUtil.ArrayBuilder keysBuilder = JsonUtil.array(keys.size());
        for (Key key : keys) {
            keysBuilder.add(Key.save(key));
        }

        return JsonUtil.object()
                .add("id", id)
                .add("settings", settings)
                .add("keys", keysBuilder.build())
                .build();
    }

    public static KeyCombo load(JsonElement json) {
        JsonUtil.ObjectLoader loader = JsonUtil.object(json);
        return new KeyCombo(
                loader.get("id", String.class),
                loader.array("keys").toList(Key::load),
                loader.get("settings", ComboSettings::load)
        );
    }

    public void release() {
        keys.forEach(Key::release);
    }

    public boolean isUnbound() {
        return keys.isEmpty();
    }
}
