package de.luludodo.rebindmykeys.gui.globalConfig.screen;

import de.luludodo.rebindmykeys.config.GlobalConfig;
import de.luludodo.rebindmykeys.gui.binding.screen.KeyBindingScreen;
import de.luludodo.rebindmykeys.gui.globalConfig.widget.GlobalConfigWidget;
import de.luludodo.rebindmykeys.gui.screen.ConfigPopup;
import de.luludodo.rebindmykeys.gui.widget.ConfigWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class GlobalConfigPopup extends ConfigPopup {
    public GlobalConfigPopup(@Nullable Screen parent) {
        super(parent, Text.translatable("rebindmykeys.gui.global.title"));
        setRenderTitle(true);
    }

    @Override
    public ConfigWidget getConfigWidget(MinecraftClient client) {
        return new GlobalConfigWidget(client, this);
    }

    @Override
    public void activateEditMode() {
        GlobalConfig.getCurrent().activateEditMode();
    }

    @Override
    public void deactivateEditMode() {
        GlobalConfig.getCurrent().deactivateEditMode();
    }

    @Override
    public void save() {
        GlobalConfig.getCurrent().applyEditModeChanges();

        if (getParent() instanceof KeyBindingScreen keyBindingScreen)
            keyBindingScreen.reloadEntries();
    }

    @Override
    public void reset() {
        GlobalConfig.getCurrent().reset();
    }

    @Override
    public boolean hasChanges() {
        return GlobalConfig.getCurrent().hasEditModeChanges();
    }
}
