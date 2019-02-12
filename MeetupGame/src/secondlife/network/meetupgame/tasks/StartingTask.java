package secondlife.network.meetupgame.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.vituz.utilties.Msg;

import java.util.Arrays;

/**
 * Created by Marko on 12.06.2018.
 */
public class StartingTask extends BukkitRunnable {

    public static int seconds = 30;
    private MeetupGame plugin = MeetupGame.getInstance();

    public StartingTask() {
        runTaskTimerAsynchronously(MeetupGame.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        if(seconds < 0) {
            plugin.getGameManager().startGame();
            cancel();
            return;
        }

        if(Arrays.asList(30, 25, 20, 15, 10, 5, 4, 3, 2, 1).contains(seconds)) {
            Msg.sendMessage("&eThe game will begin in &d" + seconds + " second" + (seconds > 1 ? "s" : "") + "&e.");

            for(Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
            }
        }

        seconds--;
    }
}