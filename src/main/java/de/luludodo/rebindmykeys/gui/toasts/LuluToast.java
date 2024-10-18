package de.luludodo.rebindmykeys.gui.toasts;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

public class LuluToast implements Toast {
    private final static TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final static long DURATION = 15000;
    @SuppressWarnings("unused")
    public enum Type {
        ERROR("textures/gui/error_toast.png"),
        WARN("textures/gui/warn_toast.png"),
        INFO("textures/gui/info_toast.png"),
        SUCCESS("textures/gui/success_toast.png"),;

        private final Identifier id;
        Type(String path) {
            id = Identifier.of("rebindmykeys", path);
        }

        public Identifier getId() {
            return id;
        }
    }

    public static void showAndLogError(Logger log, String message, @Nullable Exception exception) {
        MinecraftClient.getInstance().getToastManager().add(
                new LuluToast(Type.ERROR, message)
        );
        RebindMyKeys.DEBUG.info("Showing toast (error)");
        if (exception == null) {
            log.error(message);
        } else {
            log.error(message, exception);
        }
    }

    public static void showAndLogWarn(Logger log, String message, @Nullable Exception exception) {
        MinecraftClient.getInstance().getToastManager().add(
                new LuluToast(Type.WARN, message)
        );
        RebindMyKeys.DEBUG.info("Showing toast (warn)");
        if (exception == null) {
            log.warn(message);
        } else {
            log.warn(message, exception);
        }
    }

    private final Type type;
    private final List<OrderedText> message;
    private final int width;
    private final int height;
    private final long startTime;
    public LuluToast(Type type, String message) {
        this.type = type;
        this.message = textRenderer.wrapLines(StringVisitable.plain(message), 242);
        this.width = 250;
        this.height = 8 + this.message.size() * 10;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        context.fill(0, 0, 100, 100, 0);
        RenderUtil.render9PartTexture(context, type.getId(), 0, 0, width, height, 0, 0, 16, 16, 7, 2);
        int y = 5;
        for (OrderedText line : this.message) {
            context.drawTextWithShadow(textRenderer, line, 4, y, 0xFFFFFFFF);
            y += 10;
        }
        return System.currentTimeMillis() - this.startTime > DURATION ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
