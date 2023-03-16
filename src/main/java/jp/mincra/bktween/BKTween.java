package jp.mincra.bktween;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayDeque;
import java.util.Queue;
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
        tasks.add(new TweenTask(tmpFunction, tickTime.getMultiplier() * delay, 0));
        tmpFunction = null;
        return this;
    }

    public BKTween repeat(TickTime tickTime, long interval, long delay) {
        tasks.add(new TweenTask(tmpFunction, delay, interval));
        tmpFunction = null;
        return this;
    }

    public void run() {
        if (tmpFunction != null) {
            tasks.add(new TweenTask(tmpFunction, 0, 0));
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
                task.func().accept(null);
                run();
            }, task.delay());
        } else {
            scheduler.runTaskTimer(plugin, () -> {
                task.func().accept(null);
                run();
            }, task.delay(), interval);

        }
    }
}

record TweenTask(Consumer<Void> func, long delay, long interval) { }
