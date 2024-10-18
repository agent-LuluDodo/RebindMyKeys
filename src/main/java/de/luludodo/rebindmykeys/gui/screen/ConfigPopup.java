package de.luludodo.rebindmykeys.gui.screen;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.gui.widget.ConfigWidget;
import de.luludodo.rebindmykeys.gui.widget.ResizableButtonWidget;
import de.luludodo.rebindmykeys.gui.widget.resizable.HeightCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.WidthCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.XCalculator;
import de.luludodo.rebindmykeys.gui.widget.resizable.YCalculator;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public abstract class ConfigPopup<S extends ConfigPopup<S, C>, C extends ConfigWidget<C, S>> extends PopupScreen {
    public ConfigPopup(@Nullable Screen parent, Text title) {
        super(parent, title);
        setWidth(width -> 250);
        setHeight(height -> {
            int greedySpace = getReservedSpace() + getAdditionalHeight(); // + getAdditionalHeight();
            if (greedySpace > height) {
                RebindMyKeys.DEBUG.info("Using height: {} ({})", height, greedySpace);
                return height;
            }
            RebindMyKeys.DEBUG.info("Using greedySpace: {} ({})", greedySpace, height);
            return greedySpace;
        });

        activateEditMode();
    }

    private int topSpace = 5;
    public void setTopSpace(int topSpace) {
        this.topSpace = topSpace;
    }
    public int getTopSpace() {
        return topSpace;
    }

    private int bottomSpace = 35;
    public void setBottomSpace(int bottomSpace) {
        this.bottomSpace = bottomSpace;
    }
    public int getBottomSpace() {
        return bottomSpace;
    }

    public int getReservedSpace() {
        return getTopSpace() + getBottomSpace();
    }

    public abstract C getConfigWidget(MinecraftClient client);

    private C configs;
    @Override
    public void init() {
        initTop();
        configs = addResizableChild(getConfigWidget(client));
        initBottom();

        configs.init(); // can't resize the other elements if they haven't been created & registered, so wait for that and then call init
    }

    public C getConfigs() {
        return configs;
    }

    @SuppressWarnings("EmptyMethod")
    public void initTop() {}
    public void initBottom() {
        addResizableChild(getApplyButton(
                width -> 10,
                height -> height - 30,
                width -> width / 3 - 10,
                height -> 20
        ));
        addResizableChild(getResetButton(
                width -> width / 3 + 5,
                height -> height - 30,
                width -> width / 3 - 10,
                height -> 20
        ));
        addResizableChild(getQuitButton(
                width -> width / 3 * 2,
                height -> height - 30,
                width -> width / 3 - 10,
                height -> 20
        ));
    }

    protected ResizableButtonWidget getApplyButton(XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height) {
        return ResizableButtonWidget.builder(
                this,
                Text.translatable("rebindmykeys.gui.apply"),
                button -> save()
        ).dimensions(x, y, width, height).build();
    }
    protected ResizableButtonWidget getResetButton(XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height) {
        return ResizableButtonWidget.builder(
                this,
                Text.translatable("rebindmykeys.gui.reset"),
                button -> reset()
        ).dimensions(x, y, width, height).build();
    }
    protected ResizableButtonWidget getQuitButton(XCalculator x, YCalculator y, WidthCalculator width, HeightCalculator height) {
        return ResizableButtonWidget.builder(
                this,
                Text.translatable("rebindmykeys.gui.quit"),
                button -> close()
        ).dimensions(x, y, width, height).build();
    }

    public int getAdditionalHeight() {
        return configs == null ? 0 : configs.getTargetHeight();
    }

    public void resize() {
        Window window = MinecraftClient.getInstance().getWindow();
        resize(client, window.getScaledWidth(), window.getScaledHeight());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    public void activateEditMode() {}
    public void deactivateEditMode() {}
    public abstract void save();
    public abstract void reset();
    public abstract boolean hasChanges();

    @Override
    public void close() {
        askToSaveChanges(() -> {
            deactivateEditMode();
            super.close();
        });
    }

    public void askToSaveChanges(Action action) {
        if (hasChanges()) {
            assert client != null;
            ConfirmPopup.create(
                    client,
                    this,
                    Text.translatable("rebindmykeys.gui.confirmUnsavedChanges.title"),
                    Text.translatable("rebindmykeys.gui.confirmUnsavedChanges.message"),
                    () -> {
                        save();
                        action.run();
                    },
                    action,
                    true
            );
        } else {
            action.run();
        }
    }
}