package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TickUtil {
    static {
        ClientTickEvents.START_CLIENT_TICK.register(TickUtil::tick);
    }
    private static void tick(MinecraftClient client) {
        MapUtil.removeAll(scheduled, (action, ticksLeft) -> {
            if (ticksLeft.decrementAndGet() == 0) {
                action.run();
                return true;
            }
            return false;
        });
    }

    private static final Map<Action, AtomicInteger> scheduled = new HashMap<>();
    public static void schedule(@NotNull Action action, @Range(from = 1, to = Integer.MAX_VALUE) int ticksDelay) {
        scheduled.put(action, new AtomicInteger(ticksDelay));
    }
    public static void schedule(@NotNull Action action) {
        schedule(action, 1);
    }
}
