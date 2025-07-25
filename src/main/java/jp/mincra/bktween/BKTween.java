package jp.mincra.bktween;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * BKTween(BukkitTween).
 * This example print "Bar" 20 ticks after print "Foo"
 * tween.execute(() -> print("Foo"))
 *      .delay(TickTime.TICK, 20)
 *      .execute(() -> print("Bar"))
 *      .run();
 */
public class BKTween {
    private Function<Void, Boolean> tmpFunction;
    private final Queue<TweenTask> tasks;
    private final Plugin plugin;

    public BKTween(Plugin plugin) {
        tasks = new ArrayDeque<>();
        this.plugin = plugin;
    }

    public BKTween execute(Function<Void, Boolean> func) {
        if (tmpFunction == null) {
            tmpFunction = func;
        } else {
            tmpFunction = tmpFunction.andThen(v -> func.apply(null));
        }
        return this;
    }

    public BKTween delay(TickTime tickTime, long delay) {
        tasks.add(new TweenTask(tmpFunction,
                tickTime.getMultiplier() * delay, 0, 0));
        tmpFunction = null;
        return this;
    }

    /**
     *
     * @param tickTime interval, delay の単位
     * @param interval 関数を実行する間隔
     * @param delay 関数を実行するまで待機する時間
     * @param attempts -1 にすると無限に実行
     * @return 同一のBKTweenオブジェクト
     */
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
            // 遅延実行
            scheduler.runTaskLater(plugin, () -> {
                var func = task.func();
                if (func != null) {
                    func.apply(null);
                }
                run();
            }, task.delay());
        } else {
            // 繰り返し実行
            AtomicInteger currentIteration = new AtomicInteger(0);
            AtomicInteger processId = new AtomicInteger();
            int maxAttempts = task.attempts();

            BukkitTask bkTask = scheduler.runTaskTimer(plugin, () -> {
                int currentIterate = currentIteration.incrementAndGet();

                if (currentIterate >= maxAttempts && maxAttempts != -1) {
                    Bukkit.getScheduler().cancelTask(processId.get());
                }

                boolean shouldContinues = task.func().apply(null);
                if (!shouldContinues) {
                    Bukkit.getScheduler().cancelTask(processId.get());
                }
            }, task.delay(), interval);
            processId.set(bkTask.getTaskId());
        }
    }
}

record TweenTask(Function<Void, Boolean> func, long delay, long interval, int attempts) {
}