package de.luludodo.rebindmykeys.modSupport;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.util.FabricUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.HashMap;
import java.util.Map;

public class VanillaKeyBindingHelper {
    public static class SpecialKey extends InputUtil.Key {
        public SpecialKey(int id, String translationKey) {
            super(translationKey, InputUtil.Type.KEYSYM, id);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof SpecialKey s && s.getCode() == getCode();
        }
    }
    private static final Map<String, KeyBinding> idToBinding = new HashMap<>();
    private static final Map<String, Integer> idToKeyId = new HashMap<>();
    private static final Map<Integer, String> keyIdToId = new HashMap<>();
    private static int curKeyId = -1000;
    private static boolean triggered = false;

    public static boolean isTriggered() {
        return triggered;
    }

    public static void trigger(String id) {
        triggered = true;
        RebindMyKeys.DEBUG.info("[{}] Triggering vanilla action: {} ({})", FabricUtil.getTickCount(), id, idToKeyId.get(id));
        KeyBinding.onKeyPressed(new SpecialKey(idToKeyId.get(id), "rebindmykeys.specialKey"));
        triggered = false;
    }

    public static void toggle(String id, boolean state) {
        triggered = true;
        RebindMyKeys.DEBUG.info("[{}] Triggering vanilla toggle: {} ({}) | {}", FabricUtil.getTickCount(), id, idToKeyId.get(id), state);
        KeyBinding.setKeyPressed(new SpecialKey(idToKeyId.get(id), "rebindmykeys.specialKey"), state);
        triggered = false;
    }

    public static void register(KeyBinding binding) {
        String id = binding.getTranslationKey();
        if (idToKeyId.containsKey(id))
            throw new IllegalArgumentException("Duplicate key: " + id);

        idToBinding.put(id, binding);
        idToKeyId.put(id, curKeyId);
        keyIdToId.put(curKeyId, id);
        curKeyId--;
    }

    public static void init() {
        triggered = true;
        for (KeyBinding binding : idToBinding.values()) {
            binding.setBoundKey(new SpecialKey(getKeyId(binding.getTranslationKey()), KeyBindingHelper.getBoundKeyOf(binding).getTranslationKey()));
        }
        KeyBinding.updateKeysByCode();
        triggered = false;
    }

    public static String getId(int id) {
        return keyIdToId.get(id);
    }

    public static int getKeyId(String id) {
        return idToKeyId.get(id);
    }

    public static KeyBinding getBinding(String id) {
        return idToBinding.get(id);
    }

    public static KeyBinding getBinding(int id) {
        return getBinding(getId(id));
    }
}
