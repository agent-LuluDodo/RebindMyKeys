package de.luludodo.rebindmykeys.gui.binding.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.luludodo.rebindmykeys.gui.binding.screen.KeyCombinationPopup;
import de.luludodo.rebindmykeys.gui.binding.screen.SettingsPopup;
import de.luludodo.rebindmykeys.gui.widgets.VariableElementListWidget;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActionMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActivateOn;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold.HoldMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.toggle.ToggleMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.ComboSettings;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.Context;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.IContext;
import de.luludodo.rebindmykeys.util.ArrayUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class SettingsWidget extends VariableElementListWidget<SettingsWidget.Entry> {
    private final KeyCombo combo;
    public SettingsWidget(MinecraftClient client, SettingsPopup parent, KeyCombo combo) {
        super(client, parent, parent.getWidth() - 30, parent.getHeight() - 60, 15, 25);
        setRowMargin(0);
        resizeRowWidth();
        setScrollBarMargin(0);
        setRenderBackground(false);
        setRenderHeader(false, 0);
        setTopMargin(0);
        setBottomMargin(0);
        this.combo = combo;
    }

    public void init() {
        reload();
    }

    @Override
    public void resize(int totalWidth, int totalHeight) {
        super.resize(totalWidth, totalHeight);
        resizeRowWidth();
    }

    private void resizeRowWidth() {
        setRowWidth(getParent().getWidth() - 20);
        fitRowWidth();
    }

    public void reload() {
        getParent().resetHeight();

        addEntry(new OrderSensitiveEntry(combo));
        addEntry(new PressCountEntry(combo.getSettings().operationMode()));

        if (combo.getSettings().operationMode() instanceof ActionMode mode) {
            addEntry(new ActivateOnEntry(mode));
        } else if (combo.getSettings().operationMode() instanceof HoldMode mode) {
            addEntry(new InvertedEntry(mode));
        } else if (combo.getSettings().operationMode() instanceof ToggleMode mode) {
            addEntry(new InitialStateEntry(mode));
            addEntry(new ToggleOnEntry(mode));
        } // TODO: better system to allow custom OperationModes

        addEntry(new ContextEntry(combo.getSettings().context()));
        addEntry(new OperationModeEntry(combo.getSettings().operationMode()));
        addEntry(new SkipFilterEntry(combo));
        addEntry(new KeyCombinationEntry(client, getParent()));

        getParent().resize();
    }

    @Override
    public SettingsPopup getParent() {
        return (SettingsPopup) super.getParent();
    }

    @Override
    public int addEntry(Entry entry) {
        getParent().additionalHeight(entry.getHeight());
        entry.setTextRenderer(client.textRenderer);
        return super.addEntry(entry);
    }

    public static abstract class Entry extends VariableElementListWidget.Entry<Entry> {
        private final Text title;
        public Entry(Text title) {
            this.title = title;
        }

        public Text getTitle() {
            return title;
        }
        protected abstract Selectable getSelectable();
        protected abstract Element getChild();

        private TextRenderer textRenderer;
        public void setTextRenderer(TextRenderer textRenderer) {
            this.textRenderer = textRenderer;
        }
        public TextRenderer getTextRenderer() {
            return textRenderer;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(getSelectable());
        }

        @Override
        public List<? extends Element> children() {
            return List.of(getChild());
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            RenderSystem.enableBlend();
            context.drawText(getTextRenderer(), getTitle(), x, y + 5, 0xFFFFFFFF, true);
            renderWidget(x + width - 90, y, 90, 20, context, mouseX, mouseY, delta);
        }

        protected abstract void renderWidget(int x, int y, int width, int height, DrawContext context, int mouseX, int mouseY, float delta);

        @Override
        public int getHeight() {
            return 20;
        }
    }
    public static abstract class ButtonEntry extends Entry {
        private final PressableWidget button;
        public ButtonEntry(Text title, PressableWidget button) {
            super(title);
            this.button = button;
        }

        public PressableWidget getButton() {
            return button;
        }
        @Override
        protected Selectable getSelectable() {
            return getButton();
        }
        @Override
        protected Element getChild() {
            return getButton();
        }

        @Override
        public void renderWidget(int x, int y, int width, int height, DrawContext context, int mouseX, int mouseY, float delta) {
            getButton().setX(x + width - 90);
            getButton().setY(y);
            getButton().setWidth(90);
            getButton().setHeight(20);
            getButton().render(context, mouseX, mouseY, delta);
        }
    }
    public static abstract class TextEntry extends Entry {
        private TextFieldWidget textField = null;
        private final Predicate<String> predicate;
        private final Consumer<String> onChange;
        private final String initially;
        public TextEntry(Text title, String initially, Predicate<String> predicate, Consumer<String> onChange) {
            super(title);
            this.initially = initially;
            this.predicate = predicate;
            this.onChange = onChange;
        }

        @Override
        public void setTextRenderer(TextRenderer textRenderer) {
            super.setTextRenderer(textRenderer);

            textField = new TextFieldWidget(getTextRenderer(), 0, 0, Text.empty());
            textField.setChangedListener(newValue -> {
                if (predicate.test(newValue)) {
                    textField.setEditableColor(TextFieldWidget.DEFAULT_EDITABLE_COLOR);
                    onChange.accept(newValue);
                } else {
                    //noinspection ConstantConditions
                    textField.setEditableColor(Formatting.RED.getColorValue()); // never null, since red is a color
                }
            });
            textField.setText(initially);
            textField.setCursor(0, false); // doesn't render the text until you click on it otherwise
        }

        public TextFieldWidget getTextField() {
            return textField;
        }
        @Override
        protected Selectable getSelectable() {
            return getTextField();
        }
        @Override
        protected Element getChild() {
            return getTextField();
        }

        @Override
        public void renderWidget(int x, int y, int width, int height, DrawContext context, int mouseX, int mouseY, float delta) {
            getTextField().setX(x + width - 90);
            getTextField().setY(y);
            getTextField().setWidth(90);
            getTextField().setHeight(20);
            getTextField().render(context, mouseX, mouseY, delta);
        }
    }

    public static class ActivateOnEntry extends ButtonEntry {
        public ActivateOnEntry(ActionMode mode) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.activateOn"),
                    CyclingButtonWidget.builder(
                                    (Function<ActivateOn, Text>) value -> switch (value) {
                                        case PRESS -> Text.translatable("rebindmykeys.activateOn.press");
                                        case RELEASE -> Text.translatable("rebindmykeys.activateOn.release");
                                        case BOTH -> Text.translatable("rebindmykeys.activateOn.both");
                                        case UNKNOWN -> Text.translatable("rebindmykeys.activateOn.unknown");
                                    }
                            )
                            .values(
                                    ActivateOn.values()
                            )
                            .initially(
                                    mode.getActivateOn()
                            )
                            .omitKeyText()
                            .build(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Text.empty(),
                                    (button, value) -> mode.setActivateOn(value)
                            )
            );
        }
    }

    public static class InvertedEntry extends ButtonEntry {
        public InvertedEntry(HoldMode mode) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.inverted"),
                    CyclingButtonWidget.onOffBuilder(
                                    Text.translatable("rebindmykeys.gui.settings.inverted.on"),
                                    Text.translatable("rebindmykeys.gui.settings.inverted.on")
                            )
                            .initially(
                                    mode.getInverted()
                            )
                            .omitKeyText()
                            .build(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Text.empty(),
                                    (button, value) -> mode.setInverted(value)
                            )
            );
        }
    }

    public static class InitialStateEntry extends ButtonEntry {
        public InitialStateEntry(ToggleMode mode) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.initialState"),
                    CyclingButtonWidget.onOffBuilder(
                                    Text.translatable("rebindmykeys.gui.settings.initialState.on"),
                                    Text.translatable("rebindmykeys.gui.settings.initialState.on")
                            )
                            .initially(
                                    mode.getInitialState()
                            )
                            .omitKeyText()
                            .build(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Text.empty(),
                                    (button, value) -> mode.setInitialState(value)
                            )
            );
        }
    }

    public static class ToggleOnEntry extends ButtonEntry {
        public ToggleOnEntry(ToggleMode mode) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.toggleOnPress"),
                    CyclingButtonWidget.onOffBuilder(
                                    Text.translatable("rebindmykeys.gui.settings.toggleOnPress.on"),
                                    Text.translatable("rebindmykeys.gui.settings.toggleOnPress.off")
                            )
                            .initially(
                                    mode.getToggleOn()
                            )
                            .omitKeyText()
                            .build(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Text.empty(),
                                    (button, value) -> mode.setToggleOn(value)
                            )
            );
        }
    }

    public static class OrderSensitiveEntry extends ButtonEntry {
        public OrderSensitiveEntry(KeyCombo combo) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.orderSensitive"),
                    CyclingButtonWidget.onOffBuilder(
                                    Text.translatable("rebindmykeys.gui.settings.orderSensitive.on"),
                                    Text.translatable("rebindmykeys.gui.settings.orderSensitive.off")
                            )
                            .initially(
                                    combo.getSettings().orderSensitive()
                            )
                            .omitKeyText()
                            .build(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Text.empty(),
                                    (button, value) -> {
                                        ComboSettings oldSettings = combo.getSettings();
                                        combo.setSettings(new ComboSettings(
                                                oldSettings.operationMode(),
                                                oldSettings.context(),
                                                value,
                                                oldSettings.skipFilter()
                                        ));
                                    }
                            )
            );
        }
    }

    public static class SkipFilterEntry extends ButtonEntry {
        public SkipFilterEntry(KeyCombo combo) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.skipFilter"),
                    CyclingButtonWidget.onOffBuilder(
                                    Text.translatable("rebindmykeys.gui.settings.skipFilter.on"),
                                    Text.translatable("rebindmykeys.gui.settings.skipFilter.off")
                            )
                            .initially(
                                    combo.getSettings().skipFilter()
                            )
                            .omitKeyText()
                            .build(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Text.empty(),
                                    (button, value) -> {
                                        ComboSettings oldSettings = combo.getSettings();
                                        combo.setSettings(new ComboSettings(
                                                oldSettings.operationMode(),
                                                oldSettings.context(),
                                                oldSettings.orderSensitive(),
                                                value
                                        ));
                                    }
                            )
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

    public static class OperationModeEntry extends ButtonEntry {
        public OperationModeEntry(OperationMode mode) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.operationMode"),
                    CyclingButtonWidget.builder(
                                    (Function<OperationMode, Text>) value -> Text.translatable(value.getTranslation())
                            )
                            .values(
                                    mode instanceof ActionMode? List.of(mode) :
                                            mode instanceof ToggleMode? List.of(mode, new HoldMode()) :
                                                    List.of(new ToggleMode(), mode) // TODO: better way to do this, also allow custom OperationModes
                            )
                            .initially(
                                    mode
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

    public static class PressCountEntry extends TextEntry {
        public PressCountEntry(OperationMode mode) {
            super(
                    Text.translatable("rebindmykeys.gui.settings.pressCount"),
                    String.valueOf(mode.getPressCount()),
                    newValue -> {
                        try {
                            return Integer.parseInt(newValue) > 0;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    },
                    newValue -> mode.setPressCount(Integer.parseInt(newValue))
            );
        }
    }
}
