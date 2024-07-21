package de.luludodo.rebindmykeys.util.interfaces;

import com.google.gson.JsonElement;

public interface JsonConverter<T> {
    T load(JsonElement json);
    JsonElement save(T t);
}
