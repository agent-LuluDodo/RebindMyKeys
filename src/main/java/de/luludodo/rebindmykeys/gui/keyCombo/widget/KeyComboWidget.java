package de.luludodo.rebindmykeys.gui.keyCombo.widget;

import de.luludodo.rebindmykeys.gui.keyCombo.screen.KeyComboPopup;
import de.luludodo.rebindmykeys.gui.widget.ConfigWidget;
import de.luludodo.rebindmykeys.gui.widget.IconButtonWidget;
import de.luludodo.rebindmykeys.gui.widget.ResizableCyclingButtonWidget;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.basic.BasicKey;
import de.luludodo.rebindmykeys.keybindings.registry.LuluRegistries;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import de.luludodo.rebindmykeys.util.IterationUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class KeyComboWidget extends ConfigWidget<KeyComboWidget, KeyComboPopup> {
    private final KeyCombo combo;
    public KeyComboWidget(MinecraftClient client, KeyComboPopup parent, KeyCombo combo) {
        super(client, parent);
        this.combo = combo;
    }

    @Override
    public void loadEntries() {
        for (Key key : combo.getKeys()) {
            addEntry(new KeyEntry(LuluRegistries.KEY.copy(key)));
        }
        addEntry(new AddEntry());

        KeyEntry entry = (KeyEntry) getEntry(getEntryCount() - 2);
        entry.setAsFinal.onPress();
        entry.hasChanges = false;
    }

    public void save() {
        List<Key> keys = new ArrayList<>(getEntryCount());
        for (KeyEntry entry : IterationUtil.iterable(children(), KeyEntry.class)) {
            if (entry == finalEntry)
                continue;

            keys.add(entry.getKey());
        }
        if (finalEntry != null)
            keys.add(finalEntry.getKey());

        combo.setKeys(keys);

        setNoChanges();
    }

    public void setNoChanges() {
        hasChanges = false;
        CollectionUtil.forEach(children(), KeyEntry.class, KeyEntry::setNoChanges);
    }

    private boolean hasChanges = false;
    public boolean hasChanges() {
        return hasChanges || CollectionUtil.any(children(), KeyEntry.class, KeyEntry::hasChanges);
    }

    private KeyEntry finalEntry;
    public void setFinal(KeyEntry entry) {
        finalEntry = entry;
        CollectionUtil.forEach(children(), KeyEntry.class, child -> {
            if (child != finalEntry)
                child.setNotFinal();
        });
    }

    public class KeyEntry extends Entry {
        public interface Button extends Selectable, Element {
            void render(DrawContext context, int mouseX, int mouseY, int x, int y, int width, int height, float delta);
            boolean hasChanges();
            void setNoChanges();
        }
        private boolean hasChanges = false;
        private Key key;
        private final IconButtonWidget delete = IconButtonWidget.builder(
                Identifier.of("rebindmykeys", "textures/gui/remove.png"),
                button -> {
                    removeEntry(this);
                    getParent().resize();
                    KeyComboWidget.this.hasChanges = true;
                }
        ).size(20, 20).build();
        private final IconButtonWidget setAsFinal = IconButtonWidget.builder(
                Identifier.of("rebindmykeys", "textures/gui/action.png"),
                button -> {
                    if (finalEntry == this)
                        return;

                    setFinal(this);
                    ((IconButtonWidget) button).setIcon(Identifier.of("rebindmykeys", "textures/gui/action_filled.png"));

                    delete.active = false;

                    hasChanges = true;
                }
        ).size(20, 20).build();
        private final ResizableCyclingButtonWidget<Identifier> type = ResizableCyclingButtonWidget.<Identifier>luluBuilder(
                id -> Text.translatable(LuluRegistries.KEY.constructOptional(id).orElseThrow().getTranslation())
        ).values(LuluRegistries.KEY.options()).omitKeyText().build(
                null,
                w -> 0,
                h -> 0,
                w -> 0,
                h -> 0,
                Text.empty(),
                (button, value) -> {
                    key = LuluRegistries.KEY.constructOptional(value).orElseThrow();
                    buttons = key.getButtons();
                    hasChanges = true;
                }
        );
        private List<Button> buttons;
        public KeyEntry(Key key) {
            super(Text.empty());
            boolean oldHasChanges = hasChanges;
            type.setValue(key.getId());
            hasChanges = oldHasChanges;
            this.key = key;
            this.buttons = key.getButtons();
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return CollectionUtil.mergeAndUnwrapAs(
                    Selectable.class,
                    setAsFinal,
                    type,
                    buttons,
                    delete
            );
        }

        @Override
        public List<? extends Element> children() {
            return CollectionUtil.mergeAndUnwrapAs(
                    Element.class,
                    setAsFinal,
                    type,
                    buttons,
                    delete
            );
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            setAsFinal.setPosition(x, y);
            setAsFinal.render(context, mouseX, mouseY, delta);
            int buttonWidth = (width - 40) / (buttons.size() + 1);
            type.setDimensionsAndPosition(buttonWidth, 20, x + 20, y);
            type.render(context, mouseX, mouseY, delta);
            int buttonX = x + 20 + buttonWidth;
            for (Button button : buttons) {
                button.render(context, mouseX, mouseY, buttonX, y, buttonWidth, 20, delta);
                buttonX += buttonWidth;
            }
            delete.setPosition(x + width - 20, y);
            delete.render(context, mouseX, mouseY, delta);
        }

        public Key getKey() {
            return key;
        }

        public void setNotFinal() {
            setAsFinal.setIcon(Identifier.of("rebindmykeys", "textures/gui/action.png"));
            delete.active = true;
        }

        public boolean hasChanges() {
            return hasChanges || CollectionUtil.any(buttons, Button::hasChanges);
        }

        public void setNoChanges() {
            hasChanges = false;
            buttons.forEach(Button::setNoChanges);
        }

        @Override protected Selectable getSelectable() { return null; }
        @Override protected Element getChild() { return null; }
        @Override protected void renderWidget(int x, int y, int width, int height, DrawContext context, int mouseX, int mouseY, float delta) {}
    }

    public class AddEntry extends Entry {
        private final IconButtonWidget add = IconButtonWidget.builder(
                Identifier.of("rebindmykeys", "textures/gui/add.png"),
                button -> {
                    addEntry(getEntryCount() - 1, new KeyEntry(new BasicKey()));
                    KeyComboWidget.this.hasChanges = true;
                    getParent().resize();
                }
        ).size(20, 20).setRenderBackground(false).build();
        public AddEntry() {
            super(Text.empty());
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            add.setPosition(x + width / 2 - 10, y);
            add.render(context, mouseX, mouseY, delta);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(add);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(add);
        }

        @Override protected Selectable getSelectable() { return null; }
        @Override protected Element getChild() { return null; }
        @Override protected void renderWidget(int x, int y, int width, int height, DrawContext context, int mouseX, int mouseY, float delta) {}
    }
}
