package de.luludodo.rebindmykeys.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.luludodo.rebindmykeys.gui.screen.ConfigPopup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

public abstract class ConfigWidget extends VariableElementListWidget<ConfigWidget.Entry> {
    public ConfigWidget(MinecraftClient client, ConfigPopup parent) {
        super(client, parent, parent.getWidth() - 30, parent.getHeight() - parent.getReservedSpace(), 15, parent.getTopSpace());
        setRowMargin(0);
        resizeRowWidth();
        setScrollBarMargin(0);
        setRenderBackground(false);
        setRenderHeader(false, 0);
        setTopMargin(0);
        setBottomMargin(0);
    }

    public void init() {
        load();
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

    private void load() {
        loadEntries();
        getParent().resize();
    }

    public void reload() {
        clearEntries();
        load();
    }

    public abstract void loadEntries();

    @Override
    public ConfigPopup getParent() {
        return (ConfigPopup) super.getParent();
    }

    @Override
    public int addEntry(Entry entry) {
        entry.setTextRenderer(client.textRenderer);
        return super.addEntry(entry);
    }

    public void addEntry(int index, Entry entry) {
        entry.setTextRenderer(client.textRenderer);
        children().add(index, entry);
    }

    @Override
    public void addEntryToTop(Entry entry) {
        entry.setTextRenderer(client.textRenderer);
        super.addEntryToTop(entry);
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

    public static class ButtonEntry extends Entry {
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

    public static class CyclingButtonEntry<T> extends ButtonEntry {
        @SafeVarargs
        public CyclingButtonEntry(Text title, Function<T, Text> valueToText, T initially, CyclingButtonWidget.UpdateCallback<T> callback, T... values) {
            super(
                    title,
                    CyclingButtonWidget.builder(valueToText).values(values).initially(initially).omitKeyText()
                            .build(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Text.empty(),
                                    callback
                            )
            );
        }
        public CyclingButtonEntry(Text title, Function<T, Text> valueToText, T initially, CyclingButtonWidget.UpdateCallback<T> callback, Collection<T> values) {
            super(
                    title,
                    CyclingButtonWidget.builder(valueToText).values(values).initially(initially).omitKeyText()
                            .build(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Text.empty(),
                                    callback
                            )
            );
        }
    }

    public static class OnOffButtonEntry extends ButtonEntry {
        public OnOffButtonEntry(Text title, Text on, Text off, boolean initially, CyclingButtonWidget.UpdateCallback<Boolean> callback) {
            super(
                    title,
                    CyclingButtonWidget.onOffBuilder(on, off).initially(initially).omitKeyText()
                            .build(
                                    0,
                                    0,
                                    0,
                                    0,
                                    Text.empty(),
                                    callback
                            )
            );
        }
    }

    public static class TextEntry extends Entry {
        private TextFieldWidget textField = null;
        private final @Nullable Function<String, Text> suffix;
        private Text suffixValue;
        private final Predicate<String> predicate;
        private final Consumer<String> onChange;
        private final String initially;
        public TextEntry(Text title, String initially, @Nullable Function<String, Text> suffix, Predicate<String> predicate, Consumer<String> onChange) {
            super(title);

            // Validate input
            if (!predicate.test(initially))
                throw new IllegalArgumentException("initially does not match predicate");

            this.initially = initially;
            this.suffix = suffix;
            if (suffix != null)
                this.suffixValue = suffix.apply(initially);
            this.predicate = predicate;
            this.onChange = onChange;
        }
        public TextEntry(Text title, String initially, Predicate<String> predicate, Consumer<String> onChange) {
            this(title, initially, null, predicate, onChange);
        }

        @Override
        public void setTextRenderer(TextRenderer textRenderer) {
            super.setTextRenderer(textRenderer);

            textField = new TextFieldWidget(getTextRenderer(), 0, 0, Text.empty());
            textField.setChangedListener(newValue -> {
                if (predicate.test(newValue)) {
                    textField.setEditableColor(TextFieldWidget.DEFAULT_EDITABLE_COLOR);
                    onChange.accept(newValue);
                    if (suffix != null)
                        suffixValue = suffix.apply(newValue);
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
            if (suffix != null) {
                //noinspection ConstantConditions
                context.drawTextWithShadow(
                        getTextRenderer(),
                        suffixValue,
                        getTextField().isFocused()?
                                x + width - getTextRenderer().getWidth(suffixValue) - 5 :
                                x + width - 90 + getTextRenderer().getWidth(getTextField().getText()) + 5,
                        y + 10 - getTextRenderer().fontHeight / 2,
                        Formatting.GRAY.getColorValue()
                );
            }
        }
    }

    public static class NumberEntry<N extends Number> extends TextEntry {
        @FunctionalInterface
        public interface Parser<N extends Number> {
            @NotNull N valueOf(@NotNull String s) throws NumberFormatException;
        }

        public NumberEntry(Text title, N initially, @Nullable Function<N, Text> suffix, Parser<N> parser, Predicate<@NotNull N> predicate, Consumer<@NotNull N> onChange) {
            super(
                    title,
                    String.valueOf(initially),
                    suffix == null ? null : value -> suffix.apply(parser.valueOf(value)),
                    newValue -> {
                        try {
                            return predicate.test(parser.valueOf(newValue));
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    },
                    newValue -> onChange.accept(parser.valueOf(newValue))
            );
        }
        public NumberEntry(Text title, N initially, @Nullable Function<N, Text> suffix, Parser<N> parser, Consumer<@NotNull N> onChange) {
            this(title, initially, suffix, parser, value -> true, onChange);
        }
        public NumberEntry(Text title, N initially, Parser<N> parser, Predicate<@NotNull N> predicate, Consumer<@NotNull N> onChange) {
            this(title, initially, null, parser, predicate, onChange);
        }
        public NumberEntry(Text title, N initially, Parser<N> parser, Consumer<@NotNull N> onChange) {
            this(title, initially, null, parser, value -> true, onChange);
        }
    }
}
