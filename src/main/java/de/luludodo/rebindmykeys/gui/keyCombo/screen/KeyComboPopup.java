package de.luludodo.rebindmykeys.gui.keyCombo.screen;

import de.luludodo.rebindmykeys.config.KeyBindingConfig;
import de.luludodo.rebindmykeys.gui.binding.screen.KeyBindingScreen;
import de.luludodo.rebindmykeys.gui.binding.screen.SettingsPopup;
import de.luludodo.rebindmykeys.gui.keyCombo.widget.KeyComboWidget;
import de.luludodo.rebindmykeys.gui.screen.ConfigPopup;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import de.luludodo.rebindmykeys.util.KeyBindingUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class KeyComboPopup extends ConfigPopup<KeyComboPopup, KeyComboWidget> {
    private final KeyCombo combo;
    public KeyComboPopup(SettingsPopup parent, KeyCombo combo) {
        super(parent, Text.translatable("rebindmykeys.gui.keyCombo.title"));
        this.combo = combo;
        setWidth(width -> 240);
    }

    @Override
    public KeyComboWidget getConfigWidget(MinecraftClient client) {
        return new KeyComboWidget(client, this, combo);
    }

    @Override
    public void init() {
        super.init();
        ((SettingsPopup) getParent()).resize();
        resize();
    }

    @Override
    public void save() {
        getConfigs().save();
        KeyBindingUtil.calcIncompatibleUUIDs();
        ((KeyBindingScreen) ((SettingsPopup) getParent()).getParent()).reloadEntries();
        KeyBindingConfig.getCurrent().save();
    }

    @Override
    public void reset() {
        getConfigs().reload();
    }

    @Override
    public boolean hasChanges() {
        return getConfigs().hasChanges();
    }
}
