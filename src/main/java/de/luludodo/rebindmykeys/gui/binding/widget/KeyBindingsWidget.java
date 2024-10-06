package de.luludodo.rebindmykeys.gui.binding.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.config.GlobalConfig;
import de.luludodo.rebindmykeys.config.KeyBindingConfig;
import de.luludodo.rebindmykeys.gui.binding.screen.KeyBindingScreen;
import de.luludodo.rebindmykeys.gui.binding.screen.SettingsPopup;
import de.luludodo.rebindmykeys.gui.screen.ConfirmPopup;
import de.luludodo.rebindmykeys.gui.widget.IconButtonWidget;
import de.luludodo.rebindmykeys.gui.widget.VariableElementListWidget;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.keybindings.info.CategoryInfo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import de.luludodo.rebindmykeys.util.KeyUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class KeyBindingsWidget extends VariableElementListWidget<KeyBindingsWidget.Entry> {
    private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    public KeyBindingsWidget(@NotNull KeyBindingScreen parent, MinecraftClient client) {
        super(client, parent, parent.width, parent.height - 52, 0, 20);

        loadEntries();

        calcWidth();
        setRowMargin(0);
    }

    private void loadEntries() {
        CategoryInfo.getCategories().forEach((category, bindings) -> {
            if ("rebindmykeys.key.categories.essentials".equals(category) && GlobalConfig.getCurrent().getHideEssentialKeys()) {
                return;
            }
            addEntry(new CategoryEntry(category));
            bindings.forEach(binding -> addEntry(new BindingEntry(KeyBinding.get(binding))));
        });
    }

    public void reloadEntries() {
        clearEntries();
        loadEntries();
    }

    public void resetAll() {

    }

    public boolean areAllDefault() {
        return false;
    }

    private void calcWidth() {
        int minWidth = -1;
        for (int index = 0; index < getEntryCount(); index++) {
            Entry entry = getEntry(index);
            int entryMinWidth = entry.getMinWidth();
            if (minWidth < entryMinWidth) {
                minWidth = entryMinWidth;
            }
        }
        setRowWidth(minWidth);
        fitRowWidth();
    }

    @Override
    public void resize(int totalWidth, int totalHeight) {
        super.resize(totalWidth, totalHeight);
        calcWidth();
    }

    @Override
    protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderList(context, mouseX, mouseY, delta);
        if (getEntryCount() % 2 == 0) {
            int top = getRowBottom(getEntryCount() - 1);
            context.fill(0, top, width, top + getBottomMargin() * 2, 0x22FFFFFF);
        }
    }

    public abstract static class Entry extends VariableElementListWidget.Entry<Entry> {
        public abstract int getMinWidth();
    }

    public class BindingEntry extends Entry {
        private final KeyBinding binding;
        private List<Entry> comboEntries;
        private final IconButtonWidget addButton = IconButtonWidget.builder(
                Identifier.of("rebindmykeys", "textures/gui/add.png"),
                this::onAddButtonPressed
        ).size(20, 20).build();
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
                context.fill(0, y - getRowMargin() / 2, KeyBindingsWidget.this.width, y + height + getRowMargin() / 2, 0x22FFFFFF);
            }

            // Id
            drawScrollableText(context, textRenderer, Text.translatable(binding.getId()), x + 20, y + 5, x + width - 185, y + 15, 0xFFFFFFFF);

            // Combos
            int size = comboEntries.size();
            for (int i = 0; i < size; i++) {
                int comboY = y + i * 20;
                boolean comboHovered = false;
                if (hovered) {
                    comboHovered = mouseY >= comboY && mouseY < (comboY + 20);
                }
                comboEntries.get(i).render(context, i, comboY, x, width, 20, mouseX,  mouseY, comboHovered, delta);
            }

            // Add Button
            addButton.setPosition(x + width - 185, y);
            addButton.render(context, mouseX, mouseY, delta);

            //context.fill(x, y, x + width, y + height, 0x770000FF); // Debug for bounds
        }

        private void onAddButtonPressed(ButtonWidget addButton) {
            ComboEntry entry = new ComboEntry(this, new KeyCombo(binding.getId(), List.of(), binding.getDefaultSettings()));
            binding.addKeyCombo(entry.combo);
            entry.startRecording();
            comboEntries.add(entry);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return CollectionUtil.add(
                    CollectionUtil.joinCollectionWildcard(
                            ArrayList::new,
                            CollectionUtil.run(comboEntries, Entry::selectableChildren)
                    ),
                    addButton
            );
        }

        @Override
        public List<? extends Element> children() {
            return CollectionUtil.add(
                    CollectionUtil.joinCollectionWildcard(
                            ArrayList::new,
                            CollectionUtil.run(comboEntries, Entry::children)
                    ),
                    addButton
            );
        }

        @Override
        public int getHeight() {
            return Math.max(20, comboEntries.size() * 20);
        }

        @Override
        public int getMinWidth() {
            return 22 + textRenderer.getWidth(Text.translatable(binding.getId())) + 187;
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
                    getKeyButtonMessage(),
                    this::onKeyButtonPressed
            ).size(100, 20).build();
        }

        private Text getKeyButtonMessage() {
            int pressCount = combo.getSettings().operationMode().getPressCount();
            return combo.isUnbound() ? Text.translatable("key.keyboard.unknown") : CollectionUtil.toText(combo.getKeys(), Key::getText,  pressCount != 1 ? (pressCount + "x ") : "", " + ", "");
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

        public void startRecording() {
            onKeyButtonPressed(keyButton);
        }

        private void onKeyButtonPressed(ButtonWidget keyButton) {
            keyButton.setMessage(Text.translatable("rebindmykeys.gui.keyBindings.recording"));
            KeyUtil.startRecording(keys -> {
                combo.setKeys(keys);
                keyButton.setMessage(getKeyButtonMessage());
                KeyBindingConfig.getCurrent().save();
            });
            RebindMyKeys.DEBUG.info("Key button pressed");
        }

        private void onRemoveButtonPressed(ButtonWidget removeButton) {
            parent.binding.removeKeyCombo(combo);
            parent.comboEntries.remove(this);
            KeyBindingConfig.getCurrent().save();
            RebindMyKeys.DEBUG.info("Remove button pressed");
        }

        private void onSettingsButtonPressed(ButtonWidget settingsButton) {
            client.setScreen(new SettingsPopup((KeyBindingScreen) getParent(), combo, parent.binding.getDefaultSettings()));
            RebindMyKeys.DEBUG.info("Settings button pressed");
        }

        private void onResetButtonPressed(ButtonWidget resetButton) {
            client.setScreen(
                    new ConfirmPopup(
                            getParent(),
                            Text.translatable("rebindmykeys.gui.keyBindings.confirmReset.title"),
                            Text.translatable("rebindmykeys.gui.keyBindings.confirmReset.message"),
                            this::doReset,
                            () -> {
                            }
                    )
            );
            RebindMyKeys.DEBUG.info("Reset button pressed");
        }

        private void doReset() {
            combo.setSettings(parent.binding.getDefaultSettings());
            parent.update();
            KeyBindingConfig.getCurrent().save();
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

        @Override
        public int getMinWidth() {
            return -1;
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
                context.fill(0, y - getRowMargin() / 2, KeyBindingsWidget.this.width, y + height + getRowMargin() / 2, 0x22FFFFFF);
            }

            // Name
            drawScrollableText(context, textRenderer, Text.translatable(translationKey).formatted(Formatting.UNDERLINE), x, y, x + width, y + height, 0xFFFFFFFF);
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

        @Override
        public int getMinWidth() {
            return textRenderer.getWidth(Text.translatable(translationKey));
        }
    }
}
