package jp.mincra.bktween;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * BKTween(BukkitTween).
 * This example print "Bar" 20 ticks after print "Foo"
 * tween.execute(() -> print("Foo"))
 *      .delay(TickTime.TICK, 20)
 *      .execute(() -> print("Bar"))
 *      .run();
 */
public class BKTween {
    private Consumer<Void> tmpFunction;
    private final Queue<TweenTask> tasks;
    private final Plugin plugin;

    public BKTween(Plugin plugin) {
        tasks = new ArrayDeque<>();
        this.plugin = plugin;
    }

    public BKTween execute(Consumer<Void> func) {
        if (tmpFunction == null) {
            tmpFunction = func;
        } else {
            tmpFunction = tmpFunction.andThen(func);
        }
        return this;
    }

    public BKTween delay(TickTime tickTime, long delay) {
        tasks.add(new TweenTask(tmpFunction,
                tickTime.getMultiplier() * delay, 0, 0));
        tmpFunction = null;
        return this;
    }

    public BKTween repeat(TickTime tickTime, long interval, long delay, int attempts) {
        int multi = tickTime.getMultiplier();
        tasks.add(new TweenTask(tmpFunction,
                multi * delay,
                multi * interval,
                attempts));
        tmpFunction = null;
        return this;
    }

    public void run() {
        if (tmpFunction != null) {
            tasks.add(new TweenTask(tmpFunction,
                    0, 0, 0));
            tmpFunction = null;
        }

        TweenTask task = tasks.poll();
        if (task == null)  {
            return;
        }

        BukkitScheduler scheduler = Bukkit.getScheduler();
        long interval = task.interval();
        if (interval == 0) {
            scheduler.runTaskLater(plugin, () -> {
                var func = task.func();
                if (func != null) func.accept(null);
                run();
            }, task.delay());
        } else {
            AtomicInteger currentIteration = new AtomicInteger(0);
            AtomicInteger processId = new AtomicInteger();
            int maxAttempts = task.attempts();

            BukkitTask bkTask = scheduler.runTaskTimer(plugin, () -> {
                int currentIterate = currentIteration.incrementAndGet();

                if (currentIterate >= maxAttempts) {
                    Bukkit.getScheduler().cancelTask(processId.get());
                }

                task.func().accept(null);
            }, task.delay(), interval);
            processId.set(bkTask.getTaskId());
        }
    }
}

record TweenTask(Consumer<Void> func, long delay, long interval, int attempts) {
}