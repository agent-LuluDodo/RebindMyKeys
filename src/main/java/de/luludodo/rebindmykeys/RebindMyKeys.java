package de.luludodo.rebindmykeys;

import de.luludodo.rebindmykeys.keybindings.info.VanillaKeyBindingInfo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.modifier.Modifier;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.hold.HoldMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActionMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActivateOn;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.toggle.ToggleMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.Context;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.FilterMode;
import de.luludodo.rebindmykeys.modSupport.VanillaKeyBindingHelper;
import de.luludodo.rebindmykeys.profiles.ProfileManager;
import de.luludodo.rebindmykeys.util.*;
import de.luludodo.rebindmykeys.util.enums.Key;
import de.luludodo.rebindmykeys.util.enums.KeyBindings;
import de.luludodo.rebindmykeys.util.enums.Mouse;
import de.luludodo.rebindmykeys.util.enums.OnKeyAction;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.luludodo.rebindmykeys.keybindings.info.KeyBindingInfo.*;

@Environment(EnvType.CLIENT)
public class RebindMyKeys implements ClientModInitializer {
    public static final Logger DEBUG = LoggerFactory.getLogger("RebindMyKeys DEBUG");
    public static final Logger LOG = LoggerFactory.getLogger("RebindMyKeys");

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) { // ONLY FOR DEBUGGING (getting the stack trace of a gl error)
            GLFWErrorCallbackI myCallback = (errorCode, description) -> RebindMyKeys.LOG.error("GLFW ERROR {}: {}", errorCode, description, new Throwable("StackTrace"));
            ClientTickEvents.START_CLIENT_TICK.register(client -> {
                GLFWErrorCallback errorCallback = GLFW.glfwSetErrorCallback(myCallback);
                if (errorCallback != null && errorCallback.address() != myCallback.address()) {
                    errorCallback.free();
                }
            });
        }

        FabricUtil.init();

        setMod("minecraft", "key", "key.categories");

        setCategory("movement");

        //noinspection NoTranslation
        KeyBinding vanillaTest = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "rebindmykeys.key.vanillaTest",
                GLFW.GLFW_KEY_I,
                "rebindmykeys.key.categories.vanillaTest"
        ));

        KeyUtil.create(KeyBindings.JUMP) // TODO: separate start flying and fly up and swim up
                .operationMode(HoldMode::new) // You don't stop jumping util you release jump
                .context(Context.PLAYING)
                .keysm(Key.SPACE)
                .onToggle(KeyBindingActions::jump)
                .register();
        KeyUtil.create(KeyBindings.SNEAK) // TODO: separate dismount and fly down and swim down
                .operationMode(HoldMode::new)
                .context(Context.PLAYING)
                .modifier(Modifier.SHIFT)
                .onToggle(KeyBindingActions::sneak)
                .register();
        KeyUtil.create(KeyBindings.SPRINT) // TODO: separate swim
                .operationMode(HoldMode::new)
                .context(Context.PLAYING)
                .modifier(Modifier.CONTROL)
                .onToggle(KeyBindingActions::sprint)
                .setDefaults()
                .nextCombo()
                .filter(FilterMode.SELF)
                .pressCount(2)
                .reference(KeyBindings.MOVE_FRONT) // FIXME: double tapping w stops walking and toggles if sprinting also toggles (should be line vanilla)
                .register();
        KeyUtil.create(KeyBindings.MOVE_LEFT)
                .operationMode(HoldMode::new)
                .context(Context.PLAYING)
                .keysm(Key.A)
                .onToggle(KeyBindingActions::moveLeft)
                .register();
        KeyUtil.create(KeyBindings.MOVE_RIGHT)
                .operationMode(HoldMode::new)
                .context(Context.PLAYING)
                .keysm(Key.D)
                .onToggle(KeyBindingActions::moveRight)
                .register();
        KeyUtil.create(KeyBindings.MOVE_BACK)
                .operationMode(HoldMode::new)
                .context(Context.PLAYING)
                .keysm(Key.S)
                .onToggle(KeyBindingActions::moveBack)
                .register();
        KeyUtil.create(KeyBindings.MOVE_FRONT)
                .operationMode(HoldMode::new)
                .context(Context.PLAYING)
                .keysm(Key.W)
                .onToggle(KeyBindingActions::moveFront)
                .register();

        setCategory("gameplay");

        KeyUtil.create(KeyBindings.ATTACK) // TODO: separate destroy
                .context(Context.PLAYING)
                .mouse(Mouse.LEFT)
                .onAction(KeyBindingActions::attack)
                .register();
        KeyUtil.create(KeyBindings.PICK_BLOCK) // TODO: separate destroy
                .context(Context.PLAYING)
                .mouse(Mouse.MIDDLE)
                .onAction(KeyBindingActions::pickBlock)
                .register();
        KeyUtil.create(KeyBindings.USE) // TODO: separate place
                .operationMode(HoldMode::new)
                .context(Context.PLAYING)
                .mouse(Mouse.RIGHT)
                .onToggle(KeyBindingActions::use)
                .register();

        setCategory("inventory");

        KeyUtil.create(KeyBindings.DROP)
                .operationMode(HoldMode::new)
                .context(Context.IN_GAME)
                .keysm(Key.Q)
                .onToggle(KeyBindingActions::drop)
                .register();
        KeyUtil.create(KeyBindings.HOTBAR_1) // TODO: separate swap item
                .context(Context.IN_GAME)
                .keysm(Key.KEY_1)
                .onAction(() -> KeyBindingActions.hotbar(0))
                .register();
        KeyUtil.create(KeyBindings.HOTBAR_2)
                .context(Context.IN_GAME)
                .keysm(Key.KEY_2)
                .onAction(() -> KeyBindingActions.hotbar(1))
                .register();
        KeyUtil.create(KeyBindings.HOTBAR_3)
                .context(Context.IN_GAME)
                .keysm(Key.KEY_3)
                .onAction(() -> KeyBindingActions.hotbar(2))
                .register();
        KeyUtil.create(KeyBindings.HOTBAR_4)
                .context(Context.IN_GAME)
                .keysm(Key.KEY_4)
                .onAction(() -> KeyBindingActions.hotbar(3))
                .register();
        KeyUtil.create(KeyBindings.HOTBAR_5)
                .context(Context.IN_GAME)
                .keysm(Key.KEY_5)
                .onAction(() -> KeyBindingActions.hotbar(4))
                .register();
        KeyUtil.create(KeyBindings.HOTBAR_6)
                .context(Context.IN_GAME)
                .keysm(Key.KEY_6)
                .onAction(() -> KeyBindingActions.hotbar(5))
                .register();
        KeyUtil.create(KeyBindings.HOTBAR_7)
                .context(Context.IN_GAME)
                .keysm(Key.KEY_7)
                .onAction(() -> KeyBindingActions.hotbar(6))
                .register();
        KeyUtil.create(KeyBindings.HOTBAR_8)
                .context(Context.IN_GAME)
                .keysm(Key.KEY_8)
                .onAction(() -> KeyBindingActions.hotbar(7))
                .register();
        KeyUtil.create(KeyBindings.HOTBAR_9)
                .context(Context.IN_GAME)
                .keysm(Key.KEY_9)
                .onAction(() -> KeyBindingActions.hotbar(8))
                .register();
        KeyUtil.create(KeyBindings.INVENTORY) // no toggle cause escape also closes it (hold would also make no sense)
                .context(Context.PLAYING, Context.IN_INVENTORY)
                .keysm(Key.E)
                .onAction(KeyBindingActions::inventory)
                .register();
        KeyUtil.create(KeyBindings.SWAP_OFFHAND)
                .context(Context.IN_GAME)
                .keysm(Key.F)
                .onAction(KeyBindingActions::swapOffhand)
                .register();

        setCategory("creative");

        KeyUtil.create(KeyBindings.LOAD_HOTBAR)
                .operationMode(HoldMode::new)
                .context(Context.PLAYING)
                .keysm(Key.X)
                .onToggle(KeyBindingActions::loadHotbar)
                .register();
        KeyUtil.create(KeyBindings.SAVE_HOTBAR)
                .operationMode(HoldMode::new)
                .context(Context.PLAYING)
                .keysm(Key.C)
                .onToggle(KeyBindingActions::saveHotbar)
                .register();

        setCategory("multiplayer");

        KeyUtil.create(KeyBindings.LIST_PLAYERS)
                .operationMode(HoldMode::new)
                .context(Context.IN_GAME)
                .keysm(Key.TAB)
                .onToggle(KeyBindingActions::listPlayers)
                .register();
        KeyUtil.create(KeyBindings.OPEN_CHAT)
                .context(Context.PLAYING)
                .keysm(Key.T)
                .onAction(KeyBindingActions::openChat)
                .register();
        KeyUtil.create(KeyBindings.OPEN_COMMAND)
                .context(Context.PLAYING)
                .keysm(Key.SLASH)
                .onAction(KeyBindingActions::openCommand)
                .register();
        KeyUtil.create(KeyBindings.SOCIAL_INTERACTIONS)
                .context(Context.PLAYING)
                .keysm(Key.P)
                .onAction(KeyBindingActions::socialInteractions)
                .register();

        setCategory("misc");

        KeyUtil.create(KeyBindings.ADVANCEMENTS)
                .context(Context.PLAYING)
                .keysm(Key.L)
                .onAction(KeyBindingActions::advancements)
                .register();
        KeyUtil.create(KeyBindings.SPECTATOR_OUTLINES)
                .context(Context.PLAYING)
                .onAction(KeyBindingActions::spectatorOutlines)
                .register();
        KeyUtil.create(KeyBindings.TAKE_SCREENSHOT)
                .context(Context.EVERYWHERE)
                .keysm(Key.F2)
                .onAction(OnKeyAction.ACTION_SCREENSHOT.action())
                .register();
        KeyUtil.create(KeyBindings.CINEMATIC_CAMERA)
                .operationMode(ToggleMode::new)
                .context(Context.PLAYING)
                .onToggle(KeyBindingActions::cinematicCamera)
                .register();
        KeyUtil.create(KeyBindings.FULLSCREEN)
                .operationMode(ToggleMode::new)
                .context(Context.EVERYWHERE)
                .keysm(Key.F11)
                .onToggle(OnKeyAction.TOGGLE_FULLSCREEN.toggle())
                .register();
        KeyUtil.create(KeyBindings.PERSPECTIVES)
                .context(Context.PLAYING)
                .keysm(Key.F5)
                .onAction(KeyBindingActions::perspectives)
                .register();


        setMod("rebindmykeys", "rebindmykeys.key", "rebindmykeys.key.categories");

        setCategory("timer");

        KeyUtil.create("timer")
                .context(Context.EVERYWHERE)
                .keysm(Key.Z)
                .onAction(TimerUtil::stop)
                .register();

        setCategory("essentials");
        setIndex(-1);

        KeyUtil.create(KeyBindings.LEFT_CLICK)
                .filter(FilterMode.OTHERS)
                .context(Context.IN_SCREEN)
                .operationMode(() -> new ActionMode(ActivateOn.BOTH)) // we need press and release TODO: system to lock settings, so that you cannot accidentally change this and break left click :)
                .mouse(Mouse.LEFT)
                .onAction(KeyBindingActions::leftClick)
                .register();
        KeyUtil.create(KeyBindings.RIGHT_CLICK)
                .filter(FilterMode.OTHERS)
                .context(Context.IN_SCREEN)
                .operationMode(() -> new ActionMode(ActivateOn.BOTH)) // we need press and release
                .mouse(Mouse.RIGHT)
                .onAction(KeyBindingActions::rightClick)
                .register();
        // TODO: scrolling
        KeyUtil.create(KeyBindings.PAUSE_GAME) // close menu is a separate keybind
                .filter(FilterMode.OTHERS)
                .context(Context.PLAYING)
                .keysm(Key.ESCAPE)
                .onAction(OnKeyAction.ACTION_PAUSE.action())
                .register();
        KeyUtil.create(KeyBindings.CLOSE_MENU)
                .filter(FilterMode.OTHERS)
                .context(Context.IN_SCREEN)
                .keysm(Key.ESCAPE)
                .onAction(KeyBindingActions::closeMenu)
                .register();

        setCategory("camera");
        moveAfter("#key.categories.gameplay");

        // TODO: Camera controls (eg move camera left)

        setCategory("#key.categories.inventory");

        KeyUtil.create(KeyBindings.DROP_STACK) // FIXME: fix (doesn't stop pressing only presses sometimes really wierd)
                .operationMode(HoldMode::new)
                .orderSensitive(false) // vanilla behaviour
                .context(Context.IN_GAME)
                .reference(KeyBindings.DROP.getVanillaId())
                .modifier(Modifier.CONTROL)
                .onToggle(KeyBindingActions::dropStack)
                .register();
        KeyUtil.create(KeyBindings.QUICK_MOVE) // TODO: Split in more options
                .context(Context.IN_GUI)
                .modifier(Modifier.SHIFT)
                .onAction(KeyBindingActions::quickMove)
                .register();
        /*
        KeyUtil.create(KeyBindings.GRAB_ITEM)
                .context(Context.IN_GUI)
                .mouse(Mouse.LEFT)
                .onAction(KeyBindingActions::grabItem)
                .register();
        KeyUtil.create(KeyBindings.GRAB_HALF)
                .context(Context.IN_GUI)
                .mouse(Mouse.RIGHT)
                .onAction(KeyBindingActions::grabHalf)
                .register();
         */
        // TODO: Hotbar controls (eg next hotbar item)

        setCategory("#key.categories.creative");

        // TODO: custom keybindings for save-slots (currently NUM_0, NUM_1, etc)

        setCategory("#key.categories.misc");

        KeyUtil.create(KeyBindings.DEBUG_MENU)
                .operationMode(() -> new ToggleMode(false, false)) // toggle on release
                .filter(FilterMode.OTHERS)
                .context(Context.PLAYING)
                .keysm(Key.F3)
                .onToggle(OnKeyAction.TOGGLE_DEBUG_HUD.toggleTrigger())
                .register();
        KeyUtil.create(KeyBindings.NARRATOR) // Has like four modes -> cant be a toggle
                .context(Context.EVERYWHERE)
                .keysm(Key.B)
                .modifier(Modifier.CONTROL)
                .onAction(OnKeyAction.ACTION_CYCLE_NARRATOR.action())
                .register();
        KeyUtil.create(KeyBindings.HUD)
                .operationMode(ToggleMode::new)
                .context(Context.PLAYING)
                .keysm(Key.F1)
                .onToggle(OnKeyAction.TOGGLE_HUD.toggle())
                .register();
        KeyUtil.create(KeyBindings.POST_PROCESSING)
                .operationMode(ToggleMode::new)
                .context(Context.PLAYING)
                .keysm(Key.F4)
                .onToggle(OnKeyAction.TOGGLE_POST_PROCESSOR.toggle())
                .register();
        KeyUtil.create(KeyBindings.REFRESH_SERVER_LIST)
                .context(Context.IN_MULTIPLAYER_SCREEN)
                .keysm(Key.F5)
                .onAction(KeyBindingActions::refreshServerList)
                .register();

        setCategory("debugCharts");

        KeyUtil.create(KeyBindings.PROFILER_CHART)
                .operationMode(ToggleMode::new)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.KEY_1)
                .onToggle(OnKeyAction.TOGGLE_PROFILER_CHART.toggle())
                .register();
        KeyUtil.create(KeyBindings.FRAME_TIME_CHARTS)
                .operationMode(ToggleMode::new)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.KEY_2)
                .onToggle(OnKeyAction.TOGGLE_FRAME_TIME_CHARTS.toggle())
                .register();
        KeyUtil.create(KeyBindings.NETWORK_CHARTS)
                .operationMode(ToggleMode::new)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.KEY_3)
                .onToggle(OnKeyAction.TOGGLE_NETWORK_CHARTS.toggle())
                .register();

        setCategory("debugCombos");

        KeyUtil.create(KeyBindings.PAUSE_WITHOUT_MENU)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .reference(KeyBindings.PAUSE_GAME)
                .onAction(OnKeyAction.ACTION_PAUSE_WITHOUT_MENU.action())
                .register();
        KeyUtil.create(KeyBindings.DEBUG_CRASH)
                .operationMode(HoldMode::new)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.C)
                .onToggle(KeyBindingActions::debugCrash)
                .register();
        KeyUtil.create(KeyBindings.DEBUG_CRASH_JAVA)
                .operationMode(HoldMode::new)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_CRASH)
                .modifier(Modifier.CONTROL)
                .onToggle(KeyBindingActions::debugCrashJava)
                .register();
        KeyUtil.create(KeyBindings.RELOAD_CHUNKS)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.A)
                .onAction(OnKeyAction.ACTION_RELOAD_CHUNKS.action())
                .register();
        KeyUtil.create(KeyBindings.HITBOXES)
                .operationMode(ToggleMode::new)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.B)
                .onToggle(OnKeyAction.TOGGLE_HITBOXES.toggle())
                .register();
        KeyUtil.create(KeyBindings.COPY_LOCATION)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.C)
                .onAction(OnKeyAction.ACTION_COPY_LOCATION.action())
                .register();
        KeyUtil.create(KeyBindings.CLEAR_CHAT)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.D)
                .onAction(OnKeyAction.ACTION_CLEAR_CHAT.action())
                .register();
        KeyUtil.create(KeyBindings.CHUNK_BORDERS)
                .operationMode(ToggleMode::new)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.G)
                .onToggle(OnKeyAction.TOGGLE_CHUNK_BORDERS.toggle())
                .register();
        KeyUtil.create(KeyBindings.ADVANCED_TOOLTIPS)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.H)
                .onAction(OnKeyAction.TOGGLE_ADVANCED_TOOLTIPS.action())
                .register();
        KeyUtil.create(KeyBindings.COPY_SERVER_DATA)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.I)
                .onAction(OnKeyAction.ACTION_COPY_SERVER_DATA.action())
                .register();
        KeyUtil.create(KeyBindings.COPY_CLIENT_DATA)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .modifier(Modifier.SHIFT)
                .keysm(Key.I)
                .onAction(OnKeyAction.ACTION_COPY_CLIENT_DATA.action())
                .register();
        KeyUtil.create(KeyBindings.DEBUG_PROFILER)
                .operationMode(ToggleMode::new)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.L)
                .onToggle(OnKeyAction.TOGGLE_DEBUG_PROFILER.toggle())
                .register();
        KeyUtil.create(KeyBindings.SPECTATOR)
                .operationMode(ToggleMode::new)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.N)
                .onToggle(OnKeyAction.TOGGLE_SPECTATOR.toggle())
                .register();
        KeyUtil.create(KeyBindings.PAUSE_ON_LOST_FOCUS)
                .operationMode(ToggleMode::new)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.P)
                .onToggle(OnKeyAction.TOGGLE_PAUSE_ON_LOST_FOCUS.toggle())
                .register();
        KeyUtil.create(KeyBindings.PRINT_HELP)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.Q)
                .onAction(OnKeyAction.ACTION_PRINT_HELP.action())
                .register();
        KeyUtil.create(KeyBindings.DUMP_TEXTURES)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.S)
                .onAction(OnKeyAction.ACTION_DUMP_TEXTURES.action())
                .register();
        KeyUtil.create(KeyBindings.RELOAD_RESOURCES)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.T)
                .onAction(OnKeyAction.ACTION_RELOAD_RESOURCES.action())
                .register();
        KeyUtil.create(KeyBindings.OPEN_GAMEMODE_SWITCHER)
                .context(Context.PLAYING)
                .reference(KeyBindings.DEBUG_MENU)
                .keysm(Key.F4)
                .onAction(OnKeyAction.ACTION_OPEN_GAMEMODE_SWITCHER.action())
                .register();

        setCategory("timer");
        setIndex(100);


        // FIXME: check if reference + modifier with orderSensitive works correctly
/*
        KeyUtil.setCategory("mode-tests");
        // Mode Tests (work)
        KeyUtil.create("action-test")
                .operationMode(new ActionMode())
                .keysm(Key.A)
                .onAction(() -> DEBUG.info("action-test pressed"))
                .register();
        KeyUtil.create("toggle-test")
                .operationMode(new ToggleMode())
                .keysm(Key.T)
                .onToggle(newState -> DEBUG.info("toggle-test toggled to " + newState))
                .register();
        KeyUtil.create("hold-test")
                .operationMode(new HoldMode())
                .keysm(Key.H)
                .onToggle(newState -> DEBUG.info("hold-test toggled to " + newState))
                .register();

        // Keysm and Mouse Tests (mouse not implemented)
        KeyUtil.create("keysm-test") // debug
                .context(Context.EVERYWHERE)
                .keysm(Key.K)
                .onAction(() -> {
                    DEBUG.info("Jump key " + (MinecraftClient.getInstance().options.jumpKey.isPressed()? "pressed" : "released"));
                })
                .register();
        KeyUtil.create("mouse-test")
                .mouse(Mouse.MIDDLE)
                .onAction(() -> DEBUG.info("mouse-test success"))
                .register();

        // Modifier Tests (bugged after pressing both sides)
        KeyUtil.create("shift-test")
                .modifier(Modifier.SHIFT)
                .onAction(() -> DEBUG.info("shift-test success"))
                .register();
        KeyUtil.create("alt-test")
                .modifier(Modifier.ALT)
                .onAction(() -> DEBUG.info("alt-test success"))
                .register();
        KeyUtil.create("control-test")
                .modifier(Modifier.CONTROL)
                .onAction(() -> DEBUG.info("control-test success"))
                .register();

        // Reference Test (only triggers on second press)
        KeyUtil.create("reference-test-target")
                .keysm(Key.R)
                .onAction(() -> DEBUG.info("reference-test-target active"))
                .register();
        KeyUtil.create("reference-test")
                .reference("reference-test-target")
                .onAction(() -> DEBUG.info("reference-test active"))
                .register();
        KeyUtil.create("reference-future-test")
                .reference("reference-future-test-target")
                .onAction(() -> DEBUG.info("reference-future-test active"))
                .register();
        KeyUtil.create("reference-future-test-target")
                .keysm(Key.F)
                .onAction(() -> DEBUG.info("reference-future-test-target active"))
                .register();

        // Order Sensitivity Test (works)
        KeyUtil.create("order-insensitive-test")
                .orderSensitive(false)
                .keysm(Key.O)
                .onAction(() -> DEBUG.info("order-insensitive-test success"))
                .register();

        // Context Test (context not implemented)
        KeyUtil.create("in-menu-test")
                .context(Context.IN_MENU)
                .keysm(Key.I)
                .onAction(() -> DEBUG.info("in-menu-test success"))
                .register();

        // Multi Combo Tests
        KeyUtil.create("multi-combo-test")
                .keysm(Key.M)
                .keysm(Key.NUM_1)
                .nextCombo()
                .keysm(Key.M)
                .keysm(Key.NUM_2)
                .onAction(() -> DEBUG.info("multi-combo-test success"))
                .register();
        KeyUtil.create("toggle-hold-test")
                .operationMode(new ToggleMode())
                .setDefaults()
                .keysm(Key.M)
                .keysm(Key.NUM_3)
                .nextCombo()
                .operationMode(new HoldMode())
                .keysm(Key.M)
                .keysm(Key.NUM_4)
                .onToggle(newState -> DEBUG.info("toggle-hold-test toggled to " + newState))
                .register();

        // Multi Combo Tests Order Insensitive
        KeyUtil.create("order-insensitive-multi-combo-test")
                .orderSensitive(false)
                .keysm(Key.O)
                .keysm(Key.NUM_1)
                .nextCombo()
                .keysm(Key.O)
                .keysm(Key.NUM_2)
                .onAction(() -> DEBUG.info("order-insensitive-multi-combo-test success"))
                .register();
        KeyUtil.create("order-insensitive-toggle-hold-test")
                .orderSensitive(false)
                .operationMode(new ToggleMode())
                .setDefaults()
                .keysm(Key.O)
                .keysm(Key.NUM_3)
                .nextCombo()
                .operationMode(new HoldMode())
                .keysm(Key.O)
                .keysm(Key.NUM_4)
                .onToggle(newState -> DEBUG.info("order-insensitive-toggle-hold-test toggled to " + newState))
                .register();*/

        ClientLifecycleEvents.CLIENT_STARTED.register(this::onClientStarted);
        ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStopping);
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            while (vanillaTest.wasPressed()) {
                RebindMyKeys.DEBUG.info("Vanilla test pressed!");
            }
            if (vanillaTest.isPressed()) {
                RebindMyKeys.DEBUG.info("Vanilla test toggled!");
            }
        });
    }

    @SuppressWarnings("unused")
    public void onClientStarted(MinecraftClient client) {
        VanillaKeyBindingHelper.init();
        VanillaKeyBindingInfo.printCountInfo();

        ProfileManager.init();

        KeyUtil.validate();
        KeyBindingUtil.calcIncompatibleUUIDs();
    }

    @SuppressWarnings("unused")
    public void onClientStopping(MinecraftClient client) {
        ProfileManager.save();
    }
}