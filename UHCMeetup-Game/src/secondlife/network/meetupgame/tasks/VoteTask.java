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
public class VoteTask extends BukkitRunnable {

    private MeetupGame plugin = MeetupGame.getInstance();

    public VoteTask() {
        runTaskTimer(MeetupGame.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        GameData data = GameManager.getGameData();

        int time = data.getVoteTime();

        if(data.isCanStartCountdown()) {
            data.setVoteTime(time - 1);
        }

        if(time <= 0) {
            plugin.getGameManager().handleStarting();
            cancel();
            return;
        }

        plugin.getInventoryManager().handleUpdateScenarios();

        if(Bukkit.getOnlinePlayers().size() >= 6) {
            data.setCanStartCountdown(true);
        }

        if(Arrays.asList(25, 20, 15, 10, 5, 4, 3, 2, 1).contains(time)) {
            Msg.sendMessage("&eVoting ends in &d" + time + " second" + (time > 1 ? "s" : "") + "&e.");

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1F, 1F));
        }
    }
}
