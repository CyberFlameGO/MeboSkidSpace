package secondlife.network.vituz.providers.threads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.providers.scoreboard.VituzScoreboard;

public class ScoreboardThread extends Thread {

    public ScoreboardThread() {
        super("Vituz - Scoreboard Thread");
        setDaemon(false);
    }

    public void run() {
        while(true) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                try {
                    if(VituzAPI.hasEssentialsData(player)) {
                        VituzScoreboard.updateScoreboard(player);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            try {
                Thread.sleep(VituzAPI.scoreboardTime * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}