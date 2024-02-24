package de.luludodo.rebindmykeys.util.interfaces;

import com.google.gson.JsonElement;

public interface JsonSavable {
    JsonElement save();
    void load(JsonElement json);
}
