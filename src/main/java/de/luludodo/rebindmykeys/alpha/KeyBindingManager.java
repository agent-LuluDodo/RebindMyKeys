package de.luludodo.rebindmykeys.alpha;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.alpha.keyBindings.AdvancedKeyBinding;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import de.luludodo.rebindmykeys.util.FileUtil;
import de.luludodo.rebindmykeys.util.JsonUtil;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class KeyBindingManager {
    private static final Map<String, AdvancedKeyBinding> keyBindings = new HashMap<>();
    public static void register(AdvancedKeyBinding keyBinding) {
        keyBindings.put(keyBinding.getTranslationKey(), keyBinding);
    }

    public static void init() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("rebindmykeys", "key_bindings");
            }

            @Override
            public void reload(ResourceManager manager) {
                manager.findResources("key_bindings/defaults", path -> path.getPath().endsWith(".json")).forEach((id, resource) -> {

                });
            }
        });
    }

    private static final Path keyBindingPath = FabricLoader.getInstance().getConfigDir().resolve("rebindmykeys/KeyBindings.options");
    public static void load() {
        FileUtil.load(keyBindingPath, JsonUtil.reader(json -> {
            AtomicInteger unloadedKeyBindings = new AtomicInteger(keyBindings.size());
            JsonUtil.toStringMap(json).forEach((key, bindingJson) ->
                    CollectionUtil.ifPresent(keyBindings, key, binding -> {
                        try {
                            binding.load(bindingJson);
                            unloadedKeyBindings.decrementAndGet();
                        } catch (JsonParseException e) {
                            RebindMyKeys.LOG.warn("Couldn't load KeyBinding '" + key + "'", e);
                        }
                    })
            );
            if (unloadedKeyBindings.get() != 0) {
                RebindMyKeys.LOG.warn(unloadedKeyBindings.get() + " KeyBindings missing in file");
            }
        }));
    }

    public static void save() {
        FileUtil.save(keyBindingPath, JsonUtil.writer(() -> {
            JsonObject json = new JsonObject();
            keyBindings.forEach((key, binding) -> json.add(key, binding.save()));
            return json;
        }));
    }
}
