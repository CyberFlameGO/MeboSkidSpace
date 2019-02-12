package secondlife.network.meetupgame.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.GameData;
import secondlife.network.meetupgame.managers.GameManager;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.ServerUtils;

import java.util.Arrays;

/**
 * Created by Marko on 23.07.2018.
 */
public class WinTask extends BukkitRunnable {

    WinTask() {
        runTaskTimer(MeetupGame.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        GameData data = GameManager.getGameData();

        int time = data.getEndTime();

        data.setEndTime(time - 1);

        if(time <= 0) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            cancel();
            return;
        }



        /*if(time == 5) {
            Bukkit.getOnlinePlayers().forEach(player -> MeetupData.getByName(player.getName()).save());
        }*/

        if(time == 2) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                ServerUtils.sendToServer(player, "UHCMeetup-Lobby");
            });
        }

        if(Arrays.asList(10, 5, 4, 3, 2, 1).contains(time)) {
            Msg.sendMessage("&eThe game ends in &d" + time + " second" + (time > 1 ? "s" : "") + "&e.");

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1F, 1F));
        }
    }
}
