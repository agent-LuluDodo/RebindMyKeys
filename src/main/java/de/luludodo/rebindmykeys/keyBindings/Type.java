package de.luludodo.rebindmykeys.keyBindings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

public enum Type {
    EVERYWHERE(),
    GAME(EVERYWHERE),
    MOUNTED(GAME),
    UNMOUNTED(GAME),
    MENU(EVERYWHERE),
    MULTIPLAYER_MENU(MENU);

    public boolean currentlyActive() {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen screen = client.currentScreen;
        return switch (this) {
            case EVERYWHERE -> true;
            case GAME -> screen == null;
            case MOUNTED -> screen == null && client.player.hasVehicle();
            case UNMOUNTED -> screen == null && !client.player.hasVehicle();
            case MENU -> screen != null;
            case MULTIPLAYER_MENU -> screen instanceof MultiplayerScreen;
        };
    }

    private final Type parent;
    Type() {
        this.parent = null;
    }
    Type(Type parent) {
        this.parent = parent;
    }

    private boolean isParentOf(Type type) {
        while (type != null) {
            if (type == this) {
                return true;
            }
            type = type.parent;
        }
        return false;
    }

    public boolean conflictsWith(Type other) {
        return isParentOf(other) || other.isParentOf(this);
    }
}
