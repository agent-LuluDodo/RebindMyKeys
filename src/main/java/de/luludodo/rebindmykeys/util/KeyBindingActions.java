package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.mixin.MinecraftClientMixin;
import de.luludodo.rebindmykeys.util.enums.KeyBindings;
import de.luludodo.rebindmykeys.util.enums.Mouse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.option.KeyBinding;

public class KeyBindingActions {
    public static void leftClick(MinecraftClient client) {
        client.currentScreen.mouseClicked(Mouse.getX(), Mouse.getY(), Mouse.LEFT.getButton());
    }

    public static void rightClick(MinecraftClient client) {
        client.currentScreen.mouseClicked(Mouse.getX(), Mouse.getY(), Mouse.RIGHT.getButton());
    }

    public static void closeMenu(MinecraftClient client) {
        RebindMyKeys.DEBUG.info("Closing Menu");
        client.currentScreen.close();
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

    public static void pauseGame(MinecraftClient client) {
        RebindMyKeys.DEBUG.info("Pausing Game");
        client.openGameMenu(KeyBindings.DEBUG_MENU.get().isPressed());
    }

    public static void narrator(MinecraftClient client) { // TODO: Implement
        // Best bet somehow running minecraft's code which remains in onKey() [Together with debug combos]
    }

    public static void hud(MinecraftClient client, boolean newState) {
        client.options.hudHidden = newState;
    }

    public static void debugMenu(MinecraftClient client, boolean newState) { // TODO: Implement debug key combos
        client.inGameHud.getDebugHud().toggleDebugHud();
    }

    public static void postProcessing(MinecraftClient client, boolean newState) {
        client.gameRenderer.togglePostProcessorEnabled();
    }

    /**
     * @see de.luludodo.rebindmykeys.mixin.MultiplayerScreenMixin#rebindmykeys$invalidateF5Refresh(int)
     */
    public static void refreshServerList(MinecraftClient client) { // TODO: Implement
        ((MultiplayerScreen) client.currentScreen).refresh(); // Context validates all this for us
    }

    public static void quickMove(MinecraftClient client) { // TODO: Implement
        // Idk, prob dig a bit in the code and then do some stuff to make this happen (might need to reimplement item logic, so do this together with all the other modifiers once im done with testing)
    }

    public static void sprint(MinecraftClient client, boolean newState) {
        client.options.sprintKey.setPressed(newState);
    }

    public static void jump(MinecraftClient client, boolean newState) {
        RebindMyKeys.DEBUG.info((newState? "Started" : "Stopped") + " jumping");
        client.options.jumpKey.setPressed(newState);
    }

    public static void sneak(MinecraftClient client, boolean newState) {
        client.options.sneakKey.setPressed(newState);
    }

    public static void moveLeft(MinecraftClient client, boolean newState) {
        client.options.leftKey.setPressed(newState);
    }

    public static void moveRight(MinecraftClient client, boolean newState) {
        client.options.rightKey.setPressed(newState);
    }

    public static void moveBack(MinecraftClient client, boolean newState) {
        client.options.backKey.setPressed(newState);
    }

    public static void moveFront(MinecraftClient client, boolean newState) {
        client.options.forwardKey.setPressed(newState);
    }

    public static void attack(MinecraftClient client) {
        ++client.options.attackKey.timesPressed;
    }

    public static void pickBlock(MinecraftClient client) {
        ++client.options.pickItemKey.timesPressed;
    }

    public static void use(MinecraftClient client, boolean newState) { // TODO: Implement (for splitting once im done with testing)

    }

    /**
     * @see MinecraftClientMixin#rebindmykeys$isDropKeyPressed(KeyBinding)
     * @see MinecraftClientMixin#rebindmykeys$shouldDropStack()
     */
    public static void drop(MinecraftClient client, boolean newState) {}

    /**
     * @see MinecraftClientMixin#rebindmykeys$isDropKeyPressed(KeyBinding)
     * @see MinecraftClientMixin#rebindmykeys$shouldDropStack()
     */
    public static void dropStack(MinecraftClient client, boolean newState) {}

    public static void hotbar(MinecraftClient client, int slot) {
        ++client.options.hotbarKeys[slot].timesPressed;
    }

    public static void inventory(MinecraftClient client) {
        RebindMyKeys.DEBUG.info("Opening Inventory");
        client.options.inventoryKey.timesPressed++;
    }

    public static void swapOffhand(MinecraftClient client) {
        client.options.swapHandsKey.timesPressed++;
    }

    public static void loadHotbar(MinecraftClient client, boolean newState) {
        client.options.loadToolbarActivatorKey.setPressed(newState);
    }

    public static void saveHotbar(MinecraftClient client, boolean newState) {
        client.options.saveToolbarActivatorKey.setPressed(newState);
    }

    public static void listPlayers(MinecraftClient client, boolean newState) {
        client.options.playerListKey.setPressed(newState);
    }

    public static void openChat(MinecraftClient client) {
        client.options.chatKey.timesPressed++;
    }

    public static void openCommand(MinecraftClient client) {
        client.options.commandKey.timesPressed++;
    }

    public static void socialInteractions(MinecraftClient client) {
        client.options.socialInteractionsKey.timesPressed++;
    }

    public static void advancements(MinecraftClient client) {
        client.options.advancementsKey.timesPressed++;
    }

    public static void spectatorOutlines(MinecraftClient client) {
        client.options.spectatorOutlinesKey.timesPressed++;
    }

    public static void takeScreenshot(MinecraftClient client) {
        client.options.screenshotKey.timesPressed++;
    }

    public static void cinematicCamera(MinecraftClient client, boolean newState) {
        client.options.smoothCameraKey.setPressed(true);
    }

    public static void fullscreen(MinecraftClient client, boolean newState) {
        client.options.fullscreenKey.timesPressed++;
    }

    public static void perspectives(MinecraftClient client) {
        client.options.togglePerspectiveKey.timesPressed++;
    }
}
