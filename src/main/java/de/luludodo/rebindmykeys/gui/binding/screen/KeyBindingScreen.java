package de.luludodo.rebindmykeys.gui.binding.screen;

import de.luludodo.rebindmykeys.gui.globalConfig.screen.GlobalConfigPopup;
import de.luludodo.rebindmykeys.gui.profile.screen.ProfileConfigPopup;
import de.luludodo.rebindmykeys.gui.screen.ResizableScreen;
import de.luludodo.rebindmykeys.gui.binding.widget.KeyBindingsWidget;
import de.luludodo.rebindmykeys.gui.widget.IconButtonWidget;
import de.luludodo.rebindmykeys.gui.widget.ResizableButtonWidget;
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
