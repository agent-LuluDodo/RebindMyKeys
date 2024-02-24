package de.luludodo.rebindmykeys.util;

import net.minecraft.text.*;

public class TranslationUtil {
    private static final String MOD_ID = "rebindmykeys";
    public static final String TOGGLE = translation("mode.toggle");
    public static final String HOLD = translation("mode.hold");
    public static final String ACTIVATE = translation("mode.activate");
    public static final String DEACTIVATE = translation("mode.deactivate");
    public static final String PRESS = translation("mode.press");
    public static final String RELEASE = translation("mode.release");
    public static final String BOTH = translation("mode.boths");
    public static final String UNKNOWN = translation("mode.unknown");

    public static String translation(String id) {
        return (id == null || id.isEmpty())? MOD_ID : MOD_ID + "." + id;
    }

    public static MutableText concat(Text... texts) {
        MutableText result = Text.empty();
        for (Text text : texts) {
            result.append(text);
        }
        return result;
    }
}
