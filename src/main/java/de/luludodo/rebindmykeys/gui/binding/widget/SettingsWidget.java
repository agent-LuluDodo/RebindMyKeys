package de.luludodo.rebindmykeys.gui.binding.widget;

import de.luludodo.rebindmykeys.gui.binding.screen.KeyCombinationPopup;
import de.luludodo.rebindmykeys.gui.binding.screen.SettingsPopup;
import de.luludodo.rebindmykeys.gui.widget.ConfigWidget;
import de.luludodo.rebindmykeys.keybindings.keyCombo.ComboSettingsEditor;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.Context;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.IContext;
import de.luludodo.rebindmykeys.util.ArrayUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Function;

public class SettingsWidget extends ConfigWidget {
    private final ComboSettingsEditor editor;
    public SettingsWidget(MinecraftClient client, SettingsPopup parent, ComboSettingsEditor editor) {
        super(client, parent);
        this.editor = editor;
    }

    @Override
    public void loadEntries() {
        addEntry(new OrderSensitiveEntry(editor));
        addEntry(new OperationModeEntry(this, editor));

        for (Entry entry : editor.getOperationModeEntries()) {
            addEntry(entry);
        }

        addEntry(new ContextEntry(editor.getContext()));
        addEntry(new SkipFilterEntry(editor));
        addEntry(new KeyCombinationEntry(client, (SettingsPopup) getParent()));
    }

    public static class OrderSensitiveEntry extends OnOffButtonEntry {
        public OrderSensitiveEntry(ComboSettingsEditor editor) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.orderSensitive"),
                    Text.translatable("rebindmykeys.gui.settings.orderSensitive.on"),
                    Text.translatable("rebindmykeys.gui.settings.orderSensitive.off"),
                    editor.getOrderSensitive(),
                    (button, value) -> editor.setOrderSensitive(value)
            );
        }
    }

    public static class SkipFilterEntry extends OnOffButtonEntry {
        public SkipFilterEntry(ComboSettingsEditor editor) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.skipFilter"),
                    Text.translatable("rebindmykeys.gui.settings.skipFilter.on"),
                    Text.translatable("rebindmykeys.gui.settings.skipFilter.off"),
                    editor.getSkipFilter(),
                    (button, value) -> editor.setSkipFilter(value)
            );
        }
    }

    public static class ContextEntry extends ButtonEntry {
        public ContextEntry(IContext[] context) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.context"),
                    CyclingButtonWidget.builder(
                                    (Function<IContext[], Text>) array -> ArrayUtil.toText(
                                            array, value -> Text.translatable(value.getId()), "", " + ", ""
                                    )
                            )
                            .values(
                                    Context.values() // TODO: Allow custom IContext values
                            )
                            .initially(
                                    context
                            )
                            .omitKeyText()
                            .build(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Text.empty()
                            )
            );
            getButton().active = false; // Read-Only
        }
    }

    public static class OperationModeEntry extends CyclingButtonEntry<Integer> {
        public OperationModeEntry(SettingsWidget parent, ComboSettingsEditor editor) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.operationMode"),
                    editor::getOperationModeTranslation,
                    editor.getCurrentOperationMode(),
                    (button, value) -> {
                        editor.setOperationMode(value);
                        parent.reload();
                    },
                    editor.getOperationModeOptions()
            );
            getButton().active = editor.getOperationModeActive();
        }
    }

    public static class KeyCombinationEntry extends ButtonEntry {
        public KeyCombinationEntry(MinecraftClient client, SettingsPopup parent) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.keyCombination"),
                    ButtonWidget.builder(
                            Text.translatable("rebindmykeys.gui.settings.edit"),
                            button -> client.setScreen(new KeyCombinationPopup(parent))
                    ).build()
            );
        }
    }
}
