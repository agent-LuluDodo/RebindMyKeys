package de.luludodo.rebindmykeys.gui.screen;

import de.luludodo.rebindmykeys.config.GlobalConfig;
import de.luludodo.rebindmykeys.gui.widget.ResizableButtonWidget;
import de.luludodo.rebindmykeys.util.TextUtil;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Ask the user a YES or NO question.
 */
public class ConfirmPopup extends PopupScreen {
    private final List<OrderedText> wrappedMessage;
    private final int wrappedMessageHeight;
    private final Action onYes;
    private final Action onNo;
    private final boolean showCancel;
    public ConfirmPopup(@NotNull Screen parent, Text title, Text message, @NotNull Action onYes, @NotNull Action onNo, boolean showCancel) {
        super(parent, title);

        setWidth(w -> 200);
        this.wrappedMessage = TextUtil.wrapLinesWithNewlines(
                MinecraftClient.getInstance().textRenderer,
                message,
                180
        );
        wrappedMessageHeight = wrappedMessage.size() * MinecraftClient.getInstance().textRenderer.fontHeight;
        setHeight(h -> 35 + wrappedMessageHeight);

        setCloseButtonEnabled(false);

        this.onYes = onYes;
        this.onNo = onNo;
        this.showCancel = showCancel;
    }
    public ConfirmPopup(@NotNull Screen parent, Text title, Text message, @NotNull Action onYes, @NotNull Action onNo) {
        this(parent, title, message, onYes, onNo, false);
    }

    public static void create(@NotNull MinecraftClient client, @NotNull Screen parent, Text title, Text message, @NotNull Action onYes, @NotNull Action onNo, boolean showCancel) {
        if (GlobalConfig.getCurrent().getShowConfirmPopups()) {
            ConfirmPopup popup = new ConfirmPopup(parent, title, message, onYes, onNo, showCancel);
            client.setScreen(popup);
        } else {
            onYes.run();
        }
    }

    public static void create(@NotNull MinecraftClient client, @NotNull Screen parent, Text title, Text message, @NotNull Action onYes, @NotNull Action onNo) {
        if (GlobalConfig.getCurrent().getShowConfirmPopups()) {
            ConfirmPopup popup = new ConfirmPopup(parent, title, message, onYes, onNo);
            client.setScreen(popup);
        } else {
            onYes.run();
        }
    }

    @Override
    public void init() {
        if (showCancel) {
            addResizableChild(
                    ResizableButtonWidget.builder(
                            this,
                            ScreenTexts.CANCEL,
                            button -> close()
                    ).dimensions(
                            /* x */ width -> width - 60,
                            /* y */ height -> height - 25,
                            /* width */ width -> 55,
                            /* height */ height -> 20
                    ).build()
            );
            addResizableChild(
                    ResizableButtonWidget.builder(
                            this,
                            ScreenTexts.NO,
                            button -> {
                                close();
                                onNo.run();
                            }
                    ).dimensions(
                            /* x */ width -> width - 102,
                            /* y */ height -> height - 25,
                            /* width */ width -> 40,
                            /* height */ height -> 20
                    ).build()
            );
            addResizableChild(
                    ResizableButtonWidget.builder(
                            this,
                            ScreenTexts.YES,
                            button -> {
                                close();
                                onYes.run();
                            }
                    ).dimensions(
                            /* x */ width -> width - 144,
                            /* y */ height -> height - 25,
                            /* width */ width -> 40,
                            /* height */ height -> 20
                    ).build()
            );
        } else {
            addResizableChild(
                    ResizableButtonWidget.builder(
                            this,
                            ScreenTexts.NO,
                            button -> {
                                close();
                                onNo.run();
                            }
                    ).dimensions(
                            /* x */ width -> width - 45,
                            /* y */ height -> height - 25,
                            /* width */ width -> 40,
                            /* height */ height -> 20
                    ).build()
            );
            addResizableChild(
                    ResizableButtonWidget.builder(
                            this,
                            ScreenTexts.YES,
                            button -> {
                                close();
                                onYes.run();
                            }
                    ).dimensions(
                            /* x */ width -> width - 87,
                            /* y */ height -> height - 25,
                            /* width */ width -> 40,
                            /* height */ height -> 20
                    ).build()
            );
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int y = getPopupTop() + 5;
        for (OrderedText wrappedMessageLine : wrappedMessage) {
            context.drawTextWithShadow(textRenderer, wrappedMessageLine, getPopupLeft() + 10, y, 0xFFFFFF);
            y += textRenderer.fontHeight;
        }
    }
}
