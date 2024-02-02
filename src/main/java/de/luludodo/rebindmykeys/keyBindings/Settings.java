package de.luludodo.rebindmykeys.keyBindings;

import de.luludodo.rebindmykeys.RebindMyKeys;
import net.minecraft.client.util.InputUtil;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Settings {
    public static class Builder {
        private InputUtil.Key mainKey = InputUtil.UNKNOWN_KEY;
        private Context context = Context.EVERYWHERE;
        private final Set<InputUtil.Key> comboKeys = new HashSet<>(0);
        private Builder() {}

        public void setMainKey(InputUtil.Key mainKey) {
            this.mainKey = mainKey;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void addComboKey(InputUtil.Key keyBinding) {
            comboKeys.add(keyBinding);
        }

        public Settings build() {
            return new Settings(mainKey, context, comboKeys);
        }
    }
    public static Settings.Builder builder() {
        return new Builder();
    }

    private final boolean isDefault;
    private final Settings defaults;
    private InputUtil.Key mainKey;
    private Context context;
    private Collection<InputUtil.Key> comboKeys;
    protected Settings(InputUtil.Key mainkey, Context context, Collection<InputUtil.Key> comboKeys) {
        isDefault = false;
        defaults = new Settings(this);
        this.mainKey = mainkey;
        this.context = context;
        this.comboKeys = comboKeys;
    }
    private Settings(Settings settings) {
        isDefault = true;
        defaults = null;
        mainKey = settings.mainKey;
        context = settings.context;
        comboKeys = Collections.unmodifiableCollection(settings.comboKeys);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        if (isDefault)
            throw new UnsupportedOperationException();
        this.context = context;
    }

    public Collection<InputUtil.Key> getComboKeys() {
        return isDefault? comboKeys : Collections.unmodifiableCollection(comboKeys); // We don't need to double wrap comboKeys
    }

    public void addComboKey(InputUtil.Key comboKey) {
        if (isDefault)
            throw new UnsupportedOperationException();
        comboKeys.add(comboKey);
    }

    public void removeComboKey(InputUtil.Key comboKey) {
        if (isDefault)
            throw new UnsupportedOperationException();
        comboKeys.remove(comboKey);
    }

    protected boolean isDefault() {
        return isDefault;
    }

    public Settings getDefaults() {
        if (isDefault)
            throw new UnsupportedOperationException();
        return defaults;
    }

    public void read(FileReader fileReader) throws IOException {
        BufferedReader reader = new BufferedReader(fileReader);
        int lineNr = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            lineNr++;
            String[] parts = line.split(":", 2);
            try {
                switch (parts[0]) {
                    case "mainKey" -> mainKey = InputUtil.fromTranslationKey(parts[1]);
                    case "context" -> context = Context.valueOf(parts[1]);
                    case "comboKeys" -> {
                        String[] comboKeysStrings = parts[1].split(",");
                        Set<InputUtil.Key> comboKeys = new HashSet<>();
                        for (String comboKeyString : comboKeysStrings) {
                            comboKeys.add(InputUtil.fromTranslationKey(comboKeyString));
                        }
                        this.comboKeys = comboKeys;
                    }
                }
            } catch (IllegalArgumentException e) {
                RebindMyKeys.LOG.warn("Invalid entry for " + this + " at line " + lineNr + " with content '" + line + "'", e);
            }
        }
    }

    public void write(FileWriter fileWriter) throws IOException {
        fileWriter.write("mainKey:" + mainKey.getTranslationKey() + System.lineSeparator());
        fileWriter.write("context:" + context.name() + System.lineSeparator());
        StringBuilder comboKeysString = new StringBuilder("comboKeys:");
        AtomicBoolean first = new AtomicBoolean(true);
        comboKeys.forEach(comboKey -> {
            if (first.get()) {
                first.set(false);
            } else {
                comboKeysString.append(',');
            }
            comboKeysString.append(comboKey.getTranslationKey());
        });
        fileWriter.write(comboKeysString.toString());
        fileWriter.flush();
    }

    public void reset() {
        if (isDefault)
            throw new UnsupportedOperationException();
        mainKey = defaults.mainKey;
        context = defaults.context;
        comboKeys = defaults.comboKeys;
    }
}
