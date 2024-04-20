package de.luludodo.rebindmykeys;

import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.modifier.Modifier;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.HoldMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActionMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActivateOn;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.toggle.ToggleMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.Context;
import de.luludodo.rebindmykeys.util.KeyBindingActions;
import de.luludodo.rebindmykeys.util.KeyBindingUtil;
import de.luludodo.rebindmykeys.util.KeyUtil;
import de.luludodo.rebindmykeys.util.TimerUtil;
import de.luludodo.rebindmykeys.util.enums.Key;
import de.luludodo.rebindmykeys.util.enums.KeyBindings;
import de.luludodo.rebindmykeys.util.enums.Mouse;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RebindMyKeys implements ClientModInitializer {
    public static final Logger DEBUG = LoggerFactory.getLogger("RebindMyKeys DEBUG");
    public static final Logger LOG = LoggerFactory.getLogger("RebindMyKeys");

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) { // ONLY FOR DEBUGGING (getting the stack trace of a gl error)
            ClientTickEvents.START_CLIENT_TICK.register(client ->
                    GLFW.glfwSetErrorCallback((errorCode, description) -> {
                        throw new RuntimeException("GLFW ERROR " + errorCode + ": " + description);
                    })
            );
        }

        KeyUtil.setMod("Minecraft");

        KeyUtil.setCategory("timer");

        KeyUtil.create("timer")
                .context(Context.EVERYWHERE)
                .keysm(Key.Z)
                .onAction(TimerUtil::stop)
                .register();

        KeyUtil.setCategory("essentials");
        // Essentials
        KeyUtil.create(KeyBindings.LEFT_CLICK)
                .context(Context.IN_SCREEN)
                .operationMode(new ActionMode(ActivateOn.BOTH)) // we need press and release
                .mouse(Mouse.LEFT)
                .onAction(KeyBindingActions::leftClick)
                .register();
        KeyUtil.create(KeyBindings.RIGHT_CLICK)
                .context(Context.IN_SCREEN)
                .operationMode(new ActionMode(ActivateOn.BOTH)) // we need press and release
                .mouse(Mouse.RIGHT)
                .onAction(KeyBindingActions::rightClick)
                .register();
        // TODO: scrolling
        KeyUtil.create(KeyBindings.CLOSE_MENU)
                .context(Context.IN_SCREEN)
                .keysm(Key.ESCAPE)
                .onAction(KeyBindingActions::closeMenu)
                .register();

        // Vanilla
        KeyUtil.setCategory("sort-later");

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
        // TODO: Camera controls (eg move camera left)
        // TODO: Hotbar controls (eg next hotbar item)
        KeyUtil.create(KeyBindings.PAUSE_GAME) // close menu is a separate keybind
                .context(Context.PLAYING)
                .keysm(Key.ESCAPE)
                .onAction(KeyBindingActions::pauseGame)
                .register();
        KeyUtil.create(KeyBindings.NARRATOR) // Has like four modes -> cant be a toggle
                .context(Context.EVERYWHERE)
                .keysm(Key.B)
                .modifier(Modifier.CONTROL)
                .onAction(KeyBindingActions::narrator)
                .register();
        KeyUtil.create(KeyBindings.HUD)
                .operationMode(new ToggleMode())
                .context(Context.PLAYING)
                .keysm(Key.F1)
                .onToggle(KeyBindingActions::hud)
                .register();
        KeyUtil.create(KeyBindings.DEBUG_MENU)
                .operationMode(new ToggleMode())
                .context(Context.PLAYING)
                .keysm(Key.F3)
                .onToggle(KeyBindingActions::debugMenu)
                .register();
        // TODO: Add debug combos
        KeyUtil.create(KeyBindings.POST_PROCESSING)
                .operationMode(new ToggleMode())
                .context(Context.PLAYING)
                .keysm(Key.F4)
                .onToggle(KeyBindingActions::postProcessing)
                .register();
        KeyUtil.create(KeyBindings.REFRESH_SERVER_LIST)
                .context(Context.IN_MULTIPLAYER_SCREEN)
                .keysm(Key.F5)
                .onAction(KeyBindingActions::refreshServerList)
                .register();
        KeyUtil.create(KeyBindings.QUICK_MOVE) // TODO: Split in more options
                .context(Context.IN_GUI)
                .modifier(Modifier.SHIFT)
                .onAction(KeyBindingActions::quickMove)
                .register();
        KeyUtil.create(KeyBindings.SPRINT) // TODO: separate swim
                .operationMode(new HoldMode())
                .context(Context.PLAYING)
                .modifier(Modifier.CONTROL) // TODO: Add double tap w
                .onToggle(KeyBindingActions::sprint)
                .register();
        KeyUtil.create(KeyBindings.JUMP) // TODO: separate start flying and fly up and swim up
                .operationMode(new HoldMode()) // You don't stop jumping util you release jump
                .context(Context.PLAYING)
                .keysm(Key.SPACE)
                .onToggle(KeyBindingActions::jump)
                .register();
        KeyUtil.create(KeyBindings.SNEAK) // TODO: separate dismount and fly down and swim down
                .operationMode(new HoldMode())
                .context(Context.PLAYING)
                .modifier(Modifier.SHIFT)
                .onToggle(KeyBindingActions::sneak)
                .register();
        KeyUtil.create(KeyBindings.MOVE_LEFT)
                .operationMode(new HoldMode())
                .context(Context.PLAYING)
                .keysm(Key.A)
                .onToggle(KeyBindingActions::moveLeft)
                .register();
        KeyUtil.create(KeyBindings.MOVE_RIGHT)
                .operationMode(new HoldMode())
                .context(Context.PLAYING)
                .keysm(Key.D)
                .onToggle(KeyBindingActions::moveRight)
                .register();
        KeyUtil.create(KeyBindings.MOVE_BACK)
                .operationMode(new HoldMode())
                .context(Context.PLAYING)
                .keysm(Key.S)
                .onToggle(KeyBindingActions::moveBack)
                .register();
        KeyUtil.create(KeyBindings.MOVE_FRONT)
                .operationMode(new HoldMode())
                .context(Context.PLAYING)
                .keysm(Key.W)
                .onToggle(KeyBindingActions::moveFront)
                .register();
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
                .operationMode(new HoldMode())
                .context(Context.PLAYING)
                .mouse(Mouse.RIGHT)
                .onToggle(KeyBindingActions::use)
                .register();
        KeyUtil.create(KeyBindings.DROP)
                .operationMode(new HoldMode())
                .context(Context.IN_GAME)
                .keysm(Key.Q)
                .onToggle(KeyBindingActions::drop)
                .register();
        KeyUtil.create(KeyBindings.DROP_STACK) // FIXME: fix (doesn't stop pressing only presses sometimes really wierd)
                .operationMode(new HoldMode())
                .orderSensitive(false) // vanilla behaviour
                .context(Context.IN_GAME)
                .reference(KeyBindings.DROP)
                .modifier(Modifier.CONTROL)
                .onToggle(KeyBindingActions::dropStack)
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
        KeyUtil.create(KeyBindings.INVENTORY) // no toggle cause escape also closes it and I want to avoid spaghetti-code but could maybe be done later
                .context(Context.PLAYING, Context.IN_INVENTORY)
                .keysm(Key.E)
                .onAction(KeyBindingActions::inventory)
                .register();
        KeyUtil.create(KeyBindings.SWAP_OFFHAND)
                .context(Context.IN_GAME)
                .keysm(Key.F)
                .onAction(KeyBindingActions::swapOffhand)
                .register();
        KeyUtil.create(KeyBindings.LOAD_HOTBAR) // TODO: custom keybindings for save-slots (currently NUM_0, NUM_1, etc)
                .operationMode(new HoldMode())
                .context(Context.PLAYING)
                .keysm(Key.X)
                .onToggle(KeyBindingActions::loadHotbar)
                .register();
        KeyUtil.create(KeyBindings.SAVE_HOTBAR) // TODO: custom keybindings for save-slots (currently NUM_0, NUM_1, etc)
                .operationMode(new HoldMode())
                .context(Context.PLAYING)
                .keysm(Key.C)
                .onToggle(KeyBindingActions::saveHotbar)
                .register();
        KeyUtil.create(KeyBindings.LIST_PLAYERS)
                .operationMode(new HoldMode())
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
                .onAction(KeyBindingActions::takeScreenshot)
                .register();
        KeyUtil.create(KeyBindings.CINEMATIC_CAMERA)
                .operationMode(new ToggleMode())
                .context(Context.PLAYING)
                .onToggle(KeyBindingActions::cinematicCamera)
                .register();
        KeyUtil.create(KeyBindings.FULLSCREEN)
                .operationMode(new ToggleMode())
                .context(Context.EVERYWHERE)
                .keysm(Key.F12)
                .onToggle(KeyBindingActions::fullscreen)
                .register();
        KeyUtil.create(KeyBindings.PERSPECTIVES)
                .context(Context.PLAYING)
                .keysm(Key.F5)
                .onAction(KeyBindingActions::perspectives)
                .register();


        // FIXME: check if reference + modifier with orderSensitive works correctly

        KeyUtil.setMod("RebindMyKeys");

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
                .register();
    }
}