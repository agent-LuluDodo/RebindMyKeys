package de.luludodo.rebindmykeys.config;

import de.luludodo.rebindmykeys.api.config.JsonMapConfig;
import de.luludodo.rebindmykeys.config.serializer.GlobalConfigSerializer;
import de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget.sort.SortAfter;
import de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget.sort.SortOrder;
import de.luludodo.rebindmykeys.profiles.ProfileManager;

import java.util.HashMap;
import java.util.Map;

public class GlobalConfig extends JsonMapConfig<String, Object> {
    public static final String NAME = "name";
    public static final String HIDE_ESSENTIAL_KEYS = "hideEssentialKeys";
    public static final String MULTI_CLICK_DELAY_MS = "multiClickDelayMs";
    public static final String DEBUG_CRASH_TIME = "debugCrashTime";
    public static final String DEBUG_CRASH_JAVA_TIME = "debugCrashJavaTime";
    public static final String VERTICAL_SCROLL_SPEED_MODIFIER = "verticalScrollSpeedModifier";
    public static final String HORIZONTAL_SCROLL_SPEED_MODIFIER = "horizontalScrollSpeedModifier";
    public static final String SHOW_CONFIRM_POPUPS = "showConfirmPopups";
    public static final String SORT_AFTER = "sortAfter";
    public static final String SORT_ORDER = "sortOrder";

    public static GlobalConfig getCurrent() {
        return ProfileManager.getCurrentProfile().getGlobal();
    }

    public GlobalConfig(String uuid) {
        super(ProfileManager.getProfilesDirRelative().resolve(uuid).resolve("global").toString(), 1, new GlobalConfigSerializer());
    }
    private GlobalConfig(String uuid, Map<String, Object> content) {
        super(ProfileManager.getProfilesDirRelative().resolve(uuid).resolve("global").toString(), 1, new GlobalConfigSerializer(), false);
        this.content = new HashMap<>(content);
    }

    @Override
    protected Map<String, Object> getDefaults() {
        return Map.of(
                NAME, "null",
                HIDE_ESSENTIAL_KEYS, true,
                MULTI_CLICK_DELAY_MS, 500L,
                DEBUG_CRASH_TIME, 10000L,
                DEBUG_CRASH_JAVA_TIME, 10000L,
                VERTICAL_SCROLL_SPEED_MODIFIER, 1d,
                HORIZONTAL_SCROLL_SPEED_MODIFIER, 1d,
                SHOW_CONFIRM_POPUPS, true,
                SORT_AFTER, SortAfter.CATEGORY,
                SORT_ORDER, SortOrder.ASCENDING
        );
    }

    public String getName() {
        return (String) get(NAME);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public boolean getHideEssentialKeys() {
        return (boolean) get(HIDE_ESSENTIAL_KEYS);
    }
    public void setHideEssentialKeys(boolean value) {
        set(HIDE_ESSENTIAL_KEYS, value);
    }
    public long getMultiClickDelayMs() {
        return (long) get(MULTI_CLICK_DELAY_MS);
    }
    public void setMultiClickDelayMs(long value) {
        set(MULTI_CLICK_DELAY_MS, value);
    }
    public long getDebugCrashTime() {
        return (long) get(DEBUG_CRASH_TIME);
    }
    public void setDebugCrashTime(long value) {
        set(DEBUG_CRASH_TIME, value);
    }
    public long getDebugCrashJavaTime() {
        return (long) get(DEBUG_CRASH_JAVA_TIME);
    }
    public void setDebugCrashJavaTime(long value) {
        set(DEBUG_CRASH_JAVA_TIME, value);
    }
    public double getVerticalScrollSpeedModifier() {
        return (double) get(VERTICAL_SCROLL_SPEED_MODIFIER);
    }
    public void setVerticalScrollSpeedModifier(double value) {
        set(VERTICAL_SCROLL_SPEED_MODIFIER, value);
    }
    public double getHorizontalScrollSpeedModifier() {
        return (double) get(HORIZONTAL_SCROLL_SPEED_MODIFIER);
    }
    public void setHorizontalScrollSpeedModifier(double value) {
        set(HORIZONTAL_SCROLL_SPEED_MODIFIER, value);
    }
    public boolean getShowConfirmPopups() {
        return (boolean) get(SHOW_CONFIRM_POPUPS);
    }
    public void setShowConfirmPopups(boolean value) {
        set(SHOW_CONFIRM_POPUPS, value);
    }
    public SortAfter getSortAfter() {
        return (SortAfter) get(SORT_AFTER);
    }
    public void setSortAfter(SortAfter value) {
        set(SORT_AFTER, value);
    }
    public SortOrder getSortOrder() {
        return (SortOrder) get(SORT_ORDER);
    }
    public void setSortOrder(SortOrder value) {
        set(SORT_ORDER, value);
    }

    public GlobalConfig duplicate(String name) {
        return new GlobalConfig(name, content);
    }
}
