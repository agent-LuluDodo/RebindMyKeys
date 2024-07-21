package de.luludodo.rebindmykeys.util;

import com.mojang.datafixers.util.Function3;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.keybindings.KeyBinding;
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
import de.luludodo.rebindmykeys.util.interfaces.Action;
import de.luludodo.rebindmykeys.util.interfaces.IKeyBinding;
import de.luludodo.rebindmykeys.util.enums.Mouse;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KeyUtil {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static ModContainer currentMod = null;
    private static String currentKeyPrefix = "";
    private static String currentCategoryPrefix = "";
    private static String currentCategory = "N/A";
    public static class Builder {
        // Binding
        private final String id;
        private final List<KeyCombo> combos = new ArrayList<>();
        private boolean defaultsSet = false;
        private OperationMode defaultOperationMode;
        private Context[] defaultContext;
        private boolean defaultOrderSensitive;
        private boolean defaultSkipFilter;
        private int defaultPressCount;
        private Function3<String, List<Key>, ComboSettings, ? extends KeyCombo> defaultConstructor;
        // Combo
        private List<Key> keys = new ArrayList<>();
        private OperationMode operationMode = new ActionMode();
        private Context[] context = new Context[]{Context.PLAYING};
        private boolean orderSensitive = true;
        private boolean skipFilter = false;
        private int pressCount = 1;
        private Function3<String, List<Key>, ComboSettings, ? extends KeyCombo> constructor = KeyCombo::new;
        private Builder(String id) {
            this.id = getKey(id);
        }

        @Contract(" -> this")
        public Builder setDefaults() {
            defaultsSet = true;
            defaultOperationMode = operationMode;
            defaultContext = context;
            defaultOrderSensitive = orderSensitive;
            defaultSkipFilter = skipFilter;
            defaultPressCount = pressCount;
            defaultConstructor = constructor;
            return this;
        }

        @Contract("_ -> this")
        public Builder operationMode(OperationMode operationMode) {
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
        public Builder skipFilter(boolean skipFilter) {
            this.skipFilter = skipFilter;
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
            keys.add(new KeyReference(getKey(target.getId())));
            return this;
        }

        @Contract("_ -> this")
        public Builder reference(String targetId) {
            keys.add(new KeyReference(getKey(targetId)));
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
            operationMode.setPressCount(pressCount);
            combos.add(constructor.apply(id, keys, new ComboSettings(operationMode, context, orderSensitive, skipFilter)));
            keys = new ArrayList<>();
            if (defaultsSet) {
                operationMode = defaultOperationMode;
                context = defaultContext;
                orderSensitive = defaultOrderSensitive;
                skipFilter = defaultSkipFilter;
                pressCount = defaultPressCount;
                constructor = defaultConstructor;
            }
            return this;
        }

        public void register() {
            if (!defaultsSet)
                setDefaults();
            if (!keys.isEmpty()) {
                operationMode.setPressCount(pressCount);
                combos.add(new KeyCombo(id, keys, new ComboSettings(operationMode, context, orderSensitive, skipFilter)));
            }
            defaultOperationMode.setPressCount(defaultPressCount);
            KeyUtil.register(new KeyBinding(id, currentMod, combos, new ComboSettings(defaultOperationMode, defaultContext, defaultOrderSensitive, defaultSkipFilter)));
            boolean isAction = operationMode instanceof ActionMode;
            if (onAction != null) {
                if (!isAction) // I already know I'm gonna put the wrong one on some KeyBindings so this is going to save me a lot of debugging
                    throw new IllegalArgumentException("onAction set even though KeyBinding isn't an action");
                KeyBindingUtil.onAction(id, onAction);
                //KeyUtil.registerBindingActionListener(id, client -> onAction.run()); // FIXME: client should be passed as an arg if this works out
            }
            if (onToggle != null) {
                if (isAction)
                    throw new IllegalArgumentException("onToggle set even though KeyBinding is an action");
                KeyBindingUtil.onToggle(id, onToggle);
                //KeyUtil.registerBindingToggleListener(id, (client, newState) -> onToggle.accept(newState)); // FIXME: client should be passed as an arg if this works out
            }
        }
    }

    public static void validate() {
        if (CollectionUtil.oneCondition(getAll(), binding -> {
            try {
                binding.updatePressed();
                return false;
            } catch (RuntimeException e) {
                RebindMyKeys.LOG.error("KeyBinding '" + binding.getId() + "' from mod '" + binding.getModName() + "' is invalid", e);
                return true;
            }
        })) {
            throw new IllegalStateException("One or more KeyBindings are invalid");
        }
        if (CollectionUtil.oneCondition(getAll(), binding -> {
            try {
                binding.updateActive();
                return false;
            } catch (RuntimeException e) {
                RebindMyKeys.LOG.error("KeyBinding '" + binding.getId() + "' from mod '" + binding.getModName() + "' is invalid", e);
                return true;
            }
        })) {
            throw new IllegalStateException("One or more KeyBindings are invalid");
        }
    }

    public static ModContainer toModContainer(String modId) {
        return modId == null? null : FabricLoader.getInstance().getModContainer(modId).orElseThrow(() -> new IllegalArgumentException("No mod with id '" + modId + "' found"));
    }

    public static void setMod(@Nullable String modId, @Nullable String keyPrefix, @Nullable String categoryPrefix) {
        currentMod = toModContainer(modId);
        currentKeyPrefix = processPrefix(keyPrefix);
        currentCategoryPrefix = processPrefix(categoryPrefix);
    }

    @Contract(pure = true)
    public static String processPrefix(String prefix) {
        return (prefix == null || prefix.isBlank())? "" : prefix.strip() + ".";
    }

    public static void setCategory(String category) {
        currentCategory = parseCategory(category);
    }

    @Contract(pure = true)
    private static String parseCategory(String category) {
        category = category.strip();
        if (category.isEmpty())
            throw new IllegalArgumentException("category is empty");
        if (category.charAt(0) == '#') {
            category = category.substring(1).strip();
            if (category.isEmpty())
                throw new IllegalArgumentException("category is empty");
            return category;
        } else {
            return currentCategoryPrefix + category;
        }
    }

    @Contract(pure = true)
    private static String getKey(String key) {
        key = key.strip();
        if (key.isEmpty())
            throw new IllegalArgumentException("key is empty");
        if (key.charAt(0) == '#') {
            key = key.substring(1).strip();
            if (key.isEmpty())
                throw new IllegalArgumentException("key is empty");
            return key;
        } else {
            return currentKeyPrefix + key;
        }
    }

    @Contract(value = "_ -> new", pure = true)
    public static Builder create(IKeyBinding bindingEnum) {
        return new Builder(bindingEnum.getId());
    }

    @Contract(value = "_ -> new", pure = true)
    public static Builder create(String id) {
        return new Builder(id);
    }

    private static final Map<String, KeyBinding> idToBinding = new HashMap<>();
    private static boolean categoryToBindingsSorted = false;
    private static Map<String, List<KeyBinding>> categoryToBindings = new HashMap<>();
    private static final List<String> categoryOrder = new ArrayList<>();
    public static KeyBinding register(KeyBinding binding) {
        if (idToBinding.containsKey(binding.getId()))
            throw new IllegalArgumentException("A KeyBinding with the id '" + binding.getId() + "' already exists.");

        InitialKeyBindings.add(binding);

        idToBinding.put(binding.getId(), binding);
        if (!categoryToBindings.containsKey(currentCategory)) {
            categoryToBindings.put(currentCategory, new ArrayList<>());
            categoryOrder.add(currentCategory);
        }
        categoryToBindings.get(currentCategory).add(binding);
        categoryToBindingsSorted = false;
        return binding;
    }

    @Contract(pure = true)
    public static Map<String, List<KeyBinding>> getCategories() {
        if (!categoryToBindingsSorted) {
            categoryToBindings = MapUtil.sortByKey(categoryToBindings, Comparator.comparingInt(categoryOrder::indexOf));
            categoryToBindings.forEach((category, bindings) -> bindings.sort(
                    (b1, b2) -> Text.translatable(b1.getId()).getString()
                            .compareToIgnoreCase(Text.translatable(b2.getId()).getString())
                    )
            );
            categoryToBindingsSorted = true;
        }
        return Collections.unmodifiableMap(categoryToBindings);
    }

    @Contract(pure = true)
    public static void moveCategoryToTop() {
        categoryOrder.remove(currentCategory);
        categoryOrder.add(0, currentCategory);
        categoryToBindingsSorted = false;
    }

    public static void moveCategoryToBottom() {
        categoryOrder.remove(currentCategory);
        categoryOrder.add(currentCategory);
        categoryToBindingsSorted = false;
    }

    public static void moveCategoryAfter(String target) {
        categoryOrder.remove(currentCategory);
        categoryOrder.add(categoryOrder.indexOf(parseCategory(target)) + 1, currentCategory);
        categoryToBindingsSorted = false;
    }

    public static void moveCategoryBefore(String target) {
        categoryOrder.remove(currentCategory);
        categoryOrder.add(categoryOrder.indexOf(parseCategory(target)), currentCategory);
        categoryToBindingsSorted = false;
    }

    @Contract(pure = true)
    public static KeyBinding get(String id) {
        return idToBinding.get(id);
    }

    @Contract(pure = true)
    public static Collection<KeyBinding> getAll() {
        return Collections.unmodifiableCollection(idToBinding.values());
    }

    private static final Map<String, List<Consumer<MinecraftClient>>> bindingActionListener = new HashMap<>();
    public static void registerBindingActionListener(String id, Consumer<MinecraftClient> action) {
        if (!bindingActionListener.containsKey(id))
            bindingActionListener.put(id, new ArrayList<>(1));
        bindingActionListener.get(id).add(action);
    }

    private static final Map<String, List<BiConsumer<MinecraftClient, Boolean>>> bindingToggleListener = new HashMap<>();
    public static void registerBindingToggleListener(String id, BiConsumer<MinecraftClient, Boolean> action) {
        if (!bindingToggleListener.containsKey(id))
            bindingToggleListener.put(id, new ArrayList<>(1));
        bindingToggleListener.get(id).add(action);
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
    private static Consumer<List<Key>> whenRecordingDone;
    private static List<Key> recordedKeys;
    private static List<InputUtil.Key> recordedInputUtilKeys;
    public static void startRecording(Consumer<List<Key>> whenDone) {
        recording = true;
        whenRecordingDone = whenDone;
        recordedKeys = new ArrayList<>();
        recordedInputUtilKeys = new ArrayList<>();
    }

    public static boolean isRecording() {
        return recording;
    }

    public static void addRecordedKey(InputUtil.Key key) {
        recordedInputUtilKeys.add(key);
        for (Modifier modifier : Modifier.values()) {
            if (ArrayUtil.contains(modifier.getKeys(), key)) {
                recordedKeys.add(new ModifierKey(modifier));
                return;
            }
        }
        recordedKeys.add(new BasicKey(key));
    }

    public static boolean isInRecording(InputUtil.Key key) {
        return recordedInputUtilKeys.contains(key);
    }

    public static void stopRecording() {
        recording = false;
        whenRecordingDone.accept(recordedKeys);
        whenRecordingDone = null;
        recordedKeys = null;
        recordedInputUtilKeys = null;
    }
}
