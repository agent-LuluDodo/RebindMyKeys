package de.luludodo.rebindmykeys.util;

import net.minecraft.client.util.InputUtil;

public class KeyUtil {
    public static InputUtil.Key keysm(int code) {
        return InputUtil.Type.KEYSYM.createFromCode(code);
    }

    public static InputUtil.Key mouse(int code) {
        return InputUtil.Type.MOUSE.createFromCode(code);
    }
}
