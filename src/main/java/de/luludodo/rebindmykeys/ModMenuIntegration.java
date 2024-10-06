package de.luludodo.rebindmykeys;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.luludodo.rebindmykeys.gui.globalConfig.screen.GlobalConfigPopup;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return GlobalConfigPopup::new;
    }
}