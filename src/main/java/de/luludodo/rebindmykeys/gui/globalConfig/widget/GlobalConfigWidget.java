package de.luludodo.rebindmykeys.gui.globalConfig.widget;

import de.luludodo.rebindmykeys.config.GlobalConfig;
import de.luludodo.rebindmykeys.gui.globalConfig.screen.GlobalConfigPopup;
import de.luludodo.rebindmykeys.gui.widget.ConfigWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class GlobalConfigWidget extends ConfigWidget<GlobalConfigWidget, GlobalConfigPopup> {

    public GlobalConfigWidget(MinecraftClient client, GlobalConfigPopup parent) {
        super(client, parent);
    }

    @Override
    public void loadEntries() {
        addEntry(
                new OnOffButtonEntry(
                        Text.translatable("rebindmykeys.gui.global.hideEssentialKeys"),
                        Text.translatable("rebindmykeys.gui.global.hideEssentialKeys.on"),
                        Text.translatable("rebindmykeys.gui.global.hideEssentialKeys.off"),
                        GlobalConfig.getCurrent().getHideEssentialKeys(),
                        (button, newValue) -> GlobalConfig.getCurrent().setHideEssentialKeys(newValue)
                )
        );
        addEntry(
                new NumberEntry<>(
                        Text.translatable("rebindmykeys.gui.global.multiClickDelayMs"),
                        GlobalConfig.getCurrent().getMultiClickDelayMs(),
                        value -> Text.translatable("rebindmykeys.gui.global.ms"),
                        Long::valueOf,
                        newValue -> newValue > 0,
                        newValue -> GlobalConfig.getCurrent().setMultiClickDelayMs(newValue)
                )
        );
        addEntry(
                new NumberEntry<>(
                        Text.translatable("rebindmykeys.gui.global.debugCrashTime"),
                        GlobalConfig.getCurrent().getDebugCrashTime(),
                        value -> Text.translatable("rebindmykeys.gui.global.ms"),
                        Long::valueOf,
                        newValue -> newValue > 0,
                        newValue -> GlobalConfig.getCurrent().setDebugCrashTime(newValue)
                )
        );
        addEntry(
                new NumberEntry<>(
                        Text.translatable("rebindmykeys.gui.global.debugCrashJavaTime"),
                        GlobalConfig.getCurrent().getDebugCrashJavaTime(),
                        value -> Text.translatable("rebindmykeys.gui.global.ms"),
                        Long::valueOf,
                        newValue -> newValue > 0,
                        newValue -> GlobalConfig.getCurrent().setDebugCrashJavaTime(newValue)
                )
        );
        addEntry(
                new NumberEntry<>(
                        Text.translatable("rebindmykeys.gui.global.verticalScrollSpeedModifier"),
                        GlobalConfig.getCurrent().getVerticalScrollSpeedModifier(),
                        value -> Text.translatable("rebindmykeys.gui.global.times"),
                        Double::valueOf,
                        newValue -> newValue > 0,
                        newValue -> GlobalConfig.getCurrent().setVerticalScrollSpeedModifier(newValue)
                )
        );
        addEntry(
                new NumberEntry<>(
                        Text.translatable("rebindmykeys.gui.global.horizontalScrollSpeedModifier"),
                        GlobalConfig.getCurrent().getHorizontalScrollSpeedModifier(),
                        value -> Text.translatable("rebindmykeys.gui.global.times"),
                        Double::valueOf,
                        newValue -> newValue > 0,
                        newValue -> GlobalConfig.getCurrent().setHorizontalScrollSpeedModifier(newValue)
                )
        );
        addEntry(
                new OnOffButtonEntry(
                        Text.translatable("rebindmykeys.gui.global.showConfirmPopups"),
                        ScreenTexts.YES,
                        ScreenTexts.NO,
                        GlobalConfig.getCurrent().getShowConfirmPopups(),
                        (button, newValue) -> GlobalConfig.getCurrent().setShowConfirmPopups(newValue)
                )
        );
    }
}
