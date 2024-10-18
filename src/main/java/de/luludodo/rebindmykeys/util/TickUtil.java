package de.luludodo.rebindmykeys.util;

import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class TickUtil {
    static {
        ClientTickEvents.START_CLIENT_TICK.register(TickUtil::tick);
    }
    private static void tick(MinecraftClient client) {
        scheduled.removeIf(pair -> {
            if (pair.getRight().decrementAndGet() <= 0) {
                pair.getLeft().run();
                return true;
            }
            return false;
        });
        queues.forEach(Queue::tick);
    }

    private static final List<Pair<Action, AtomicInteger>> scheduled = new ArrayList<>();
    public static void schedule(@NotNull Action action, @Range(from = 1, to = Integer.MAX_VALUE) int ticksDelay) {
        scheduled.add(new Pair<>(action, new AtomicInteger(ticksDelay)));
    }
    public static void schedule(@NotNull Action action) {
        schedule(action, 1);
    }

    public static final List<Queue> queues = new ArrayList<>();
    public static void addQueue(@NotNull Queue queue) {
        queues.add(queue);
    }
    public static void removeQueue(@NotNull Queue queue) {
        queues.remove(queue);
    }

    public static QueueEntry entry(Action action) {
        return new QueueEntry(action, new AtomicInteger(0));
    }
    public static QueueEntry entry(Action action, @Range(from = 0, to = Integer.MAX_VALUE) int ticksDelay) {
        return new QueueEntry(action, new AtomicInteger(ticksDelay));
    }
    public static QueueEntry entry(@Range(from = 0, to = Integer.MAX_VALUE) int ticksDelay) {
        return new QueueEntry(null, new AtomicInteger(ticksDelay));
    }

    public interface Queue {
        void tick();
    }
    public record QueueEntry(Action action, AtomicInteger ticksDelay) {
        public QueueEntry copy() {
            return new QueueEntry(action, new AtomicInteger(ticksDelay.get()));
        }
    }
    public static class BasicQueue implements Queue {
        private final List<QueueEntry> queue = new ArrayList<>();

        public void queue(QueueEntry... entries) {
            for (QueueEntry entry : entries) {
                queue.add(entry.copy());
            }
        }

        public void queue(Iterable<QueueEntry> entries) {
            for (QueueEntry entry : entries) {
                queue.add(entry.copy());
            }
        }

        public boolean tickAndGetLeftover(boolean initial) {
            AtomicBoolean tick = new AtomicBoolean(initial);
            queue.removeIf(entry -> {
                AtomicInteger ticksDelay = entry.ticksDelay();
                if (tick.get() && ticksDelay.get() > 0) {
                    tick.set(false);
                    ticksDelay.decrementAndGet();
                }
                if (ticksDelay.get() <= 0) {
                    if (entry.action() != null)
                        entry.action().run();
                    return true;
                }
                return false;
            });
            return tick.get();
        }

        public void tick() {
            tickAndGetLeftover(true);
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }
    }
    public static class ToggleQueue implements Queue {
        private final List<QueueEntry> onToggle;
        private final List<BasicQueue> queued = new ArrayList<>(2);
        private boolean leftOver = true;
        public ToggleQueue(QueueEntry... entries) {
            onToggle = Arrays.asList(entries);
        }

        public void toggle() {
            if (queued.size() == 2) {
                queued.remove(1);
            } else {
                BasicQueue newQueue = new BasicQueue();
                newQueue.queue(onToggle);
                queued.add(newQueue);
            }
            if (leftOver)
                tick();
        }

        public void tick() {
            leftOver = true;

            if (queued.isEmpty())
                return;

            boolean canNext = true;
            while (canNext) {
                if (queued.isEmpty())
                    break;

                leftOver = queued.get(0).tickAndGetLeftover(leftOver);
                canNext = leftOver || queued.get(0).isEmpty();

                if (canNext)
                    queued.remove(0);
            }
        }

        public void removeOnceDone() {
            BasicQueue queue = new BasicQueue();
            queue.queue(entry(() -> removeQueue(this)));
        }
    }
}
