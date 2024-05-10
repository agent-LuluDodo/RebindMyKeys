package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.mixin.MinecraftClientMixin;
import de.luludodo.rebindmykeys.util.enums.KeyBindings;
import de.luludodo.rebindmykeys.util.enums.Mouse;
import de.luludodo.rebindmykeys.util.enums.OnKeyAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.option.KeyBinding;

public class KeyBindingActions {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static void leftClick() {
        if (KeyBindings.LEFT_CLICK.get("rebindmykeys.key").isPressed()) {
            CLIENT.currentScreen.mouseClicked(Mouse.getX(), Mouse.getY(), Mouse.LEFT.getButton());
        } else {
            CLIENT.currentScreen.mouseReleased(Mouse.getX(), Mouse.getY(), Mouse.LEFT.getButton());
        }
    }

    public static void rightClick() {
        if (KeyBindings.LEFT_CLICK.get("rebindmykeys.key").isPressed()) {
            CLIENT.currentScreen.mouseClicked(Mouse.getX(), Mouse.getY(), Mouse.RIGHT.getButton());
        } else {
            CLIENT.currentScreen.mouseReleased(Mouse.getX(), Mouse.getY(), Mouse.RIGHT.getButton());
        }
    }

    public static void closeMenu() { // FIXME: crash if opening menu then closing it really fast
        //RebindMyKeys.DEBUG.info("Closing Menu");
        CLIENT.currentScreen.close();
    }

    /*
    public static void grabItem(MinecraftClient client) { // TODO: more in-depth implementation
        if (client.currentScreen == null) return;
        RebindMyKeys.DEBUG.info("Grabbed Item");
        client.currentScreen.mouseClicked(Mouse.getX(), Mouse.getY(), Mouse.LEFT.getButton());
    }

    public static void grabHalf(MinecraftClient client) { // TODO: more in-depth implementation
        if (client.currentScreen == null) return;
        RebindMyKeys.DEBUG.info("Grabbed Half of Item");
        client.currentScreen.mouseClicked(Mouse.getX(), Mouse.getY(), Mouse.RIGHT.getButton());
    }
    */

    public static void pauseGame() {
        //RebindMyKeys.DEBUG.info("Pausing Game");
        CLIENT.openGameMenu(KeyBindings.DEBUG_MENU.get("rebindmykeys.key").isPressed());
    }

    public static void narrator() { // TODO: Implement
        // Best bet somehow running minecraft's code which remains in onKey() [Together with debug combos]
    }

    public static void hud(boolean newState) {
        CLIENT.options.hudHidden = newState;
    }

    private static boolean debugMenuState = false;
    public static void debugMenu(boolean newState) { // TODO: Implement debug key combos
        if (debugMenuState != newState) {
            debugMenuState = newState;
            CLIENT.inGameHud.getDebugHud().toggleDebugHud();
        }
    }

    public static void debugCrash(boolean newState) {
        if (newState) {
            OnKeyAction.START_DEBUG_CRASH.trigger();
        } else {
            OnKeyAction.STOP_DEBUG_CRASH.trigger();
        }
    }

    public static void postProcessing(boolean newState) {
        CLIENT.gameRenderer.togglePostProcessorEnabled();
    }

    /**
     * @see de.luludodo.rebindmykeys.mixin.MultiplayerScreenMixin#rebindmykeys$invalidateF5Refresh(int)
     */
    public static void refreshServerList() {
        ((MultiplayerScreen) CLIENT.currentScreen).refresh(); // Context validates all this for us
    }

    public static void quickMove() { // TODO: Implement
        // Idk, prob dig a bit in the code and then do some stuff to make this happen (might need to reimplement item logic, so do this together with all the other modifiers once im done with testing)
    }

    public static void sprint(boolean newState) {
        CLIENT.options.sprintKey.setPressed(newState);
    }

    public static void jump(boolean newState) {
        //RebindMyKeys.DEBUG.info((newState? "Started" : "Stopped") + " jumping");
        CLIENT.options.jumpKey.setPressed(newState);
    }

    public static void sneak(boolean newState) {
        CLIENT.options.sneakKey.setPressed(newState);
    }

    public static void moveLeft(boolean newState) {
        CLIENT.options.leftKey.setPressed(newState);
    }

    public static void moveRight(boolean newState) {
        CLIENT.options.rightKey.setPressed(newState);
    }

    public static void moveBack(boolean newState) {
        CLIENT.options.backKey.setPressed(newState);
    }

    public static void moveFront(boolean newState) {
        //RebindMyKeys.DEBUG.info((newState? "Started" : "Stopped") + " walking forward");
        CLIENT.options.forwardKey.setPressed(newState);
    }

    public static void attack() {
        ++CLIENT.options.attackKey.timesPressed;
    }

    public static void pickBlock() {
        ++CLIENT.options.pickItemKey.timesPressed;
    }

    public static void use(boolean newState) { // TODO: Implement (for splitting once im done with testing)
        CLIENT.options.useKey.setPressed(newState);
    }

    /**
     * Implemented using only Mixins
     * @see MinecraftClientMixin#rebindmykeys$isDropKeyPressed(KeyBinding)
     * @see MinecraftClientMixin#rebindmykeys$shouldDropStack()
     */
    public static void drop(boolean newState) {}

    /**
     * Implemented using only Mixins
     * @see MinecraftClientMixin#rebindmykeys$isDropKeyPressed(KeyBinding)
     * @see MinecraftClientMixin#rebindmykeys$shouldDropStack()
     */
    public static void dropStack(boolean newState) {}

    public static void hotbar(int slot) {
        ++CLIENT.options.hotbarKeys[slot].timesPressed;
    }

    public static void inventory() {
        //RebindMyKeys.DEBUG.info("Opening Inventory");
        CLIENT.options.inventoryKey.timesPressed++;
    }

    public static void swapOffhand() {
        CLIENT.options.swapHandsKey.timesPressed++;
    }

    public static void loadHotbar(boolean newState) {
        CLIENT.options.loadToolbarActivatorKey.setPressed(newState);
    }

    public static void saveHotbar(boolean newState) {
        CLIENT.options.saveToolbarActivatorKey.setPressed(newState);
    }

    public static void listPlayers(boolean newState) {
        CLIENT.options.playerListKey.setPressed(newState);
    }

    public static void openChat() {
        CLIENT.options.chatKey.timesPressed++;
    }

    public static void openCommand() {
        CLIENT.options.commandKey.timesPressed++;
    }

    public static void socialInteractions() {
        CLIENT.options.socialInteractionsKey.timesPressed++;
    }

    public static void advancements() {
        CLIENT.options.advancementsKey.timesPressed++;
    }

    public static void spectatorOutlines() {
        CLIENT.options.spectatorOutlinesKey.timesPressed++;
    }

    public static void takeScreenshot() {
        CLIENT.options.screenshotKey.timesPressed++;
    }

    public static void cinematicCamera(boolean newState) {
        CLIENT.options.smoothCameraKey.setPressed(newState);
    }

    public static void fullscreen(boolean newState) { // TODO: Implement with debug combo

    }

    public static void perspectives() {
        CLIENT.options.togglePerspectiveKey.timesPressed++;
    }
}
