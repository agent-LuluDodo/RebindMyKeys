package de.luludodo.rebindmykeys.util.enums;

import de.luludodo.rebindmykeys.util.interfaces.IKeyBinding;

public enum KeyBindings implements IKeyBinding {
    LEFT_CLICK("leftClick"),
    RIGHT_CLICK("rightClick"),
    CLOSE_MENU("close"),
    GRAB_ITEM("grabItem"),
    GRAB_HALF("grabHalf"),
    PAUSE_GAME("pause"),
    NARRATOR("narrator"),
    HUD("hud"),
    DEBUG_MENU("debug.menu"),
    DEBUG_CRASH("debug.crash"),
    PROFILER_CHART("debug.charts.profiler"),
    FRAME_TIME_CHARTS("debug.charts.frameTime"),
    NETWORK_CHARTS("debug.charts.network"),
    RELOAD_CHUNKS("debug.reloadChunks"),
    HITBOXES("debug.hitboxes"),
    COPY_LOCATION("debug.copyLocation"),
    CLEAR_CHAT("debug.clearChat"),
    CHUNK_BORDERS("debug.chunkBorders"),
    ADVANCED_TOOLTIPS("debug.advancedTooltips"),
    COPY_SERVER_DATA("debug.copyServerData"),
    COPY_CLIENT_DATA("debug.copyClientData"),
    DEBUG_PROFILER("debug.profiler"),
    SPECTATOR("debug.spectator"),
    PAUSE_ON_LOST_FOCUS("debug.pauseOnLostFocus"),
    PRINT_HELP("debug.printHelp"),
    DUMP_TEXTURES("debug.dumpTextures"),
    RELOAD_RESOURCES("debug.reloadResources"),
    OPEN_GAMEMODE_SWITCHER("debug.openGamemodeSwitcher"),
    POST_PROCESSING("postProcessing"),
    REFRESH_SERVER_LIST("refreshServerList"),
    QUICK_MOVE("quickMove"),
    SPRINT("sprint"),
    JUMP("jump"),
    SNEAK("sneak"),
    MOVE_LEFT("left"),
    MOVE_RIGHT("right"),
    MOVE_BACK("back"),
    MOVE_FRONT("forward"),
    ATTACK("attack"),
    PICK_BLOCK("pickItem"),
    USE("use"),
    DROP("drop"),
    DROP_STACK("dropStack"),
    HOTBAR_1("hotbar.1"),
    HOTBAR_2("hotbar.2"),
    HOTBAR_3("hotbar.3"),
    HOTBAR_4("hotbar.4"),
    HOTBAR_5("hotbar.5"),
    HOTBAR_6("hotbar.6"),
    HOTBAR_7("hotbar.7"),
    HOTBAR_8("hotbar.8"),
    HOTBAR_9("hotbar.9"),
    INVENTORY("inventory"),
    SWAP_OFFHAND("swapOffhand"),
    LOAD_HOTBAR("loadToolbarActivator"),
    SAVE_HOTBAR("saveToolbarActivator"),
    LIST_PLAYERS("playerlist"),
    OPEN_CHAT("chat"),
    OPEN_COMMAND("command"),
    SOCIAL_INTERACTIONS("socialInteractions"),
    ADVANCEMENTS("advancements"),
    SPECTATOR_OUTLINES("spectatorOutlines"),
    TAKE_SCREENSHOT("screenshot"),
    CINEMATIC_CAMERA("smoothCamera"),
    FULLSCREEN("fullscreen"),
    PERSPECTIVES("togglePerspective");

    private final String id;
    KeyBindings(String id) {
       this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getVanillaId() {
        return "#key." + getId();
    }

    public String getCustomId() {
        return "#rebindmykeys.key." + getId();
    }
}
