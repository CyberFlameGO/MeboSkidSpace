package secondlife.network.meetupgame.providers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.GameData;
import secondlife.network.meetupgame.data.MeetupData;
import secondlife.network.meetupgame.managers.GameManager;
import secondlife.network.meetupgame.managers.ScenarioManager;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.states.GameState;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.providers.LayoutProvider;
import secondlife.network.vituz.providers.tab.TabLayout;

public class TabProvider implements LayoutProvider {

	private MeetupGame plugin = MeetupGame.getInstance();

	public TabLayout getLayout(Player player) {
		TabLayout layout = TabLayout.create(player);

		if(PlayerData.getByName(player.getName()).isTab()) {
			MeetupData meetupData = MeetupData.getByName(player.getName());
			GameData data = GameManager.getGameData();

			layout.set(1, 0, "&5&lSecondLife");

			layout.set(1, 2, "&fPlayers Online:");
			layout.set(1, 3, "&d" + Bukkit.getOnlinePlayers().size());

			layout.set(1, 5, "&5&lScenarios");

			int x = 6;
			for(Scenario scenario : ScenarioManager.scenarios) {
				if(scenario.isEnabled()) {
					layout.set(1, x, "&d" + scenario.getName());
					x++;
				}
			}

			if(x == 6) {
				layout.set(1, 6, "&dNone");
			}

			layout.set(0, 5, "&fPlayer Info:");
			layout.set(0, 6, "&fKills: &d" + meetupData.getKills());
			layout.set(0, 7, "&fElo: &d" + meetupData.getElo());
			layout.set(0, 8, "&fWins: &d" + meetupData.getWins());

			layout.set(0, 10, "&fServer:");
			layout.set(0, 11, "&d" + VituzAPI.getServerName());

			layout.set(2, 5, "&fGame Info:");
			layout.set(2, 6, "&fBorder: &d" + data.getBorder());
			layout.set(2, 7, "&fSpectators: &d" + plugin.getSpectatorManager().getSpectators().size());
			layout.set(2, 8, "&fAlive: &d" + plugin.getGameManager().getAlivePlayers());

			layout.set(2, 10, "&fYour Location:");
			layout.set(2, 11, "&d(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ") [" + this.getCardinalDirection(player) + "]");

			if(data.getGameState().equals(GameState.VOTE)) {
				layout.set(2, 13, "&fStarting in:");

				if(data.getVoteTime() < 0) {
					layout.set(2, 14, "&d0&f...");
				} else {
					layout.set(2, 14, "&d" + (data.getVoteTime() + 1) + " &fseconds");
				}
			} else if(data.getGameState().equals(GameState.STARTING)) {
				layout.set(2, 13, "&fStarting in:");

				if(data.getStartingTime() < 0) {
					layout.set(2, 14, "&d0&f...");
				} else {
					layout.set(2, 14, "&d" + (data.getStartingTime() + 1) + " &fseconds");
				}
			}
		} else {
			int count = 0;

			for (Player online : Bukkit.getOnlinePlayers()) {
				if (count < 60) {
					layout.forceSet(count, VituzAPI.getNamePrefix(online) + online.getName());
					count++;
				}
			}
		}

		return layout;
	}

	public String getCardinalDirection(Player player) {
		double rot = (player.getLocation().getYaw() - 90.0F) % 360.0F;
		if (rot < 0.0D) {
			rot += 360.0D;
		}
		return getDirection(rot);
	}

	private String getDirection(double rot) {
		if ((0.0D <= rot) && (rot < 22.5D)) {
			return "W";
		}
		if ((22.5D <= rot) && (rot < 67.5D)) {
			return "NW";
		}
		if ((67.5D <= rot) && (rot < 112.5D)) {
			return "N";
		}
		if ((112.5D <= rot) && (rot < 157.5D)) {
			return "NE";
		}
		if ((157.5D <= rot) && (rot < 202.5D)) {
			return "E";
		}
		if ((202.5D <= rot) && (rot < 247.5D)) {
			return "SE";
		}
		if ((247.5D <= rot) && (rot < 292.5D)) {
			return "S";
		}
		if ((292.5D <= rot) && (rot < 337.5D)) {
			return "SW";
		}
		if ((337.5D <= rot) && (rot < 360.0D)) {
			return "W";
		}
		return null;
	}
}
