package de.luludodo.rebindmykeys.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.util.KeyBindingUtil;
import de.luludodo.rebindmykeys.util.TimerUtil;
import de.luludodo.rebindmykeys.util.enums.Key;
import de.luludodo.rebindmykeys.util.enums.OnKeyAction;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Unique private OnKeyAction rebindmykeys$action;
    @Unique private static boolean rebindmykeys$debugCrashActive = false;

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void rebindmykeys$onKey(long window, int keycode, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (OnKeyAction.hasCurrentAction()) {
            rebindmykeys$action = OnKeyAction.consumeCurrentAction();
            switch (rebindmykeys$action) {
                case START_DEBUG_CRASH -> rebindmykeys$debugCrashActive = true;
                case STOP_DEBUG_CRASH -> rebindmykeys$debugCrashActive = false;
            }
            return;
        }

        if (window != client.getWindow().getHandle()) return; // if the minecraft window isn't focused return

        if (action == GLFW.GLFW_PRESS && keycode == Key.Z.getCode()) // benchmark
            TimerUtil.start();

        if (action != GLFW.GLFW_PRESS && action != GLFW.GLFW_RELEASE) return; // if the action is not press and not release

        InputUtil.Key key = InputUtil.fromKeyCode(keycode, scancode);
        KeyBindingUtil.onKeyAll(key, action == GLFW.GLFW_PRESS);

        KeyBindingUtil.updateAll();

        ci.cancel();
    }

    @Redirect(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/Window;getHandle()J"
            )
    ) // 336: if (window != this.client.getWindow().getHandle()) {
    private long rebindmykeys$all$1(Window instance, long window, int key, int scancode, int action, int modifiers) {
        return window;
    }

    @Redirect(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z",
                    ordinal = 0
            )
    ) // 339: boolean bl = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_F3);
    private boolean rebindmykeys$f3Combos(long handle, int code) {
        return rebindmykeys$debugCrashActive || switch (rebindmykeys$action) {
            case ACTION_PAUSE_WITHOUT_MENU, ACTION_RELOAD_CHUNKS, TOGGLE_HITBOXES, ACTION_COPY_LOCATION,
                 ACTION_CLEAR_CHAT, TOGGLE_CHUNK_BORDERS, TOGGLE_ADVANCED_TOOLTIPS, ACTION_COPY_SERVER_DATA,
                 ACTION_COPY_CLIENT_DATA, ACTION_GENERATE_PERFORMANCE_METRICS, TOGGLE_SPECTATOR,
                 TOGGLE_PAUSE_ON_LOST_FOCUS, ACTION_PRINT_HELP, ACTION_DUMP_TEXTURES, ACTION_RELOAD_RESOURCES,
                 ACTION_OPEN_GAMEMODE_SWITCHER -> true; // F3 combos
            default -> false;
        };
    }

    @Redirect(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z",
                    ordinal = 1
            )
    ) // 341: if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_C) || !bl) {
    private boolean rebindmykeys$debugCrash$1(long handle, int code) {
        return rebindmykeys$debugCrashActive;
    }

    @Redirect(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z",
                    ordinal = 2
            )
    ) // 344: } else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_C) && bl) {
    private boolean rebindmykeys$debugCrash$2(long handle, int code) {
        return rebindmykeys$debugCrashActive;
    }

    @Inject(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "JUMP",
                    opcode = Opcodes.IFNULL,
                    ordinal = 0
            )
    ) // 350: if ((screen2 = this.client.currentScreen) != null) {
    // 383: && bl2) {
    private void rebindmykeys$all$2(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci, @Local(ordinal = 1) LocalRef<Screen> screen2) {
        screen2.set(null);
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z", ordinal = 0)),
            constant = @Constant(intValue = 1, ordinal = 0)
    ) // 364: if (!(action != 1 ||
    private int rebindmykeys$fullscreen_screenshot$1(int constant, long window, int keycode, int scancode, int action, int modifiers) {
        return switch (rebindmykeys$action) {
            case TOGGLE_FULLSCREEN, ACTION_SCREENSHOT -> action;
            default -> action + 1;
        };
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z", ordinal = 0)),
            constant = @Constant(classValue = KeybindsScreen.class, ordinal = 0)
    ) // 364: || this.client.currentScreen instanceof KeybindsScreen && ((KeybindsScreen)screen2).lastKeyCodeUpdateTime > Util.getMeasuringTimeMs() - 20L)) {
    private Class<?> rebindmykeys$fullscreen_screenshot$2(Class<?> clazz) {
        // won't be reached if check for fullscreen_screenshot$1 fails so no switch needed
        return RebindMyKeys.class; // this.client.currentScreen will never be an instance of RebindMyKeys.class :)
    }

    @Redirect(
            method = "onKey",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z", ordinal = 0)),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;matchesKey(II)Z",
                    ordinal = 0
            )
    ) // 365: if (this.client.options.fullscreenKey.matchesKey(key, scancode)) {
    private boolean rebindmykeys$fullscreen(KeyBinding instance, int keyCode, int scanCode) {
        return rebindmykeys$action == OnKeyAction.TOGGLE_FULLSCREEN;
    }

    @Redirect(
            method = "onKey",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z", ordinal = 0)),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;matchesKey(II)Z",
                    ordinal = 1
            )
    ) // 370: if (this.client.options.screenshotKey.matchesKey(key, scancode)) {
    private boolean rebindmykeys$screenshot(KeyBinding instance, int keyCode, int scanCode) {
        return rebindmykeys$action == OnKeyAction.ACTION_SCREENSHOT;
    }

    @Redirect(
            method = "onKey",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/NarratorManager;isActive()Z", ordinal = 0)),
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Boolean;booleanValue()Z",
                    ordinal = 0
            )
    ) // 378: if (this.client.getNarratorManager().isActive() && this.client.options.getNarratorHotkey().getValue().booleanValue()) {
    private boolean rebindmykeys$narrator$1(Boolean instance) {
        return rebindmykeys$action == OnKeyAction.ACTION_CYCLE_NARRATOR;
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/NarratorManager;isActive()Z", ordinal = 0)),
            constant = @Constant(intValue = 0)
    ) // 383: if (action != 0 &&
    private int rebindmykeys$narrator$2(int constant, long window, int keycode, int scanCode, int action, int modifiers) {
        return action;
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/NarratorManager;isActive()Z", ordinal = 0)),
            constant = @Constant(intValue = GLFW.GLFW_KEY_B)
    ) // 383: && key == GLFW.GLFW_KEY_B &&
    private int rebindmykeys$narrator$3(int constant, long window, int keycode, int scanCode, int action, int modifiers) {
        return keycode;
    }

    @Redirect(
            method = "onKey",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/NarratorManager;isActive()Z", ordinal = 0)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z", ordinal = 0)
    ) // 383: && Screen.hasControlDown() &&
    private boolean rebindmykeys$narrator$4() {
        return true;
    }

    @ModifyConstant(
            method = "onKey"
    )
}