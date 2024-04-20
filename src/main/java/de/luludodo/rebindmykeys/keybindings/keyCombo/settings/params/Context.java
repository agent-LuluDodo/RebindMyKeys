package de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

import java.util.Objects;

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

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static boolean isCurrent(Context context) {
        return switch (context) {
            case EVERYWHERE -> true;
            case IN_GAME -> CLIENT.world != null;
            case IN_SCREEN -> CLIENT.currentScreen != null;
            case PLAYING -> isCurrent(IN_GAME) && !isCurrent(IN_SCREEN);
            case IN_GUI -> isCurrent(IN_GAME) && isCurrent(IN_SCREEN);
            case IN_INVENTORY -> CLIENT.currentScreen instanceof InventoryScreen;
            case IN_MENU -> !isCurrent(IN_GAME) && isCurrent(IN_SCREEN);
            case IN_MULTIPLAYER_SCREEN -> CLIENT.currentScreen instanceof MultiplayerScreen;
        };
    }
}
