package de.luludodo.rebindmykeys.util;

import com.mojang.datafixers.util.Function3;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
import de.luludodo.rebindmykeys.keybindings.info.KeyBindingInfo;
import de.luludodo.rebindmykeys.keybindings.info.ModInfo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.KeyCombo;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.Key;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.basic.BasicKey;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.modifier.Modifier;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.modifier.ModifierKey;
import de.luludodo.rebindmykeys.keybindings.keyCombo.keys.reference.KeyReference;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.OperationMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.operationModes.action.ActionMode;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.ComboSettings;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.Context;
import de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params.FilterMode;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import de.luludodo.rebindmykeys.util.interfaces.IKeyBinding;
import de.luludodo.rebindmykeys.util.enums.Mouse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class KeyUtil {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    @SuppressWarnings("unused")
    public static class Builder {
        // Binding
        private final String id;
        private final List<KeyCombo> combos = new ArrayList<>();
        private boolean defaultsSet = false;
        private Supplier<OperationMode> defaultOperationMode;
        private Context[] defaultContext;
        private boolean defaultOrderSensitive;
        private FilterMode defaultFilter;
        private int defaultPressCount;
        private Function3<String, List<Key>, ComboSettings, ? extends KeyCombo> defaultConstructor;
        // Combo
        private List<Key> keys = new ArrayList<>();
        private Supplier<OperationMode> operationMode = ActionMode::new;
        private Context[] context = new Context[]{Context.PLAYING};
        private boolean orderSensitive = true;
        private FilterMode filter = FilterMode.ALL;
        private int pressCount = 1;
        private Function3<String, List<Key>, ComboSettings, ? extends KeyCombo> constructor = KeyCombo::new;
        private Builder(String id) {
            this.id = KeyBindingInfo.getKey(id);
        }

        @Contract(" -> this")
        public Builder setDefaults() {
            defaultsSet = true;
            defaultOperationMode = operationMode;
            defaultContext = context;
            defaultOrderSensitive = orderSensitive;
            defaultFilter = filter;
            defaultPressCount = pressCount;
            defaultConstructor = constructor;
            return this;
        }

        @Contract("_ -> this")
        public Builder operationMode(Supplier<OperationMode> operationMode) {
            this.operationMode = operationMode;
            return this;
        }

        @Contract("_ -> this")
        public Builder context(Context... context) {
            this.context = context;
            return this;
        }

        @Contract("_ -> this")
        public Builder orderSensitive(boolean orderSensitive) {
            this.orderSensitive = orderSensitive;
            return this;
        }

        @Contract("_ -> this")
        public Builder filter(FilterMode filter) {
            this.filter = filter;
            return this;
        }

        @Contract("_ -> this")
        public Builder pressCount(int pressCount) {
            if (pressCount <= 0) throw new IllegalArgumentException("pressCount cannot be equal to or smaller than 0");
            this.pressCount = pressCount;
            return this;
        }

        @Contract("_ -> this")
        public Builder constructor(Function3<String, List<Key>, ComboSettings, ? extends KeyCombo> constructor) {
            this.constructor = constructor;
            return this;
        }

        /**
         * Supported A-Z a-z 0-9
         */
        @Contract("_ -> this")
        public Builder keysm(de.luludodo.rebindmykeys.util.enums.Key key) {
            keys.add(new BasicKey(key.getKey()));
            return this;
        }

        @Contract("_ -> this")
        public Builder keysm(int code) {
            keys.add(new BasicKey(InputUtil.Type.KEYSYM.createFromCode(code)));
            return this;
        }

        @Contract("_ -> this")
        public Builder mouse(Mouse mouse) {
            keys.add(new BasicKey(mouse.getKey()));
            return this;
        }

        @Contract("_ -> this")
        public Builder mouse(int button) {
            keys.add(new BasicKey(InputUtil.Type.MOUSE.createFromCode(button)));
            return this;
        }

        @Contract("_ -> this")
        public Builder modifier(Modifier modifier) {
            keys.add(new ModifierKey(modifier));
            return this;
        }

        @Contract("_ -> this")
        public Builder reference(IKeyBinding target) {
            keys.add(new KeyReference(KeyBindingInfo.getKey(target.getId())));
            return this;
        }

        @Contract("_ -> this")
        public Builder reference(String targetId) {
            keys.add(new KeyReference(KeyBindingInfo.getKey(targetId)));
            return this;
        }

        private Action onAction;
        @Contract("_ -> this")
        public Builder onAction(Action action) {
            onAction = action;
            return this;
        }

        private Consumer<Boolean> onToggle;
        @Contract("_ -> this")
        public Builder onToggle(Consumer<Boolean> action) {
            onToggle = action;
            return this;
        }

        @Contract(" -> this")
        public Builder nextCombo() {
            OperationMode mode = operationMode.get();
            mode.setPressCount(pressCount);
            combos.add(constructor.apply(id, keys, new ComboSettings(mode, context, orderSensitive, filter)));
            keys = new ArrayList<>();
            if (defaultsSet) {
                operationMode = defaultOperationMode;
                context = defaultContext;
                orderSensitive = defaultOrderSensitive;
                filter = defaultFilter;
                pressCount = defaultPressCount;
                constructor = defaultConstructor;
            }
            return this;
        }

        public void register() {
            if (!defaultsSet)
                setDefaults();
            if (!keys.isEmpty()) {
                nextCombo();
            }
            OperationMode defaultMode = defaultOperationMode.get();
            defaultMode.setPressCount(defaultPressCount);
            KeyBindingInfo.addId(id);
            InitialKeyBindings.add(new KeyBinding(id, combos, new ComboSettings(defaultMode, defaultContext, defaultOrderSensitive, defaultFilter)));
            boolean isAction = defaultMode instanceof ActionMode;
            if (onAction != null) {
                if (!isAction) // I already know I'm gonna put the wrong one on some KeyBindings so this is going to save me a lot of debugging
                    throw new IllegalArgumentException("onAction set even though KeyBinding isn't an action (id=" + id + ")");
                KeyBindingUtil.onAction(id, onAction);
            }
            if (onToggle != null) {
                if (isAction)
                    throw new IllegalArgumentException("onToggle set even though KeyBinding is an action (id=" + id + ")");
                KeyBindingUtil.onToggle(id, onToggle);
            }
        }
    }

    public static void validate() {
        boolean oneOrMoreInvalid = false;
        //noinspection RedundantIfStatement
        if (CollectionUtil.any(KeyBinding.getAll(), binding -> {
            try {
                binding.updatePressed();
                return false;
            } catch (RuntimeException e) {
                RebindMyKeys.LOG.error("KeyBinding '{}' from mod '{}' is invalid (failed: updatePressed())", binding.getId(), ModInfo.getMod(binding.getId()), e);
                return true;
            }
        })) {
            oneOrMoreInvalid = true;
        }
        if (CollectionUtil.any(KeyBinding.getAll(), binding -> {
            try {
                binding.updateActive();
                return false;
            } catch (RuntimeException e) {
                RebindMyKeys.LOG.error("KeyBinding '{}' from mod '{}' is invalid (failed: updateActive())", binding.getId(), ModInfo.getMod(binding.getId()), e);
                return true;
            }
        })) {
            oneOrMoreInvalid = true;
        }
        if (oneOrMoreInvalid)
            throw new IllegalStateException("One or more KeyBindings are invalid");
    }

    @Contract(value = "_ -> new", pure = true)
    public static Builder create(IKeyBinding bindingEnum) {
        return new Builder(bindingEnum.getId());
    }

    @Contract(value = "_ -> new", pure = true)
    public static Builder create(String id) {
        return new Builder(id);
    }

    /**
     * <h1>Doesn't work for <b>scancodes</b>!</h1>
     */
    @Contract(pure = true)
    public static boolean isPressed(InputUtil.Key key) {
        return switch (key.getCategory()) {
            case KEYSYM -> GLFW.glfwGetKey(CLIENT.getWindow().getHandle(), key.getCode()) != GLFW.GLFW_RELEASE;
            case MOUSE -> GLFW.glfwGetMouseButton(CLIENT.getWindow().getHandle(), key.getCode()) != GLFW.GLFW_RELEASE;
            case SCANCODE -> false; // GLFW doesn't track scancode releases
        };
    }

    private static boolean recording = false;
    private static @Nullable Consumer<List<Key>> whenRecordingDone;
    private static @Nullable Consumer<List<Key>> onRecordingKey;
    private static List<Key> recordedKeys;
    private static List<InputUtil.Key> recordedInputUtilKeys;
    public static void startRecording(@Nullable Consumer<List<Key>> whenDone, @Nullable Consumer<List<Key>> onKey) {
        recording = true;
        whenRecordingDone = whenDone;
        onRecordingKey = onKey;
        recordedKeys = new ArrayList<>();
        recordedInputUtilKeys = new ArrayList<>();
    }

    private static boolean recordingBasicKey = false;
    private static @Nullable Consumer<InputUtil.Key> whenRecordingBasicKeyDone;
    public static void startRecordingBasicKey(@Nullable Consumer<InputUtil.Key> whenDone) {
        recordingBasicKey = true;
        whenRecordingBasicKeyDone = whenDone;
    }

    private static boolean recordingModifierKey = false;
    private static @Nullable Consumer<Modifier> whenRecordingModifierKeyDone;
    public static void startRecordingModifierKey(@Nullable Consumer<Modifier> whenDone) {
        recordingModifierKey = true;
        whenRecordingModifierKeyDone = whenDone;
    }

    public static boolean isRecording() {
        return recording || recordingBasicKey || recordingModifierKey;
    }

    public static void addRecordedKey(InputUtil.Key key) {
        if (recording) {
            addRecordedNormalKey(key);
        }

        if (recordingBasicKey) {
            addRecordedBasicKey(key);
        }

        if (recordingModifierKey) {
            addRecordedModifierKey(key);
        }
    }

    private static void addRecordedNormalKey(InputUtil.Key key) {
        recordedInputUtilKeys.add(key);
        for (Modifier modifier : Modifier.values()) {
            if (ArrayUtil.contains(modifier.getKeys(), key)) {
                recordedKeys.add(new ModifierKey(modifier));

                if (onRecordingKey != null)
                    onRecordingKey.accept(recordedKeys);
                return;
            }
        }
        recordedKeys.add(new BasicKey(key));

        if (onRecordingKey != null)
            onRecordingKey.accept(recordedKeys);
    }

    private static void addRecordedBasicKey(InputUtil.Key key) {
        if (whenRecordingBasicKeyDone != null)
            whenRecordingBasicKeyDone.accept(key);

        recordingBasicKey = false;
        whenRecordingBasicKeyDone = null;
    }

    private static void addRecordedModifierKey(InputUtil.Key key) {
        for (Modifier modifier : Modifier.values()) {
            if (ArrayUtil.contains(modifier.getKeys(), key)) {
                if (whenRecordingModifierKeyDone != null)
                    whenRecordingModifierKeyDone.accept(modifier);

                recordingModifierKey = false;
                whenRecordingModifierKeyDone = null;
                return;
            }
        }
    }

    public static boolean isInRecording(InputUtil.Key key) {
        return recording && recordedInputUtilKeys.contains(key);
    }

    public static void stopRecording() {
        if (recording) {
            recording = false;
            if (whenRecordingDone != null) {
                whenRecordingDone.accept(recordedKeys);
                whenRecordingDone = null;
            }
            onRecordingKey = null;
            recordedKeys = null;
            recordedInputUtilKeys = null;
        }
    }
}
