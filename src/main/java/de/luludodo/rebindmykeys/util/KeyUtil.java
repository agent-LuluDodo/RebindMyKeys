package de.luludodo.rebindmykeys.util;

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
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KeyUtil {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static Class<? extends ClientModInitializer> currentMod = null;
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
        // Combo
        private List<Key> keys = new ArrayList<>();
        private OperationMode operationMode = new ActionMode();
        private Context[] context = new Context[]{Context.PLAYING};
        private boolean orderSensitive = true;
        private Builder(String id) {
            this.id = getKey(id);
        }

        public Builder setDefaults() {
            defaultsSet = true;
            defaultOperationMode = operationMode;
            defaultContext = context;
            defaultOrderSensitive = orderSensitive;
            return this;
        }

        public Builder operationMode(OperationMode operationMode) {
            this.operationMode = operationMode;
            return this;
        }

        public Builder context(Context... context) {
            this.context = context;
            return this;
        }

        public Builder orderSensitive(boolean orderSensitive) {
            this.orderSensitive = orderSensitive;
            return this;
        }

        /**
         * Supported A-Z a-z 0-9
         */
        public Builder keysm(de.luludodo.rebindmykeys.util.enums.Key key) {
            keys.add(new BasicKey(key.getKey()));
            return this;
        }

        public Builder keysm(int code) {
            keys.add(new BasicKey(InputUtil.Type.KEYSYM.createFromCode(code)));
            return this;
        }

        public Builder mouse(Mouse mouse) {
            keys.add(new BasicKey(mouse.getKey()));
            return this;
        }

        public Builder mouse(int button) {
            keys.add(new BasicKey(InputUtil.Type.MOUSE.createFromCode(button)));
            return this;
        }

        public Builder modifier(Modifier modifier) {
            keys.add(new ModifierKey(modifier));
            return this;
        }

        public Builder reference(IKeyBinding target) {
            keys.add(new KeyReference(getKey(target.getId())));
            return this;
        }

        public Builder reference(String targetId) {
            keys.add(new KeyReference(getKey(targetId)));
            return this;
        }

        private Action onAction;
        public Builder onAction(Action action) {
            onAction = action;
            return this;
        }

        private Consumer<Boolean> onToggle;
        public Builder onToggle(Consumer<Boolean> action) {
            onToggle = action;
            return this;
        }

        public Builder nextCombo() {
            combos.add(new KeyCombo(id, keys, new ComboSettings(operationMode, context, orderSensitive)));
            keys = new ArrayList<>();
            if (defaultsSet) {
                operationMode = defaultOperationMode;
                context = defaultContext;
                orderSensitive = defaultOrderSensitive;
            }
            return this;
        }

        public void register() {
            if (!defaultsSet)
                setDefaults();
            if (!keys.isEmpty())
                combos.add(new KeyCombo(id, keys, new ComboSettings(operationMode, context, orderSensitive)));
            KeyUtil.register(new KeyBinding(id, currentMod, combos, new ComboSettings(defaultOperationMode, defaultContext, defaultOrderSensitive)));
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
    static {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            validate();
            recalcLengthToCombos();
            KeyBindingUtil.calcIncompatibleUUIDs();
        });
    }

    private static void validate() {
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

    public static void setMod(@Nullable Class<? extends ClientModInitializer> mod, @Nullable String keyPrefix, @Nullable String categoryPrefix) {
        currentMod = mod;
        currentKeyPrefix = processPrefix(keyPrefix);
        currentCategoryPrefix = processPrefix(categoryPrefix);
    }

    public static String processPrefix(String prefix) {
        return (prefix == null || prefix.isBlank())? "" : prefix.strip() + ".";
    }

    public static void setCategory(String category) {
        currentCategory = parseCategory(category);
    }

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

    public static Builder create(IKeyBinding bindingEnum) {
        return new Builder(bindingEnum.getId());
    }

    public static Builder create(String id) {
        return new Builder(id);
    }

    private static final Map<String, KeyBinding> idToBinding = new HashMap<>();
    private static final Map<String, List<KeyBinding>> categoryToBindings = new HashMap<>();
    private static final List<String> categoryOrder = new ArrayList<>();
    private static final Map<Integer, List<KeyCombo>> lengthToCombos = new TreeMap<>(Collections.reverseOrder());
    public static KeyBinding register(KeyBinding binding) {
        if (idToBinding.containsKey(binding.getId()))
            throw new IllegalArgumentException("A KeyBinding with the id '" + binding.getId() + "' already exists.");
        idToBinding.put(binding.getId(), binding);
        if (!categoryToBindings.containsKey(currentCategory)) {
            categoryToBindings.put(currentCategory, new ArrayList<>());
            categoryOrder.add(currentCategory);
        }
        categoryToBindings.get(currentCategory).add(binding);
        return binding;
    }

    public static Map<String, List<KeyBinding>> getCategories() {
        return Collections.unmodifiableMap(MapUtil.sortByKey(categoryToBindings, Comparator.comparingInt(categoryOrder::indexOf)));
    }

    public static void moveCategoryToTop() {
        categoryOrder.remove(currentCategory);
        categoryOrder.add(0, currentCategory);
    }

    public static void moveCategoryToBottom() {
        categoryOrder.remove(currentCategory);
        categoryOrder.add(currentCategory);
    }

    public static void moveCategoryAfter(String target) {
        categoryOrder.remove(currentCategory);
        categoryOrder.add(categoryOrder.indexOf(parseCategory(target)) + 1, currentCategory);
    }

    public static void moveCategoryBefore(String target) {
        categoryOrder.remove(currentCategory);
        categoryOrder.add(categoryOrder.indexOf(parseCategory(target)), currentCategory);
    }

    public static KeyBinding get(String id) {
        return idToBinding.get(id);
    }

    public static Collection<KeyBinding> getAll() {
        return Collections.unmodifiableCollection(idToBinding.values());
    }

    public static Map<Integer, List<KeyCombo>> getCombosByLength() {
        return Collections.unmodifiableMap(lengthToCombos);
    }

    public static void recalcLengthToCombos() {
        lengthToCombos.clear();
        for (KeyBinding binding : idToBinding.values()) {
            for (KeyCombo combo : binding.getKeyCombos()) {
                int length = combo.getLength();
                if (!lengthToCombos.containsKey(length)) {
                    lengthToCombos.put(length, new ArrayList<>());
                }
                lengthToCombos.get(length).add(combo);
            }
        }
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
    public static boolean isPressed(InputUtil.Key key) {
        return switch (key.getCategory()) {
            case KEYSYM -> GLFW.glfwGetKey(CLIENT.getWindow().getHandle(), key.getCode()) != GLFW.GLFW_RELEASE;
            case MOUSE -> GLFW.glfwGetMouseButton(CLIENT.getWindow().getHandle(), key.getCode()) != GLFW.GLFW_RELEASE;
            case SCANCODE -> false; // GLFW doesn't track scancode releases
        };
    }
}
