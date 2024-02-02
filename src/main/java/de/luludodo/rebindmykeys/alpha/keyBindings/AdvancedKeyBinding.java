package de.luludodo.rebindmykeys.alpha.keyBindings;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdvancedKeyBinding extends KeyBinding {
    private List<KeyCombo> defaultCombos;
    private List<Pair<Integer, Integer>> defaultSimpleKeys;
    private boolean defaultInverted;
    private List<KeyCombo> keyCombos;
    private List<Pair<Integer, Integer>> simpleKeys;
    private boolean inverted;
    public AdvancedKeyBinding(String translationKey, List<KeyCombo> keyCombos, String category, List<Pair<Integer, Integer>> simpleKeys) {
        super(translationKey, getSimpleKey(keyCombos, simpleKeys).getCategory(), getSimpleKey(keyCombos, simpleKeys).getCode(), category);
        defaultCombos = keyCombos;
        defaultSimpleKeys = simpleKeys;
        this.keyCombos = keyCombos;
        this.simpleKeys = simpleKeys;
    }

    public boolean isActive() {
        for (KeyCombo keyCombo : keyCombos) {
            if (keyCombo.isActive())
                return true;
        }
        return false;
    }

    public void onKey(InputUtil.Key key, int action) {
        keyCombos.forEach(keyCombo -> keyCombo.onKey(key, action));
    }

    public List<KeyCombo> getKeyCombos() {
        return Collections.unmodifiableList(keyCombos);
    }
    public int addKeyCombo(KeyCombo keyCombo) {
        keyCombos.add(keyCombo);
        return keyCombos.indexOf(keyCombo);
    }
    public void removeKeyCombo(int index) {
        keyCombos.remove(index);
    }

    public void reset() {
        if (isDefault())
            return;
        keyCombos = new ArrayList<>(defaultCombos);
        simpleKeys = new ArrayList<>(defaultSimpleKeys);
    }

    private static InputUtil.Key getSimpleKey(List<KeyCombo> keyCombos, List<Pair<Integer, Integer>> simpleKeys) {
        if (simpleKeys.isEmpty())
            return InputUtil.UNKNOWN_KEY;
        Pair<Integer, Integer> simpleKey = simpleKeys.get(0);
        return keyCombos.get(simpleKey.getA()).getKey(simpleKey.getB());
    }
    private InputUtil.Key getSimpleKey() {
        return getSimpleKey(keyCombos, simpleKeys);
    }

    @Override
    public void setBoundKey(InputUtil.Key key) {
        super.setBoundKey(key);
        simpleKeys.forEach(simpleKey -> keyCombos.get(simpleKey.getA()).editKey(simpleKey.getB(), boundKey));
    }

    @Override
    public Text getBoundKeyLocalizedText() {
        return getSimpleKey().getLocalizedText();
    }
    @Override
    public String getBoundKeyTranslationKey() {
        return getSimpleKey().getTranslationKey();
    }

    public boolean isDefault() {
        return CollectionUtil.equalsIgnoreOrder(defaultCombos, keyCombos) &&
                CollectionUtil.equalsIgnoreOrder(defaultSimpleKeys, simpleKeys);
    }

    public boolean equals(AdvancedKeyBinding keyBinding) {
        return CollectionUtil.equalsIgnoreOrder(defaultCombos, keyBinding.defaultCombos) &&
                CollectionUtil.equalsIgnoreOrder(defaultSimpleKeys, keyBinding.defaultSimpleKeys) &&
                CollectionUtil.equalsIgnoreOrder(keyCombos, keyBinding.keyCombos) &&
                CollectionUtil.equalsIgnoreOrder(simpleKeys, keyBinding.simpleKeys);
    }

    public void load(JsonElement json) {

    }

    public JsonElement save() {
        return null;
    }
}
