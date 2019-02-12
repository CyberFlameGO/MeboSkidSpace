package secondlife.network.meetupgame.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.ServerUtils;

import java.util.Arrays;

/**
 * Created by Marko on 13.06.2018.
 */
public class WinTask extends BukkitRunnable {

    public static int seconds = 10;

    public WinTask() {
        runTaskTimer(MeetupGame.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        if(seconds < 0) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                ServerUtils.sendToServer(player, "Minigames");
            }

            cancel();
            return;
        }

        if(Arrays.asList(10, 5, 4, 3, 2, 1).contains(seconds)) {
            Msg.sendMessage("&eThe game ends in &d" + seconds + " second" + (seconds > 1 ? "s" : "") + "&e.");

            for(Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
            }
        }

        seconds--;
    }
}
