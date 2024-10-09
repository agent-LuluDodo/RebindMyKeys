package de.luludodo.rebindmykeys.gui.binding.screen;

import de.luludodo.rebindmykeys.config.GlobalConfig;
import de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget.SearchTarget;
import de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget.SortAfter;
import de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget.SortOrder;
import de.luludodo.rebindmykeys.gui.globalConfig.screen.GlobalConfigPopup;
import de.luludodo.rebindmykeys.gui.profile.screen.ProfileConfigPopup;
import de.luludodo.rebindmykeys.gui.screen.ResizableScreen;
import de.luludodo.rebindmykeys.gui.binding.widget.keyBindingWidget.KeyBindingsWidget;
import de.luludodo.rebindmykeys.gui.widget.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class KeyBindingScreen extends ResizableScreen {
    private final Screen parent;
    public KeyBindingScreen(Screen parent) {
        super(Text.translatable("controls.keybinds.title"));
        this.parent = parent;
    }

    private KeyBindingsWidget bindings;
    private ResizableButtonWidget resetButton;
    @Override
    protected void init() {
        bindings = addResizableChild(
                new KeyBindingsWidget(this, client)
        );
        resetButton = addResizableChild(
                ResizableButtonWidget.builder(
                        this,
                        Text.translatable("controls.resetAll"),
                        button -> bindings.resetAll()
                ).dimensions(
                        /* x: */ width -> width / 2 - 155,
                        /* y: */ height -> height - 29,
                        /* width: */ width -> 150,
                        /* height: */ height -> 20
                ).build()
        );
        addResizableChild(
                ResizableButtonWidget.builder(
                        this,
                        ScreenTexts.DONE,
                        button -> close()
                ).dimensions(
                        /* x: */ width -> width / 2 + 5,
                        /* y: */ height -> height - 29,
                        /* width: */ width -> 150,
                        /* height: */ height -> 20
                ).build()
        );
        addResizableChild(
                ResizableCyclingButtonWidget.luluBuilder(
                        SearchTarget::getTranslation
                ).values(SearchTarget.values()).initially(SearchTarget.NAME).omitKeyText().build(
                        this,
                        /* x: */ width -> width / 2 - 155,
                        /* y: */ height -> height - 49,
                        /* width: */ width -> 60,
                        /* height: */ height -> 20,
                        Text.empty(),
                        (button, value) -> bindings.setSearchTarget(value)
                )
        );
        ResizableTextFieldWidget search = addResizableChild(
                new ResizableTextFieldWidget(
                        textRenderer,
                        this,
                        /* x: */ width -> width / 2 - 95,
                        /* y: */ height -> height - 49,
                        /* width: */ width -> 90,
                        /* height: */ height -> 20,
                        Text.translatable("rebindmykeys.gui.keyBindings.search")
                )
        );
        search.setPlaceholder(Text.translatable("rebindmykeys.gui.keyBindings.search"));
        search.setChangedListener(bindings::setSearchQuery);
        addResizableChild(
                ResizableCyclingButtonWidget.luluBuilder(
                        SortAfter::getTranslation
                ).values(SortAfter.values()).initially(GlobalConfig.getCurrent().getSortAfter()).omitKeyText().build(
                        this,
                        /* x: */ width -> width / 2 + 5,
                        /* y: */ height -> height - 49,
                        /* width: */ width -> 130,
                        /* height: */ height -> 20,
                        Text.empty(),
                        (button, value) -> {
                            bindings.setSortAfter(value);
                            GlobalConfig.getCurrent().setSortAfter(value);
                        }
                )
        );
        addResizableChild(
                CyclingIconButtonWidget.iconBuilder(SortOrder::getIcon)
                        .values(SortOrder.values())
                        .initially(GlobalConfig.getCurrent().getSortOrder())
                        .iconDimensions(2, 2, 16, 16)
                        .iconTextureSize(16, 16)
                        .build(
                                this,
                                /* x: */ width -> width / 2 + 135,
                                /* y: */ height -> height - 49,
                                /* width: */ width -> 20,
                                /* height: */ height -> 20,
                                (button, value) -> {
                                    bindings.setSortOrder(value);
                                    GlobalConfig.getCurrent().setSortOrder(value);
                                }
                        )
        );
        addResizableChild(
                IconButtonWidget.builder(
                        this,
                        Identifier.of("rebindmykeys", "textures/gui/settings.png"),
                        button -> client.setScreen(new GlobalConfigPopup(this))
                ).dimensions(
                        /* x: */ width -> width - 20,
                        /* y: */ height -> 0,
                        /* width: */ width -> 20,
                        /* height: */ height -> 20
                ).build()
        );
        addResizableChild(
                ResizableButtonWidget.builder(
                        this,
                        Text.translatable("rebindmykeys.gui.profiles"),
                        button -> client.setScreen(new ProfileConfigPopup(this))
                ).dimensions(
                        /* x: */ width -> 0,
                        /* y: */ height -> 0,
                        /* width: */ width -> 80,
                        /* height: */ height -> 20
                ).build()
        );
        addResizableChild(
                CyclingIconButtonWidget.iconOnOffBuilder(
                        Identifier.of("rebindmykeys", "textures/gui/collapse_all.png"),
                        Identifier.of("rebindmykeys", "textures/gui/expand_all.png")
                ).setRenderBackground(false).build(
                        this,
                        /* x: */ width -> width - 40,
                        /* y: */ height -> 0,
                        /* width: */ width -> 20,
                        /* height: */ height -> 20,
                        (button, value) -> {
                            if (value) {
                                bindings.expandAll();
                            } else {
                                bindings.collapseAll();
                            }
                        }
                )
        );
    }

    public void reloadEntries() {
        bindings.reloadEntries();
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        resetButton.active = !bindings.areAllDefault();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(context);
    }
}
