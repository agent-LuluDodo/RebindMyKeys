package de.luludodo.rebindmykeys.modSupport;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.luludodo.rebindmykeys.gui.binding.screen.KeyBindingScreen;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return KeyBindingScreen::new;
    }
}