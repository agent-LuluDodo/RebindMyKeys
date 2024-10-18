package de.luludodo.rebindmykeys.gui.binding.widget;

import de.luludodo.rebindmykeys.gui.binding.screen.SettingsPopup;
import de.luludodo.rebindmykeys.gui.keyCombo.screen.KeyComboPopup;
import de.luludodo.rebindmykeys.gui.widget.ConfigWidget;
import de.luludodo.rebindmykeys.gui.widget.ResizableCyclingButtonWidget;
import de.luludodo.rebindmykeys.keybindings.keyCombo.ComboSettingsEditor;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.Context;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.FilterMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.IContext;
import de.luludodo.rebindmykeys.util.ArrayUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Function;

public class SettingsWidget extends ConfigWidget<SettingsWidget, SettingsPopup> {
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
        addEntry(new KeyCombinationEntry(client, getParent()));
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

    public static class SkipFilterEntry extends CyclingButtonEntry<FilterMode> {
        public SkipFilterEntry(ComboSettingsEditor editor) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.filter"),
                    FilterMode::getTranslation,
                    editor.getFilter(),
                    (button, value) -> editor.setFilter(value),
                    FilterMode.values()
            );
        }
    }

    public static class ContextEntry extends ButtonEntry {
        public ContextEntry(IContext[] context) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.context"),
                    ResizableCyclingButtonWidget.luluBuilder(
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
                                    null,
                                    w -> 0,
                                    h -> 0,
                                    w -> 0,
                                    h -> 0,
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
                            button -> client.setScreen(new KeyComboPopup(parent, parent.getCombo()))
                    ).build()
            );
        }
    }
}
