package secondlife.network.vituz.utilties;

import org.bukkit.Bukkit;
import secondlife.network.vituz.Vituz;

public class Tasks {

    public static void run(Callable callable) {
        Bukkit.getScheduler().runTask(Vituz.getInstance(), callable::call);
    }

    public static void runAsync(Callable callable) {
        Bukkit.getScheduler().runTaskAsynchronously(Vituz.getInstance(), callable::call);
    }

    public static void runLater(Callable callable, long delay) {
        Bukkit.getScheduler().runTaskLater(Vituz.getInstance(), callable::call, delay);
    }

    public static void runAsyncLater(Callable callable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Vituz.getInstance(), callable::call, delay);
    }

    public static void runTimer(Callable callable, long delay, long interval) {
        Bukkit.getScheduler().runTaskTimer(Vituz.getInstance(), callable::call, delay, interval);
    }

    public static void runAsyncTimer(Callable callable, long delay, long interval) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Vituz.getInstance(), callable::call, delay, interval);
    }

    public interface Callable {
        void call();
    }
}
