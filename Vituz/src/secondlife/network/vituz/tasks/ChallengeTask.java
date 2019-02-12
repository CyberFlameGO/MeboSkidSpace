package secondlife.network.vituz.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.ChallengeData;
import secondlife.network.vituz.utilties.ChallengeUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.Date;

public class ChallengeTask extends BukkitRunnable {

    public ChallengeTask() {
        this.runTaskTimerAsynchronously(Vituz.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            ChallengeData data = ChallengeData.getByName(player.getName());

            if(!data.isW1_1()) {
                Date now = new Date();

                if(now.getHours() == 4) {
                    data.setW1_1(true);
                    ChallengeUtils.givePoints(player, 10);
                    player.sendMessage(Color.translate("&4&lYou have successfully completed challenge 1 (Week 1)"));
                    player.sendMessage(Color.translate("&4&lPlease relog to receive your points."));
                }
            }

            if(!data.isW1_2()) {
                if(data.getPlayedRankedMatches() >= 150) {
                    data.setW1_2(true);
                    ChallengeUtils.givePoints(player, 10);
                    player.sendMessage(Color.translate("&4&lYou have successfully completed challenge 2 (Week 1)"));
                    player.sendMessage(Color.translate("&4&lPlease relog to receive your points."));
                }
            }

            if(!data.isW1_3()) {
                if(data.getWonRankedMatches() >= 60) {
                    data.setW1_3(true);
                    ChallengeUtils.givePoints(player, 10);
                    player.sendMessage(Color.translate("&4&lYou have successfully completed challenge 3 (Week 1)"));
                    player.sendMessage(Color.translate("&4&lPlease relog to receive your points."));
                }
            }

            if(!data.isW1_4()) {
                if(data.getPlayedUHCGames() >= 10) {
                    data.setW1_4(true);
                    ChallengeUtils.givePoints(player, 10);
                    player.sendMessage(Color.translate("&4&lYou have successfully completed challenge 4 (Week 1)"));
                    player.sendMessage(Color.translate("&4&lPlease relog to receive your points."));
                }
            }

            if(!data.isW1_5()) {
                if(data.getKillsInSingleUHCGame() >= 5) {
                    data.setW1_5(true);
                    ChallengeUtils.givePoints(player, 5);
                    player.sendMessage(Color.translate("&4&lYou have successfully completed challenge 5 (Week 1)"));
                    player.sendMessage(Color.translate("&4&lPlease relog to receive your points."));
                }
            }
        });
    }
}
