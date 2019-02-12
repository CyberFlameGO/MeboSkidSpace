package secondlife.network.meetupgame.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.GameData;
import secondlife.network.meetupgame.managers.GameManager;
import secondlife.network.vituz.utilties.Msg;

import java.util.Arrays;

/**
 * Created by Marko on 23.07.2018.
 */
public class StartingTask extends BukkitRunnable {

    private MeetupGame plugin = MeetupGame.getInstance();

    public StartingTask() {
        runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void run() {
        GameData data = GameManager.getGameData();

        int time = data.getStartingTime();

        data.setStartingTime(time - 1);

        if(time <= 0) {
            plugin.getGameManager().handleStart();
            cancel();
            return;
        }

        if(Arrays.asList(15, 10, 5, 4, 3, 2, 1).contains(time)) {
            Msg.sendMessage("&eThe game will begin in &d" + time + " second" + (time > 1 ? "s" : "") + "&e.");

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1F, 1F));
        }
    }
}
