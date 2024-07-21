package de.luludodo.rebindmykeys.config;

import de.luludodo.rebindmykeys.api.config.JsonMapConfig;
import de.luludodo.rebindmykeys.config.serializer.GlobalConfigSerializer;
import de.luludodo.rebindmykeys.profiles.ProfileManager;

import java.util.Map;

public class GlobalConfig extends JsonMapConfig<String, Object> {
    public static final String MULTI_CLICK_DELAY_MS = "multiClickDelayMs";
    public static final String DEBUG_CRASH_TIME = "debugCrashTime";
    public static final String DEBUG_CRASH_JAVA_TIME = "debugCrashJavaTime";
    public static final String VERTICAL_SCROLL_SPEED_MODIFIER = "verticalScrollSpeedModifier";
    public static final String HORIZONTAL_SCROLL_SPEED_MODIFIER = "horizontalScrollSpeedModifier";

    public GlobalConfig(String name) {
        super(ProfileManager.getProfilesDir().resolve(name).resolve("global").toString(), new GlobalConfigSerializer());
    }

    @Override
    protected Map<String, Object> getDefaults() {
        return Map.of(
                MULTI_CLICK_DELAY_MS, 500L,
                DEBUG_CRASH_TIME, 10000L,
                DEBUG_CRASH_JAVA_TIME, 10000L,
                VERTICAL_SCROLL_SPEED_MODIFIER, 1d,
                HORIZONTAL_SCROLL_SPEED_MODIFIER, 1d
        );
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
}
