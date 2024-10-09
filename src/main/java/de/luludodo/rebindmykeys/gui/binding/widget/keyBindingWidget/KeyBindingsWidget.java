package de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget;

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
import de.luludodo.rebindmykeys.keybindings.info.ModInfo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import de.luludodo.rebindmykeys.util.KeyUtil;
import de.luludodo.rebindmykeys.util.MapUtil;
import de.luludodo.rebindmykeys.util.RenderUtil;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KeyBindingsWidget extends VariableElementListWidget<KeyBindingsWidget.Entry> {
    private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    private boolean isOdd;
    private boolean renderedSomething;

    private SortAfter sortAfter = GlobalConfig.getCurrent().getSortAfter();
    private SortOrder sortOrder = GlobalConfig.getCurrent().getSortOrder();

    private SearchTarget searchTarget = SearchTarget.NAME;
    private String searchQuery = "";

    public KeyBindingsWidget(@NotNull KeyBindingScreen parent, MinecraftClient client) {
        super(client, parent, parent.width, parent.height - 72, 0, 20);

        loadEntries();

        calcWidth();
        setRowMargin(0);
    }

    public void collapseAll() {
        children().forEach(child -> {
            if (child instanceof BindingParentEntry bindingParent) {
                if (!bindingParent.collapsed)
                    bindingParent.toggleCollapsed();
            }
        });
    }

    public void expandAll() {
        children().forEach(child -> {
            if (child instanceof BindingParentEntry bindingParent) {
                if (bindingParent.collapsed)
                    bindingParent.toggleCollapsed();
            }
        });
    }

    public void setSearchTarget(SearchTarget searchTarget) {
        this.searchTarget = searchTarget;
        updateSearchQuery();
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = applyQueryFormatting(searchQuery);
        updateSearchQuery();
    }

    private void updateSearchQuery() {
        children().forEach(child -> child.applySearchQuery(searchQuery, searchTarget));
        calcWidth();
        setScrollAmount(getScrollAmount());
    }

    private void loadEntries() {
        switch (sortAfter) {
            case CATEGORY -> SortHelper.getSortedCategories(sortOrder).forEach((category, bindings) -> {
                if ("rebindmykeys.key.categories.essentials".equals(category) && GlobalConfig.getCurrent().getHideEssentialKeys())
                    return;

                CategoryEntry entry = new CategoryEntry(category);
                bindings.forEach(binding -> entry.addChild(new BindingEntry(KeyBinding.get(binding))));
                addEntry(entry);
            });
            case MOD -> SortHelper.getSortedMods(sortOrder).forEach((mod, bindings) -> {
                ModEntry entry = new ModEntry(mod);
                bindings.forEach(binding -> {
                    if (GlobalConfig.getCurrent().getHideEssentialKeys() && "rebindmykeys.key.categories.essentials".equals(CategoryInfo.getCategory(binding)))
                        return;

                    entry.addChild(new BindingEntry(KeyBinding.get(binding)));
                });
                addEntry(entry);
            });
            case NAME -> SortHelper.getSortedNames(sortOrder).forEach(binding ->
                addEntry(new BindingEntry(KeyBinding.get(binding)))
            );
        }
    }

    public void setSortAfter(SortAfter sortAfter) {
        if (sortAfter == this.sortAfter) return;

        this.sortAfter = sortAfter;
        reloadEntries();
    }

    public void setSortOrder(SortOrder sortOrder) {
        if (sortOrder == this.sortOrder) return;

        this.sortOrder = sortOrder;
        reloadEntries();
    }

    public void reloadEntries() {
        clearEntries();
        loadEntries();
        setScrollAmount(getScrollAmount());
    }

    public void resetAll() {
        // TODO: implement
    }

    public boolean areAllDefault() {
        return false;
    }

    private void calcWidth() {
        setRowWidth(CollectionUtil.max(children(), Entry::getMinWidth));
        fitRowWidth();
    }

    @Override
    public void resize(int totalWidth, int totalHeight) {
        super.resize(totalWidth, totalHeight);
        calcWidth();
        setScrollAmount(getScrollAmount());
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        if (renderTooltip != null) {
            renderTooltip.accept(context);
            renderTooltip = null;
        }
    }

    @Override
    protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        isOdd = false;
        renderedSomething = false;
        super.renderList(context, mouseX, mouseY, delta);
        if (!renderedSomething) {
            context.drawCenteredTextWithShadow(textRenderer, Text.translatable("rebindmykeys.gui.keyBindings.empty"), (getX() + getRight()) / 2, (getY() + getBottom()) / 2 - textRenderer.fontHeight / 2, 0xFFAAAAAA);
        } else if (!isOdd) {
            int top = getRowBottom(getEntryCount() - 1);
            context.fill(0, top, width, top + getBottomMargin() * 2, 0x22FFFFFF);
        }
    }

    private Consumer<DrawContext> renderTooltip = null;
    private void tooltipRenderer(Consumer<DrawContext> renderTooltip) {
        this.renderTooltip = renderTooltip;
    }

    public abstract static class Entry extends VariableElementListWidget.Entry<Entry> {
        public abstract void applySearchQuery(String query, SearchTarget target);
        protected abstract boolean isFiltered();
        public abstract int getMinWidth();
    }

    public abstract static class ParentEntry extends Entry {
        private List<Entry> children = new ArrayList<>();
        protected boolean filtered = false;

        public void addChild(Entry child) {
            children.add(child);
        }

        public void removeChild(Entry child) {
            children.remove(child);
        }

        public void setChildren(List<Entry> children) {
            this.children = children;
        }

        public List<Entry> getChildren() {
            return children;
        }

        public Entry getChild(int index) {
            return children.get(index);
        }

        public int getChildCount() {
            return children.size();
        }

        public void clearChildren() {
            children = new ArrayList<>();
        }

        @Override
        public void applySearchQuery(String query, SearchTarget target) {
            getChildren().forEach(child -> child.applySearchQuery(query, target));
            filtered = CollectionUtil.all(getChildren(), Entry::isFiltered);
        }

        @Override
        public boolean isFiltered() {
            return filtered;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            if (filtered)
                return;

            int size = getChildCount();
            int curY = y;
            for (int i = 0; i < size; i++) {
                boolean comboHovered = false;
                Entry child = getChild(i);
                int childHeight = child.getHeight();
                if (hovered) {
                    comboHovered = mouseY >= curY && mouseY < (curY + childHeight);
                }
                getChild(i).render(context, i, curY, x, width, childHeight, mouseX,  mouseY, comboHovered, delta);
                curY += childHeight;
            }
        }

        @Override
        public List<Selectable> selectableChildren() {
            return filtered ? List.of() : CollectionUtil.joinCollectionWildcard(
                    ArrayList::new,
                    CollectionUtil.run(children, Entry::selectableChildren)
            );
        }

        @Override
        public List<Element> children() {
            return filtered ? List.of() : CollectionUtil.joinCollectionWildcard(
                    ArrayList::new,
                    CollectionUtil.run(children, Entry::children)
            );
        }

        @Override
        public int getHeight() {
            return filtered ? 0 : CollectionUtil.combineInt(getChildren(), Entry::getHeight);
        }

        @Override
        public int getMinWidth() {
            return filtered ? 0 : CollectionUtil.max(getChildren(), Entry::getMinWidth);
        }
    }

    public class BindingEntry extends ParentEntry {
        private final KeyBinding binding;
        private final IconButtonWidget addButton = IconButtonWidget.builder(
                Identifier.of("rebindmykeys", "textures/gui/add.png"),
                this::onAddButtonPressed
        ).size(20, 20).build();
        public BindingEntry(KeyBinding binding) {
            this.binding = binding;
            update();
        }

        @Override
        public void applySearchQuery(String query, SearchTarget target) {
            super.applySearchQuery(query, target);
            if (target == SearchTarget.KEY) {
                filtered = CollectionUtil.all(getChildren(), child -> ((ComboEntry) child).filtered);
            } else {
                String filterString;
                if (target == SearchTarget.NAME) {
                    filterString = Text.translatable(binding.getId()).getString();
                } else if (target == SearchTarget.CATEGORY) {
                    filterString = Text.translatable(CategoryInfo.getCategory(binding.getId())).getString();
                } else if (target == SearchTarget.MOD) {
                    filterString = ModInfo.getModName(ModInfo.getMod(binding.getId()));
                } else {
                    throw new IllegalArgumentException("Unknown SearchTarget: " + target);
                }
                filtered = !applyQueryFormatting(filterString).contains(query);
            }
        }

        public void update() {
            clearChildren();
            for (KeyCombo keyCombo : binding.getKeyCombos()) {
                addChild(new ComboEntry(this, keyCombo));
            }
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            if (filtered)
                return;

            KeyBindingsWidget.this.renderedSomething = true;

            // Stripes
            if (KeyBindingsWidget.this.isOdd) {
                context.fill(0, y - getRowMargin() / 2, KeyBindingsWidget.this.width, y + height + getRowMargin() / 2, 0x22FFFFFF);
            }

            // Id
            drawScrollableText(context, textRenderer, Text.translatable(binding.getId()), x + 20, y + 5, x + width - 185, y + 15, 0xFFFFFFFF);

            // Combos
            super.render(context, index, y, x, width, height, mouseX, mouseY, hovered, delta);

            // Add Button
            addButton.setPosition(x + width - 185, y);
            addButton.render(context, mouseX, mouseY, delta);

            KeyBindingsWidget.this.isOdd = !KeyBindingsWidget.this.isOdd;

            //context.fill(x, y, x + width, y + height, 0x770000FF); // Debug for bounds
        }

        private void onAddButtonPressed(ButtonWidget addButton) {
            ComboEntry entry = new ComboEntry(this, new KeyCombo(binding.getId(), List.of(), binding.getDefaultSettings()));
            binding.addKeyCombo(entry.combo);
            entry.startRecording();
            addChild(entry);
        }

        @Override
        public List<Selectable> selectableChildren() {
            return filtered ? List.of() : CollectionUtil.add(
                    super.selectableChildren(),
                    addButton
            );
        }

        @Override
        public List<Element> children() {
            return filtered ? List.of() : CollectionUtil.add(
                    super.children(),
                    addButton
            );
        }

        @Override
        public int getHeight() {
            return filtered ? 0 : Math.max(super.getHeight(), 20);
        }

        @Override
        public int getMinWidth() {
            return filtered ? 0 : 22 + textRenderer.getWidth(Text.translatable(binding.getId())) + 187;
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
        private boolean recording = false;

        private boolean filtered = false;
        public ComboEntry(BindingEntry parent, KeyCombo combo) {
            this.parent = parent;
            this.combo = combo;
            keyButton = ButtonWidget.builder(
                    getKeyButtonMessage(),
                    this::onKeyButtonPressed
            ).size(100, 20).build();
        }

        @Override
        public void applySearchQuery(String query, SearchTarget target) {
            if (target == SearchTarget.KEY) {
                filtered = !applyQueryFormatting(getKeyButtonMessage().getString()).contains(query);
            } else {
                filtered = false;
            }
        }

        @Override
        public boolean isFiltered() {
            return filtered;
        }

        private Text getKeyButtonMessage() {
            return getKeyButtonMessage(combo.getKeys());
        }

        private Text getKeyButtonMessage(List<Key> keys) {
            int pressCount = combo.getSettings().operationMode().getPressCount();
            return keys.isEmpty() ? Text.translatable("key.keyboard.unknown") : CollectionUtil.toText(keys, Key::getText,  pressCount != 1 ? (pressCount + "x ") : "", " + ", "");
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            if (filtered)
                return;

            KeyBindingsWidget.this.renderedSomething = true;

            RenderSystem.enableBlend();
            //context.fill(x, y, x + width, y + height, hovered? 0x55FF0000 : 0x5500FF00); // Debug for hovering
            context.drawTexture(combo.getSettings().operationMode().getIcon(), x, y, 0, 0, 20, 20, 20, 20);
            keyButton.setPosition(x + width - 165, y);
            keyButton.render(context, mouseX, mouseY, delta);
            if (recording) {
                int keyX1 = keyButton.getX();
                int keyY1 = keyButton.getY();
                int keyX2 = keyX1 + keyButton.getWidth();
                int keyY2 = keyY1 + keyButton.getHeight();
                context.fill(keyX1, keyY1, keyX2, keyY1 + 1, 0xFFFF0000);
                context.fill(keyX1, keyY2 - 1, keyX2, keyY2, 0xFFFF0000);
                context.fill(keyX1, keyY1, keyX1 + 1, keyY2, 0xFFFF0000);
                context.fill(keyX2 - 1, keyY1, keyX2, keyY2, 0xFFFF0000);
            }
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
            recording = true;
            KeyUtil.startRecording(keys -> {
                recording = false;
                combo.setKeys(keys);
                keyButton.setMessage(getKeyButtonMessage());
                KeyBindingConfig.getCurrent().save();
            }, keys -> {
                keyButton.setMessage(getKeyButtonMessage(keys));
            });
            RebindMyKeys.DEBUG.info("Key button pressed");
        }

        private void onRemoveButtonPressed(ButtonWidget removeButton) {
            parent.binding.removeKeyCombo(combo);
            parent.removeChild(this);
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
                            KeyBindingsWidget.this::resetAll
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
            return filtered ? List.of() : List.of(keyButton, removeButton, settingsButton, resetButton);
        }

        @Override
        public List<? extends Element> children() {
            return filtered ? List.of() : List.of(keyButton, removeButton, settingsButton, resetButton);
        }

        @Override
        public int getHeight() {
            return filtered ? 0 : 20;
        }

        @Override
        public int getMinWidth() {
            return -1;
        }
    }

    public abstract class BindingParentEntry extends ParentEntry {
        public final static Identifier EXPAND = Identifier.of("rebindmykeys", "textures/gui/expand.png");
        public final static Identifier COLLAPSE = Identifier.of("rebindmykeys", "textures/gui/collapse.png");

        private List<Entry> collapsedChildren;
        protected boolean collapsed = false;
        protected int y;

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            if (isFiltered())
                return;

            KeyBindingsWidget.this.renderedSomething = true;

            int color = collapseButtonHovered(mouseX, mouseY) ? 0xFFAAAAAA : 0xFFFFFFFF;

            // Stripes
            if (KeyBindingsWidget.this.isOdd) {
                context.fill(0, y - getRowMargin() / 2, KeyBindingsWidget.this.width, y + 20 + getRowMargin() / 2, 0x22FFFFFF);
            }

            renderParent(context, index, y, x, width, height, mouseX, mouseY, color, delta);

            // Collapse/Expand
            this.y = y;
            RenderUtil.setShaderColor(context, color);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            context.drawTexture(collapsed ? EXPAND : COLLAPSE, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
            context.setShaderColor(1f, 1f, 1f, 1f);

            KeyBindingsWidget.this.isOdd = !KeyBindingsWidget.this.isOdd;

            // Children
            super.render(context, index, y + 20, x, width, height - 20, mouseX, mouseY, hovered, delta);
        }

        public abstract void renderParent(DrawContext context, int index,  int y, int x, int width, int height, int mouseX, int mouseY, int color, float delta);

        private boolean collapseButtonHovered(double mouseX, double mouseY) {
            return mouseY >= y && mouseY < y + 20;
        }

        public void toggleCollapsed() {
            collapsed = !collapsed;
            if (collapsed) {
                collapsedChildren = getChildren();
                clearChildren();
            } else {
                setChildren(collapsedChildren);
                collapsedChildren = null;
            }
            KeyBindingsWidget.this.calcWidth();
            setScrollAmount(getScrollAmount());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isFiltered())
                return false;

            if (collapseButtonHovered(mouseX, mouseY)) {
                toggleCollapsed();
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public int getHeight() {
            return isFiltered() ? 0 : getParentHeight() + (collapsed ? 0 : super.getHeight());
        }

        public abstract int getParentHeight();

        @Override
        public int getMinWidth() {
            return isFiltered() ? 0 : Math.max(getParentMinWidth(), (collapsed ? 0 : super.getMinWidth()));
        }
        public abstract int getParentMinWidth();
    }

    public class CategoryEntry extends BindingParentEntry {
        private final String translationKey;
        public CategoryEntry(String translationKey) {
            this.translationKey = translationKey;
        }

        @Override
        public void renderParent(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, int color, float delta) {
            // Name
            drawScrollableText(context, textRenderer, Text.translatable(translationKey).formatted(Formatting.UNDERLINE), x + 20, y, x + width, y + 20, color);
        }

        @Override
        public int getParentHeight() {
            return 20;
        }

        @Override
        public int getParentMinWidth() {
            return textRenderer.getWidth(Text.translatable(translationKey)) + 20;
        }
    }

    public class ModEntry extends BindingParentEntry {
        private final String name;
        private final ModIcon icon;
        public ModEntry(@Nullable ModContainer mod) {
            this.name = ModInfo.getModName(mod);
            this.icon = new ModIcon(mod, 16, 128);
        }

        @Override
        public void renderParent(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, int color, float delta) {
            // Name
            drawScrollableText(context, textRenderer, Text.literal(name).formatted(Formatting.UNDERLINE), x + 40, y, x + width - 20, y + 20, color);

            // Icon
            if (icon.available()) {
                int iconX = x + width / 2 - textRenderer.getWidth(name) / 2 - 12;
                //context.fill(iconX + 1, y + 1, iconX + 19, y + 19, 0xFF000000);
                RenderUtil.setShaderColor(context, color);
                icon.renderLowRes(context, iconX + 2, y + 2, 0);
                context.setShaderColor(1f, 1f, 1f, 1f);
                if (iconHovered(iconX, mouseX, mouseY)) {
                    KeyBindingsWidget.this.tooltipRenderer(tooltipContext -> {
                        int tooltipX = MathHelper.clamp(mouseX + 6, 4, MinecraftClient.getInstance().getWindow().getScaledWidth() - 132);
                        int tooltipY = MathHelper.clamp(mouseY + 6, 4, MinecraftClient.getInstance().getWindow().getScaledHeight() - 132);
                        TooltipBackgroundRenderer.render(tooltipContext, tooltipX, tooltipY, 128, 128, 400);
                        icon.renderHighRes(tooltipContext, tooltipX, tooltipY, 401);
                    });
                }
            }
        }

        private boolean iconHovered(int iconX, double mouseX, double mouseY) {
            return mouseX >= iconX + 2 && mouseX < iconX + 18 && mouseY >= y + 2 && mouseY < y + 18;
        }

        @Override
        public int getParentHeight() {
            return 20;
        }

        @Override
        public int getParentMinWidth() {
            return textRenderer.getWidth(name) + 60;
        }
    }

    public static String applyQueryFormatting(String value) {
        return value.toLowerCase().replaceAll("[ \\-_.,!?`^´'*~\"/\\\\()\\[\\]{}%&\t]+", "");
    }
}
