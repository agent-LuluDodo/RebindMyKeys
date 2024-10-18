package de.luludodo.rebindmykeys.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.profiles.ProfileManager;
import de.luludodo.rebindmykeys.util.KeyBindingUtil;
import de.luludodo.rebindmykeys.util.KeyUtil;
import de.luludodo.rebindmykeys.util.TimerUtil;
import de.luludodo.rebindmykeys.util.enums.Key;
import de.luludodo.rebindmykeys.util.enums.OnKeyAction;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Stack;

@SuppressWarnings("SameReturnValue")
@Debug(export = true)
@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @SuppressWarnings("unused") @Shadow private boolean switchF3State;
    @Unique private final Stack<OnKeyAction> rebindmykeys$action = new Stack<>(); // needed for recursive calls
    @Unique private static boolean rebindmykeys$debugCrashActive = false;
    @Unique private static boolean rebindmykeys$debugCrashJavaActive = false;
    @Unique private final Stack<Boolean> rebindmykeys$isDebugCombo = new Stack<>();
    @Unique private static boolean rebindmykeys$skipNextDebugToggle = false;

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void rebindmykeys$onKey(long window, int keycode, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (OnKeyAction.hasCurrentAction()) {
            rebindmykeys$action.push(OnKeyAction.consumeCurrentAction());

            if (rebindmykeys$action.peek() == null)
                throw new IllegalStateException("Couldn't consume current action!");
            
            rebindmykeys$isDebugCombo.push(switch (rebindmykeys$action.peek()) {
                case ACTION_PAUSE_WITHOUT_MENU, ACTION_RELOAD_CHUNKS, TOGGLE_HITBOXES, ACTION_COPY_LOCATION,
                     ACTION_CLEAR_CHAT, TOGGLE_CHUNK_BORDERS, TOGGLE_ADVANCED_TOOLTIPS, ACTION_COPY_SERVER_DATA,
                     ACTION_COPY_CLIENT_DATA, TOGGLE_DEBUG_PROFILER, TOGGLE_SPECTATOR,
                     TOGGLE_PAUSE_ON_LOST_FOCUS, ACTION_PRINT_HELP, ACTION_DUMP_TEXTURES, ACTION_RELOAD_RESOURCES,
                     ACTION_OPEN_GAMEMODE_SWITCHER, TOGGLE_PROFILER_CHART, TOGGLE_FRAME_TIME_CHARTS, TOGGLE_NETWORK_CHARTS -> true; // F3 combos
                default -> false;
            });

            switch (rebindmykeys$action.peek()) {
                // Debug Crash
                case START_DEBUG_CRASH -> rebindmykeys$debugCrashActive = true;
                case STOP_DEBUG_CRASH -> rebindmykeys$debugCrashActive = false;

                // Debug Java Crash
                case START_DEBUG_CRASH_JAVA -> rebindmykeys$debugCrashJavaActive = true;
                case STOP_DEBUG_CRASH_JAVA -> rebindmykeys$debugCrashJavaActive = false;
            }

            // Debug Combos
            if (rebindmykeys$isDebugCombo.peek())
                rebindmykeys$skipNextDebugToggle = true;

            return;
        } else if (KeyUtil.isRecording()) {
            InputUtil.Key key = InputUtil.fromKeyCode(keycode, scancode);
            if (action == GLFW.GLFW_PRESS) {
                KeyUtil.addRecordedKey(key);

                ci.cancel();
                return;
            } else if (KeyUtil.isInRecording(key)) {
                KeyUtil.stopRecording();

                ci.cancel();
                return;
            }
        }

        rebindmykeys$action.push(OnKeyAction.UPDATE_SCREEN_KEYS);
        rebindmykeys$isDebugCombo.push(false);

        if (window != client.getWindow().getHandle()) {
            return; // if the minecraft window isn't focused return
        }

        if (action == GLFW.GLFW_PRESS && keycode == Key.Z.getCode()) // benchmark
            TimerUtil.start();

        if (action != GLFW.GLFW_PRESS && action != GLFW.GLFW_RELEASE) {
            return; // if the action is not press and not release
        }

        InputUtil.Key key = InputUtil.fromKeyCode(keycode, scancode);
        KeyBindingUtil.onKey(key, action == GLFW.GLFW_PRESS);

        KeyBindingUtil.update();
    }

    @Inject(method = "onKey", at = @At("RETURN"))
    public void rebindmykeys$endOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        rebindmykeys$action.pop();
        rebindmykeys$isDebugCombo.pop();
    }

    /* ************************************************************************************************************** */
    /*           Allows me to use the Vanilla implementations from Keyboard#onKey instead of writing my own           */
    /* ************************************************************************************************************** */

    @Redirect(
            method = "onKey",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/Window;getHandle()J"
            )
    ) // 336: if (window != this.client.getWindow().getHandle()) {
    private long rebindmykeys$all(Window instance, long window, int key, int scancode, int action, int modifiers) {
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
    private boolean rebindmykeys$debugCrash_debugCombo(long handle, int code) {
        return rebindmykeys$debugCrashActive || rebindmykeys$debugCrashJavaActive || rebindmykeys$isDebugCombo.peek();
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
        return rebindmykeys$debugCrashActive || rebindmykeys$debugCrashJavaActive;
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
        return rebindmykeys$debugCrashActive || rebindmykeys$debugCrashJavaActive;
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
    // 395: if (screen2 != null) {
    private void rebindmykeys$updateScreenKeys(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci, @Local(ordinal = 0) LocalRef<Screen> screen2) {
        if (rebindmykeys$action.peek() != OnKeyAction.UPDATE_SCREEN_KEYS)
            screen2.set(null);
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z",
                            ordinal = 0
                    )
            ),
            constant = @Constant(
                    intValue = 1,
                    ordinal = 1
            )
    ) // 364: if (!(action != 1 ||
    private int rebindmykeys$fullscreen_screenshot$1(int constant, long window, int keycode, int scancode, int action, int modifiers) {
        return switch (rebindmykeys$action.peek()) {
            case TOGGLE_FULLSCREEN, ACTION_SCREENSHOT -> action;
            default -> action + 1;
        };
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyConstant(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z",
                            ordinal = 0
                    )
            ),
            constant = @Constant(
                    classValue = KeybindsScreen.class,
                    ordinal = 0
            )
    ) // 364: || this.client.currentScreen instanceof KeybindsScreen && ((KeybindsScreen)screen2).lastKeyCodeUpdateTime > Util.getMeasuringTimeMs() - 20L)) {
    private Class<?> rebindmykeys$fullscreen_screenshot$2(Object instance, Class<?> clazz) {
        // won't be reached if check for fullscreen_screenshot$1 fails so no switch needed
        return RebindMyKeys.class; // this.client.currentScreen will never be an instance of RebindMyKeys.class :)
    }

    @Redirect(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;matchesKey(II)Z",
                    ordinal = 0
            )
    ) // 365: if (this.client.options.fullscreenKey.matchesKey(key, scancode)) {
    private boolean rebindmykeys$fullscreen(KeyBinding instance, int keyCode, int scanCode) {
        return rebindmykeys$action.peek() == OnKeyAction.TOGGLE_FULLSCREEN;
    }

    @Redirect(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;matchesKey(II)Z",
                    ordinal = 1
            )
    ) // 370: if (this.client.options.screenshotKey.matchesKey(key, scancode)) {
    private boolean rebindmykeys$screenshot(KeyBinding instance, int keyCode, int scanCode) {
        return rebindmykeys$action.peek() == OnKeyAction.ACTION_SCREENSHOT;
    }

    @Redirect(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/NarratorManager;isActive()Z",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Boolean;booleanValue()Z",
                    ordinal = 0
            )
    ) // 378: if (this.client.getNarratorManager().isActive() && this.client.options.getNarratorHotkey().getValue().booleanValue()) {
    private boolean rebindmykeys$narrator$1(Boolean instance) {
        return rebindmykeys$action.peek() == OnKeyAction.ACTION_CYCLE_NARRATOR;
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/NarratorManager;isActive()Z",
                            ordinal = 0
                    )
            ),
            constant = @Constant(
                    intValue = 0,
                    ordinal = 0
            )
    ) // 383: if (action != 0 &&
    private int rebindmykeys$narrator$2(int constant, long window, int keycode, int scanCode, int action, int modifiers) {
        return action;
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/NarratorManager;isActive()Z",
                            ordinal = 0
                    )
            ),
            constant = @Constant(
                    intValue = GLFW.GLFW_KEY_B,
                    ordinal = 0
            )
    ) // 383: && key == GLFW.GLFW_KEY_B &&
    private int rebindmykeys$narrator$3(int constant, long window, int keycode, int scanCode, int action, int modifiers) {
        return keycode;
    }

    @Redirect(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/NarratorManager;isActive()Z",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z",
                    ordinal = 0
            )
    ) // 383: && Screen.hasControlDown() &&
    private boolean rebindmykeys$narrator$4() {
        return true;
    }

    @Inject(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "JUMP",
                    opcode = Opcodes.IFNE,
                    ordinal = 0
            )
    ) // 412: if (action == 0) {
    private void rebindmykeys$debugHud$1(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci, @Local(argsOnly = true, ordinal = 2) LocalIntRef actionRef) {
        if (rebindmykeys$action.peek() == OnKeyAction.TOGGLE_DEBUG_HUD) {
            if (rebindmykeys$skipNextDebugToggle) {
                rebindmykeys$skipNextDebugToggle = false;
                actionRef.set(1);
            } else {
                switchF3State = false;
                actionRef.set(0);
            }
        } else {
            actionRef.set(1);
        }
    }

    @Redirect(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V",
                    ordinal = 0
            )
    ) // 413: KeyBinding.setKeyPressed(key2, false);
    // 414: if (bl4 &&
    private void rebindmykeys$debugHud$2(InputUtil.Key key, boolean pressed, @Local(ordinal = 2) LocalBooleanRef bl4) {
        bl4.set(true);
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;",
                            ordinal = 0
                    )
            ),
            constant = @Constant(
                    intValue = GLFW.GLFW_KEY_F3,
                    ordinal = 0
            )
    ) // 414: && key == GLFW.GLFW_KEY_F3) {
    private int rebindmykeys$debugHud$3(int constant, long window, int keycode, int scancode, int action, int modifiers) {
        return keycode;
    }

    @Inject(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=293",
                    ordinal = 0,
                    shift = At.Shift.BY,
                    by = -5
            )
    ) // 424: if (bl4) {
    // 440: if (bl3) {
    private void rebindmykeys$postProcessor_pause_pauseWithoutMenu_debugCombo_hideHud_charts(long window, int keycode, int scancode, int action, int modifiers, CallbackInfo ci, @Local(ordinal = 2) LocalBooleanRef bl4, @Local(ordinal = 1) LocalBooleanRef bl3) {
        bl3.set(false);
        bl4.set(switch (rebindmykeys$action.peek()) {
            case TOGGLE_POST_PROCESSOR, ACTION_PAUSE, TOGGLE_HUD -> true;
            default -> rebindmykeys$isDebugCombo.peek();
        });
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;",
                            ordinal = 0
                    )
            ),
            constant = @Constant(
                    intValue = GLFW.GLFW_KEY_F4,
                    ordinal = 0
            )
    ) // 425: if (key == GLFW.GLFW_KEY_F4 && this.client.gameRenderer != null) {
    private int rebindmykeys$postProcessor(int constant, long window, int keycode, int scancode, int action, int modifiers) {
        return rebindmykeys$action.peek() == OnKeyAction.TOGGLE_POST_PROCESSOR? keycode : keycode + 1;
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;",
                            ordinal = 0
                    )
            ),
            constant = @Constant(
                    intValue = GLFW.GLFW_KEY_ESCAPE,
                    ordinal = 0
            )
    ) // 428: if (key == GLFW.GLFW_KEY_ESCAPE) {
    private int rebindmykeys$pause_pauseWithoutMenu(int constant, long window, int keycode, int scancode, int action, int modifiers) {
        return switch (rebindmykeys$action.peek()) {
            case ACTION_PAUSE, ACTION_PAUSE_WITHOUT_MENU -> keycode;
            default -> keycode + 1;
        };
    }

    @ModifyArg(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Keyboard;processF3(I)Z",
                    ordinal = 0
            ),
            index = 0
    ) // 432: this.switchF3State |= (bl52 |= bl && this.processF3(key));
    private int rebindmykeys$debugCombo(int key) {
        return switch (rebindmykeys$action.peek()) {
            case ACTION_RELOAD_CHUNKS -> GLFW.GLFW_KEY_A;
            case TOGGLE_HITBOXES -> GLFW.GLFW_KEY_B;
            case ACTION_CLEAR_CHAT -> GLFW.GLFW_KEY_D;
            case TOGGLE_CHUNK_BORDERS -> GLFW.GLFW_KEY_G;
            case TOGGLE_ADVANCED_TOOLTIPS -> GLFW.GLFW_KEY_H;
            case ACTION_COPY_CLIENT_DATA, ACTION_COPY_SERVER_DATA -> GLFW.GLFW_KEY_I;
            case TOGGLE_SPECTATOR -> GLFW.GLFW_KEY_N;
            case ACTION_OPEN_GAMEMODE_SWITCHER -> GLFW.GLFW_KEY_F4;
            case TOGGLE_PAUSE_ON_LOST_FOCUS -> GLFW.GLFW_KEY_P;
            case ACTION_PRINT_HELP -> GLFW.GLFW_KEY_Q;
            case ACTION_DUMP_TEXTURES -> GLFW.GLFW_KEY_S;
            case ACTION_RELOAD_RESOURCES -> GLFW.GLFW_KEY_T;
            case TOGGLE_DEBUG_PROFILER -> GLFW.GLFW_KEY_L;
            case ACTION_COPY_LOCATION -> GLFW.GLFW_KEY_C;
            case TOGGLE_PROFILER_CHART -> GLFW.GLFW_KEY_1;
            case TOGGLE_FRAME_TIME_CHARTS -> GLFW.GLFW_KEY_2;
            case TOGGLE_NETWORK_CHARTS -> GLFW.GLFW_KEY_3;
            default -> -1;
        };
    }

    @Redirect(
            method = "processF3",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasReducedDebugInfo()Z",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;hasShiftDown()Z"
                    , ordinal = 0
            )
    ) // 163: this.copyLookAt(this.client.player.hasPermissionLevel(2), !Screen.hasShiftDown());
    private boolean rebindmykeys$copyData() {
        return rebindmykeys$action.peek() == OnKeyAction.ACTION_COPY_CLIENT_DATA;
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;",
                            ordinal = 0
                    )
            ),
            constant = @Constant(
                    intValue = GLFW.GLFW_KEY_F1,
                    ordinal = 0
            )
    ) // 433: if (key == GLFW.GLFW_KEY_F1) {
    private int rebindmykeys$hideHud(int constant, long window, int keycode, int scancode, int action, int modifiers) {
        return rebindmykeys$action.peek() == OnKeyAction.TOGGLE_HUD? keycode : keycode + 1;
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;",
                            ordinal = 0
                    )
            ),
            constant = @Constant(
                    intValue = GLFW.GLFW_KEY_0,
                    ordinal = 0
            )
    ) // 436: if (this.client.getDebugHud().shouldShowRenderingChart() && !bl && key >= GLFW.GLFW_KEY_0 &&
    private int rebindmykeys$charts$1(int constant, long window, int keycode, int scancode, int action, int modifiers) {
        return switch (rebindmykeys$action.peek()) {
            case TOGGLE_PROFILER_CHART, TOGGLE_FRAME_TIME_CHARTS, TOGGLE_NETWORK_CHARTS -> keycode;
            default -> keycode + 1;
        };
    }

    @ModifyConstant(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;",
                            ordinal = 0
                    )
            ),
            constant = @Constant(
                    intValue = GLFW.GLFW_KEY_9,
                    ordinal = 0
            )
    ) // 436: && key <= GLFW.GLFW_KEY_9) {
    private int rebindmykeys$charts$2(int constant, long window, int keycode, int scancode, int action, int modifiers) {
        return keycode;
    }

    @ModifyArg(
            method = "onKey",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;handleProfilerKeyPress(I)V",
                    ordinal = 0
            ),
            index = 0
    ) // 437: this.client.handleProfilerKeyPress(key - GLFW.GLFW_KEY_0);
    private int rebindmykeys$chats$3(int digit) {
        return switch(rebindmykeys$action.peek()) {
            case TOGGLE_PROFILER_CHART -> 1;
            case TOGGLE_FRAME_TIME_CHARTS -> 2;
            case TOGGLE_NETWORK_CHARTS -> 3;
            default -> throw new Error("Problem with KeyboardMixin#rebindmykeys$charts$3 or KeyboardMixin#rebindmykeys$charts$1"); // should never be reached
        };
    }

    @ModifyConstant(
            method = "pollDebugCrash",
            constant = @Constant(
                    longValue = 10000L,
                    ordinal = 0
            )
    ) // 488: long m = 10000L - (l - this.debugCrashStartTime);
    private long rebindmykeys$debugCrash$3(long original) {
        return rebindmykeys$debugCrashJavaActive?
                ProfileManager.getCurrentProfile().getGlobal().getDebugCrashJavaTime() :
                ProfileManager.getCurrentProfile().getGlobal().getDebugCrashTime();
    }

    @Redirect(
            method = "pollDebugCrash",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z",
                    ordinal = 0
            )
    ) // 491: if (Screen.hasControlDown()) {
    private boolean rebindmykeys$debugCrashJava() {
        return rebindmykeys$debugCrashJavaActive;
    }
}