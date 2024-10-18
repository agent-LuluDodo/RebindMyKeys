package de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

public enum Context implements IContext {
    EVERYWHERE,
    IN_GAME(EVERYWHERE),
    IN_SCREEN(EVERYWHERE),
    PLAYING(IN_GAME),
    IN_GUI(IN_GAME, IN_SCREEN),
    IN_INVENTORY(IN_GUI),
    IN_MENU(IN_SCREEN),
    IN_MULTIPLAYER_SCREEN(IN_MENU);

    private final Context[] parents;
    Context(Context... parents) {
        this.parents = parents;
    }

    @Override
    public IContext[] getParents() {
        return parents;
    }

    @Override
    public boolean isCurrent() {
        return Context.isCurrent(this);
    }

    public static boolean isCurrent(Context context) {
        return switch (context) {
            case EVERYWHERE -> true;
            case IN_GAME -> MinecraftClient.getInstance().world != null;
            case IN_SCREEN -> MinecraftClient.getInstance().currentScreen != null;
            case PLAYING -> isCurrent(IN_GAME) && !isCurrent(IN_SCREEN);
            case IN_GUI -> isCurrent(IN_GAME) && isCurrent(IN_SCREEN);
            case IN_INVENTORY -> MinecraftClient.getInstance().currentScreen instanceof InventoryScreen;
            case IN_MENU -> !isCurrent(IN_GAME) && isCurrent(IN_SCREEN);
            case IN_MULTIPLAYER_SCREEN -> MinecraftClient.getInstance().currentScreen instanceof MultiplayerScreen;
        };
    }

    public String getId() {
        return "rebindmykeys.context." + switch (this) {
            case EVERYWHERE -> "everywhere";
            case IN_GAME -> "inGame";
            case IN_SCREEN -> "inScreen";
            case PLAYING -> "playing";
            case IN_GUI -> "inGui";
            case IN_INVENTORY -> "inInventory";
            case IN_MENU -> "inMenu";
            case IN_MULTIPLAYER_SCREEN -> "inMultiplayerScreen";
        };
    }
}
