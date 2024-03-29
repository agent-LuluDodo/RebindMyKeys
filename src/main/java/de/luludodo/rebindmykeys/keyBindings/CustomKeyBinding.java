package de.luludodo.rebindmykeys.keyBindings;

import de.luludodo.rebindmykeys.util.KeyUtil;
import de.luludodo.rebindmykeys.util.MouseUtil;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomKeyBinding extends KeyBinding {
    private final Type type;

    public CustomKeyBinding(String translationKey, InputUtil.Key key, String category, Type type) {
        super(translationKey, InputUtil.Type.KEYSYM, -1, category);
        this.boundKey = key;
        this.defaultKey = key;
        this.type = type;
    }

    public CustomKeyBinding(String translationKey, int code, String category, Type type) {
        this(translationKey, InputUtil.Type.KEYSYM.createFromCode(code), category, type);
    }

    @Override
    public boolean equals(KeyBinding keyBinding) {
        return boundKey.equals(keyBinding.boundKey) && canConflictWith(keyBinding) && (!(keyBinding instanceof CustomKeyBinding customBinding) || customBinding.canConflictWith(this));
    }

    private static final Map<String, Type> specialKeyBindings = new HashMap<>();
    static {
        registerSpecialKeyBinding("key.fullscreen", Type.EVERYWHERE);
        registerSpecialKeyBinding("key.screenshot", Type.EVERYWHERE);
    }

    public static void registerSpecialKeyBinding(String translationKey, Type type) {
        specialKeyBindings.put(translationKey, type);
    }

    public static Type getType(KeyBinding keyBinding) {
        return keyBinding instanceof CustomKeyBinding custom? custom.getType() : specialKeyBindings.getOrDefault(keyBinding.getTranslationKey(), Type.GAME);
    }

    protected boolean canConflictWith(KeyBinding other) {
        return type.conflictsWith(getType(other));
    }

    public Type getType() {
        return type;
    }

    public static boolean conflicts(KeyBinding keyBinding1, KeyBinding keyBinding2) {
        if (keyBinding2 instanceof CustomKeyBinding) {
            return keyBinding2.equals(keyBinding1);
        } else {
            return keyBinding1.equals(keyBinding2);
        }
    }

    @Override
    public boolean matchesMouse(int button) {
        return super.matchesMouse(button);
    }

    @Override
    public boolean matchesKey(int keycode, int scancode) {
        return MouseUtil.wasMouseInput()? Objects.equals(getTranslationKey(), MouseUtil.getTranslationKey()) : super.matchesKey(keycode, scancode);
    }

    @Override
    public boolean isPressed() {
        if (boundKey == InputUtil.UNKNOWN_KEY)
            return false;
        return switch (boundKey.getCategory()) {
            case KEYSYM -> KeyUtil.keysmPressed(boundKey.getCode());
            case SCANCODE -> KeyUtil.scancodePressed(boundKey.getCode());
            case MOUSE -> KeyUtil.mousePressed(boundKey.getCode());
        };
    }
}
