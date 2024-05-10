package de.luludodo.rebindmykeys.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.gui.KeyBindingScreen;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import de.luludodo.rebindmykeys.util.KeyUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class KeyBindingsWidget extends VariableElementListWidget<KeyBindingsWidget.Entry> {
    private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    private final @NotNull KeyBindingScreen parent;
    public KeyBindingsWidget(@NotNull KeyBindingScreen parent, MinecraftClient client) {
        super(client, parent.width, parent.height - 52, 20);
        this.parent = parent;

        AtomicInteger minWidth = new AtomicInteger(-1);
        KeyUtil.getCategories().forEach((category, bindings) -> {
            addEntry(new CategoryEntry(category));
            bindings.sort((b1, b2) -> Text.translatable(b1.getId()).getString().compareToIgnoreCase(Text.translatable(b2.getId()).getString()));
            bindings.forEach(binding -> {
                BindingEntry entry = new BindingEntry(binding);
                addEntry(entry);
                int entryMinWidth = entry.getMinWidth();
                if (minWidth.get() < entryMinWidth) {
                    minWidth.set(entryMinWidth);
                }
            });
        });
        setRowWidth(minWidth.get());
        fitRowWidth();
        setRowMargin(0);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    public abstract static class Entry extends VariableElementListWidget.Entry<Entry> {}

    public class BindingEntry extends Entry {
        private final KeyBinding binding;
        private List<ComboEntry> comboEntries;
        public BindingEntry(KeyBinding binding) {
            this.binding = binding;
            update();
        }

        public void update() {
            comboEntries = new ArrayList<>(binding.getKeyCombos().size());
            for (KeyCombo keyCombo : binding.getKeyCombos()) {
                comboEntries.add(new ComboEntry(this, keyCombo));
            }
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            // Stripes
            if (index % 2 == 1) {
                context.fill(0, y - getRowMargin() / 2, KeyBindingsWidget.this.width, y + height + getRowMargin() / 2, 0x99FFFFFF);
            }

            // Id
            drawScrollableText(context, textRenderer, Text.translatable(binding.getId()), x + 25, y + 5, x + width - 170, y + 15, 0xFFFFFFFF);

            // Combos
            for (int i = 0; i < comboEntries.size(); i++) {
                int comboY = y + i * 20;
                boolean comboHovered = false;
                if (hovered) {
                    comboHovered = mouseY >= comboY && mouseY < (comboY + 20);
                }
                comboEntries.get(i).render(context, i, comboY, x, width, 20, mouseX,  mouseY, comboHovered, delta);
            }

            //context.fill(x, y, x + width, y + height, 0x770000FF); // Debug for bounds
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return CollectionUtil.joinCollectionWildcard(ArrayList::new, CollectionUtil.run(comboEntries, ComboEntry::selectableChildren));
        }

        @Override
        public List<? extends Element> children() {
            return CollectionUtil.joinCollectionWildcard(ArrayList::new, CollectionUtil.run(comboEntries, ComboEntry::children));
        }

        @Override
        public int getHeight() {
            return Math.max(comboEntries.size(), 1) * 20;
        }

        public int getMinWidth() {
            return 25 + textRenderer.getWidth(Text.translatable(binding.getId())) + 5 + 80 + 5 + 20 + 20 + 20;
        }
    }

    public class ComboEntry extends Entry {
        private final ButtonWidget keyButton;
        private final IconButtonWidget removeButton = IconButtonWidget.builder(
                Identifier.of("rebindmykeys", "textures/gui/remove.png"),
                this::onRemoveButtonPressed
        ).size(20, 20).build();
        private final IconButtonWidget settingsButton = IconButtonWidget.builder(
                Identifier.of("rebindmykeys", "textures/gui/settings.png"),
                this::onSettingsButtonPressed
        ).size(20, 20).build();
        private final IconButtonWidget resetButton = IconButtonWidget.builder(
                Identifier.of("rebindmykeys", "textures/gui/reset.png"),
                this::onResetButtonPressed
        ).size(20, 20).build();
        private final BindingEntry parent;
        private final KeyCombo combo;
        public ComboEntry(BindingEntry parent, KeyCombo combo) {
            this.parent = parent;
            this.combo = combo;
            keyButton = ButtonWidget.builder(
                    CollectionUtil.toText(combo.getKeys(), Key::getText, "", " + ", ""),
                    this::onKeyButtonPressed
            ).size(100, 20).build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            RenderSystem.enableBlend();
            //context.fill(x, y, x + width, y + height, hovered? 0x55FF0000 : 0x5500FF00); // Debug for hovering
            context.drawTexture(combo.getSettings().operationMode().getIcon(), x, y, 0, 0, 20, 20, 20, 20);
            keyButton.setPosition(x + width - 165, y);
            keyButton.render(context, mouseX, mouseY, delta);
            removeButton.setPosition(x + width - 60, y);
            removeButton.render(context, mouseX, mouseY, delta);
            settingsButton.setPosition(x + width - 40, y);
            settingsButton.render(context, mouseX, mouseY, delta);
            resetButton.setPosition(x + width - 20, y);
            resetButton.render(context, mouseX, mouseY, delta);
        }

        private void onKeyButtonPressed(ButtonWidget keyButton) {
            RebindMyKeys.DEBUG.info("Key button pressed");
        }

        private void onRemoveButtonPressed(ButtonWidget keyButton) {
            parent.binding.removeKeyCombo(combo);
            parent.update();
        }

        private void onSettingsButtonPressed(ButtonWidget keyButton) {
            RebindMyKeys.DEBUG.info("Settings button pressed");
        }

        private void onResetButtonPressed(ButtonWidget keyButton) {
            RebindMyKeys.DEBUG.info("Reset button pressed");
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(keyButton, removeButton, settingsButton, resetButton);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(keyButton, removeButton, settingsButton, resetButton);
        }

        @Override
        public int getHeight() {
            return 20;
        }
    }

    public class CategoryEntry extends Entry {
        private final String translationKey;
        public CategoryEntry(String translationKey) {
            this.translationKey = translationKey;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            // Stripes
            if (index % 2 == 1) {
                context.fill(0, y - getRowMargin() / 2, KeyBindingsWidget.this.width, y + height + getRowMargin() / 2, 0x99FFFFFF);
            }

            // Name
            drawScrollableText(context, textRenderer, Text.translatable(translationKey), x, y, x + width, y + height, 0xFFFFFFFF);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }

        @Override
        public List<? extends Element> children() {
            return List.of();
        }

        @Override
        public int getHeight() {
            return 20;
        }
    }
}
