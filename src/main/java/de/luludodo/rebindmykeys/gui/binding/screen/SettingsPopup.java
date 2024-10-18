package de.luludodo.rebindmykeys.gui.binding.screen;

import de.luludodo.rebindmykeys.config.KeyBindingConfig;
import de.luludodo.rebindmykeys.gui.binding.widget.SettingsWidget;
import de.luludodo.rebindmykeys.gui.screen.ConfigPopup;
import de.luludodo.rebindmykeys.keybindings.keyCombo.ComboSettingsEditor;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import de.luludodo.rebindmykeys.util.KeyBindingUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class SettingsPopup extends ConfigPopup<SettingsPopup, SettingsWidget> {
    private final KeyCombo combo;
    private final ComboSettingsEditor editor;
    public SettingsPopup(@NotNull KeyBindingScreen parent, KeyCombo combo) {
        super(parent, Text.translatable("rebindmykeys.gui.settings.title"));
        setRenderTitle(true);
        this.combo = combo;
        editor = new ComboSettingsEditor(combo);
    }

    public SettingsWidget getConfigWidget(MinecraftClient client) {
        return new SettingsWidget(client, this, editor);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void save() {
        editor.apply();
        KeyBindingUtil.calcIncompatibleUUIDs();
        ((KeyBindingScreen) getParent()).reloadEntries();
        KeyBindingConfig.getCurrent().save();
    }

    @Override
    public void reset() {
        editor.reload();
        editor.setNoChanges();
        getConfigs().reload();
    }

    public KeyCombo getCombo() {
        return combo;
    }

    @Override
    public boolean hasChanges() {
        return editor.hasChanges();
    }
}
