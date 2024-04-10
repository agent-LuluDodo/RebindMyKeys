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
import de.luludodo.rebindmykeys.util.interfaces.IKeyBinding;
import de.luludodo.rebindmykeys.util.enums.KeyBindings;
import de.luludodo.rebindmykeys.util.enums.Mouse;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KeyUtil {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static String currentMod = "N/A";
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
            this.id = id;
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

        public Builder reference(KeyBindings target) {
            keys.add(new KeyReference(target.getId()));
            return this;
        }

        public Builder reference(String targetId) {
            keys.add(new KeyReference(targetId));
            return this;
        }

        private Consumer<MinecraftClient> onAction;
        public Builder onAction(Consumer<MinecraftClient> action) {
            onAction = action;
            return this;
        }

        private BiConsumer<MinecraftClient, Boolean> onToggle;
        public Builder onToggle(BiConsumer<MinecraftClient, Boolean> action) {
            onToggle = action;
            return this;
        }

        public Builder nextCombo() {
            combos.add(new KeyCombo(keys, new ComboSettings(operationMode, context, orderSensitive)));
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
                combos.add(new KeyCombo(keys, new ComboSettings(operationMode, context, orderSensitive)));
            KeyUtil.register(new KeyBinding(id, combos, new ComboSettings(defaultOperationMode, defaultContext, defaultOrderSensitive)));
            boolean isAction = operationMode instanceof ActionMode;
            if (onAction != null) {
                if (!isAction) // I already know I'm gonna put the wrong one on some KeyBindings so this is going to save me a lot of debugging
                    throw new IllegalArgumentException("onAction set even though KeyBinding isn't an action");
                registerBindingActionListener(id, onAction);
            }
            if (onToggle != null) {
                if (isAction)
                    throw new IllegalArgumentException("onToggle set even though KeyBinding is an action");
                registerBindingToggleListener(id, onToggle);
            }
        }
    }

    static {
        ClientTickEvents.START_CLIENT_TICK.register(KeyUtil::onStartClientTick);
    }

    private static void onStartClientTick(MinecraftClient client) {
        idToBinding.values().forEach(KeyBinding::tick);
        idToBinding.forEach((id, binding) -> {
            if (binding.isAction()) {
                if (bindingActionListener.containsKey(id) && binding.wasPressed()) {
                    bindingActionListener.get(id).forEach(listener -> listener.accept(client));
                }
            } else {
                if (bindingToggleListener.containsKey(id) && binding.stateChanged()) {
                    bindingToggleListener.get(id).forEach(listener -> listener.accept(client, binding.isToggled()));
                }
            }
        });
    }

    public static void setMod(String mod) {
        currentMod = mod;
    }

    public static void setCategory(String category) {
        currentCategory = category;
    }

    public static Builder create(IKeyBinding bindingEnum) {
        return new Builder(bindingEnum.getId());
    }

    public static Builder create(String id) {
        return new Builder(id);
    }

    private static final Map<String, KeyBinding> idToBinding = new HashMap<>();
    public static void register(KeyBinding binding) {
        if (idToBinding.containsKey(binding.getId()))
            throw new IllegalArgumentException("A KeyBinding with the id '" + binding.getId() + "' already exists.");
        idToBinding.put(binding.getId(), binding);
    }

    public static KeyBinding get(String id) {
        return idToBinding.get(id);
    }

    public static Collection<KeyBinding> getAll() {
        return idToBinding.values();
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
