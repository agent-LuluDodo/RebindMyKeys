package de.luludodo.rebindmykeys.util.enums;

import de.luludodo.rebindmykeys.util.interfaces.IKeyBinding;

public enum KeyBindings implements IKeyBinding {
    LEFT_CLICK("left_click"),
    RIGHT_CLICK("right_click"),
    CLOSE_MENU("close_menu"),
    GRAB_ITEM("grab_item"),
    GRAB_HALF("grab_half"),
    PAUSE_GAME("pause_game"),
    NARRATOR("narrator"),
    HUD("hud"),
    DEBUG_MENU("debug_menu"),
    POST_PROCESSING("post_processing"),
    REFRESH_SERVER_LIST("refresh_server_list"),
    QUICK_MOVE("quick_move"),
    SPRINT("sprint"),
    JUMP("jump"),
    SNEAK("sneak"),
    MOVE_LEFT("move_left"),
    MOVE_RIGHT("move_right"),
    MOVE_BACK("move_back"),
    MOVE_FRONT("move_front"),
    ATTACK("attack"),
    PICK_BLOCK("pick_block"),
    USE("use"),
    DROP("drop"),
    DROP_STACK("drop_stack"),
    HOTBAR_1("hotbar_1"),
    HOTBAR_2("hotbar_2"),
    HOTBAR_3("hotbar_3"),
    HOTBAR_4("hotbar_4"),
    HOTBAR_5("hotbar_5"),
    HOTBAR_6("hotbar_6"),
    HOTBAR_7("hotbar_7"),
    HOTBAR_8("hotbar_8"),
    HOTBAR_9("hotbar_9"),
    INVENTORY("inventory"),
    SWAP_OFFHAND("swap_offhand"),
    LOAD_HOTBAR("load_hotbar"),
    SAVE_HOTBAR("save_hotbar"),
    LIST_PLAYERS("list_players"),
    OPEN_CHAT("open_chat"),
    OPEN_COMMAND("open_command"),
    SOCIAL_INTERACTIONS("social_interactions"),
    ADVANCEMENTS("advancements"),
    SPECTATOR_OUTLINES("spectator_outlines"),
    TAKE_SCREENSHOT("take_screenshot"),
    CINEMATIC_CAMERA("cinematic_camera"),
    FULLSCREEN("fullscreen"),
    PERSPECTIVES("perspectives");

    private final String id;
    KeyBindings(String id) {
       this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
