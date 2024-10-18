package de.luludodo.rebindmykeys.modSupport.operationMode;

public enum OriginalMode {
    UNKNOWN("unknown"),
    ACTION("action"),
    HOLD("hold"),
    TOGGLE("toggle");

    private final String translationKey;
    OriginalMode(String id) {
        translationKey = "rebindmykeys.originalMode." + id;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
