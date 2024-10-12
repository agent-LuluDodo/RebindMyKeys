package de.luludodo.rebindmykeys.gui.profile.screen;

import de.luludodo.rebindmykeys.gui.profile.widget.ProfileConfigWidget;
import de.luludodo.rebindmykeys.gui.screen.ConfigPopup;
import de.luludodo.rebindmykeys.gui.widget.ConfigWidget;
import de.luludodo.rebindmykeys.gui.widget.ResizableButtonWidget;
import de.luludodo.rebindmykeys.profiles.ProfileManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ProfileConfigPopup extends ConfigPopup<ProfileConfigPopup, ProfileConfigWidget> {
    public ProfileConfigPopup(@Nullable Screen parent) {
        super(parent, Text.translatable("rebindmykeys.gui.profile.title"));
    }

    @Override
    public void initBottom() {
        addResizableChild(getApplyButton(
                width -> 10,
                height -> height - 30,
                width -> width / 3 - 10,
                height -> 20
        ));
        addResizableChild(
                ResizableButtonWidget.builder(
                        this,
                        Text.translatable("rebindmykeys.gui.reload"),
                        button -> reload()
                ).dimensions(
                        width -> width / 3 + 5,
                        height -> height - 30,
                        width -> width / 3 - 10,
                        height -> 20
                ).build()
        );
        addResizableChild(getQuitButton(
                width -> width / 3 * 2,
                height -> height - 30,
                width -> width / 3 - 10,
                height -> 20
        ));
    }

    @Override
    public ProfileConfigWidget getConfigWidget(MinecraftClient client) {
        return new ProfileConfigWidget(client, this);
    }

    @Override
    public void save() {
        getConfigs().save();
    }

    @Override
    public void reset() {}

    public void reload() {
        askToSaveChanges(() -> {
            ProfileManager.reload();
            getConfigs().reload();
        });
    }

    @Override
    public boolean hasChanges() {
        return getConfigs().hasChanges();
    }
}
