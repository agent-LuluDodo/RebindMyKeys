package de.luludodo.rebindmykeys.gui.binding.screen;

import de.luludodo.rebindmykeys.gui.binding.widget.CompatibilityWidget;
import de.luludodo.rebindmykeys.gui.screen.ConfigPopup;
import de.luludodo.rebindmykeys.modSupport.VanillaKeyBindingWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class CompatibilityPopup extends ConfigPopup<CompatibilityPopup, CompatibilityWidget> {
    private final VanillaKeyBindingWrapper binding;
    public CompatibilityPopup(KeyBindingScreen parent, VanillaKeyBindingWrapper binding) {
        super(parent, Text.translatable("rebindmykeys.gui.compatibility.title"));
        this.binding = binding;
    }

    @Override
    public CompatibilityWidget getConfigWidget(MinecraftClient client) {
        return new CompatibilityWidget(client, this, binding);
    }

    @Override
    public void save() {
        getConfigs().save();
    }

    @Override
    public void reset() {
        getConfigs().reset();
    }

    @Override
    public boolean hasChanges() {
        return getConfigs().hasChanges();
    }
}
