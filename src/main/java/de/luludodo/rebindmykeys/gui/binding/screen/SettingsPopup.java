package de.luludodo.rebindmykeys.gui.binding.screen;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.gui.binding.widget.SettingsWidget;
import de.luludodo.rebindmykeys.gui.screen.PopupScreen;
import de.luludodo.rebindmykeys.gui.widgets.ResizableButtonWidget;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class SettingsPopup extends PopupScreen {
    private final KeyCombo combo;
    public SettingsPopup(@NotNull KeyBindingScreen parent, KeyCombo combo) {
        super(parent, Text.translatable("rebindmykeys.gui.settings.title"));
        setWidth(width -> 250);
        setHeight(height -> Math.min(height, 60 + getAdditionalHeight()));
        this.combo = combo;
    }

    @Override
    public void init() {
        SettingsWidget settings = addResizableChild(new SettingsWidget(client, this, combo));
        addResizableChild(ResizableButtonWidget.builder(this, Text.translatable("rebindmykeys.gui.settings.reset"), button -> {
            RebindMyKeys.DEBUG.info("Reset Settings pressed");
        }).dimensions(
                /* x */ width -> 10,
                /* y */ height -> height - 30,
                /* width */ width -> 110,
                /* height */ height -> 20
        ).build());
        addResizableChild(ResizableButtonWidget.builder(this, Text.translatable("rebindmykeys.gui.settings.resetKeys"), button -> {
            RebindMyKeys.DEBUG.info("Reset Keys pressed");
        }).dimensions(
                /* x */ width -> width - 120,
                /* y */ height -> height - 30,
                /* width */ width -> 110,
                /* height */ height -> 20
        ).build());
        settings.init(); // can't resize the other elements if they haven't been created & registered, so wait for that and then call init
    }

    private int additionalHeight = 0;
    public void additionalHeight(final int height) {
        additionalHeight += height;
    }
    public int getAdditionalHeight() {
        return additionalHeight;
    }
    public void resetHeight() {
        additionalHeight = 0;
    }

    public void resize() {
        resize(client, getParent().width, getParent().height);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, getPopupCenterX(),  getPopupTop() + 7, 0xFFFFFFFF);
        context.drawHorizontalLine(getPopupLeft() + 8, getPopupRight() - 9, getPopupTop() + 18, 0xFFFFFFFF); // why is x2 one more to the right than it should be ?!?!??! vanilla implementation is wierd
        context.drawHorizontalLine(getPopupLeft() + 9, getPopupRight() - 8, getPopupTop() + 19, 0xFF3F3F3F);
    }
}
