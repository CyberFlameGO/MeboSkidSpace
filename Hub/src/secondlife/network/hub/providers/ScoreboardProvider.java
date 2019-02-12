package secondlife.network.hub.providers;

import org.bukkit.entity.Player;
import secondlife.network.hub.Hub;
import secondlife.network.hub.managers.QueueManager;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.providers.ScoreProvider;
import secondlife.network.vituz.providers.scoreboard.ScoreboardConfiguration;
import secondlife.network.vituz.providers.scoreboard.TitleGetter;
import secondlife.network.vituz.punishments.Punishment;
import secondlife.network.vituz.punishments.PunishmentType;
import secondlife.network.vituz.utilties.Color;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardProvider implements ScoreProvider {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration sc = new ScoreboardConfiguration();
        
        sc.setTitleGetter(new TitleGetter("&5&lSecondLife"));
        sc.setScoreGetter(new ScoreboardProvider());
        
        return sc;
    }

    @Override
    public String[] getScores(Player player) {
        List<String> board = new ArrayList<>();
        
        if(player != null) {
        	board.add("&7&m--------------------------");

        	if(VituzAPI.isBanned(player)) {
				PunishData punishData = PunishData.getByName(player.getName());
				Punishment punishment = punishData.getBannedPunishment();

				if(punishment.isActive() && punishment.getType() != PunishmentType.BLACKLIST) {
					board.add("&fYou are " + (punishment.isPermanent() ? "permanently " : "temporarily ") + "banned");
					board.add("");
					board.add("&fDuration:");
					board.add("&d" + punishment.getTimeLeft());
					board.add("");
				}

				board.add("&fYou can appeal at:");
				board.add("&d" + Vituz.getInstance().getEssentialsManager().getAppealAt());
			} else {
        		board.add("&fOnline:");
        		board.add("&d" + Hub.getInstance().getCountManager().getGlobalCount());
        		board.add("");
				board.add("&fRank:");
				board.add(VituzAPI.getColorPrefix(player) + VituzAPI.getRankName(player.getName()));
        		board.add("");
				if(QueueManager.getByPlayer(player) != null) {
					board.add("&f" + QueueManager.getQueueName(player).toUpperCase() + " Queue:");
					board.add("&d#" + (QueueManager.getByPlayer(player).getPlayers().indexOf(player) + 1) + " out of " + QueueManager.getByPlayer(player).getPlayers().size());
					board.add("");
				}

				board.add("&dsecondlife.network");
			}

			board.add("&d&7&m--------------------------");
        }

		return board.stream().map(Color::translate).toArray(String[]::new);
    }
}
