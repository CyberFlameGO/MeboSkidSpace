package secondlife.network.meetupgame.providers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.GameData;
import secondlife.network.meetupgame.data.MeetupData;
import secondlife.network.meetupgame.managers.GameManager;
import secondlife.network.meetupgame.managers.ScenarioManager;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.scenario.type.NoCleanScenario;
import secondlife.network.meetupgame.tasks.BorderTimeTask;
import secondlife.network.meetupgame.utilties.MeetupUtils;
import secondlife.network.vituz.providers.ScoreProvider;
import secondlife.network.vituz.providers.scoreboard.ScoreboardConfiguration;
import secondlife.network.vituz.providers.scoreboard.TitleGetter;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoreboardProvider implements ScoreProvider {

	private MeetupGame plugin = MeetupGame.getInstance();

	public static ScoreboardConfiguration create() {
		ScoreboardConfiguration sc = new ScoreboardConfiguration();

		sc.setTitleGetter(new TitleGetter("&5&lSecondLife"));
		sc.setScoreGetter(new ScoreboardProvider());

		return sc;
	}

	@Override
	public String[] getScores(Player player) {
		List<String> board = new ArrayList<>();

		MeetupData meetupData = MeetupData.getByName(player.getName());
		GameData data = GameManager.getGameData();

		switch (GameManager.getGameData().getGameState()) {
			case VOTE: {
				board.add("&7&m----------------------");
				board.add("&fOnline: &d" + Bukkit.getOnlinePlayers().size());
				board.add("&fRerolls: &d" + meetupData.getRerolls());
				board.add("");
				board.add("&fScenario Votes:");
				ScenarioManager.getScenarios()
						.stream().sorted(Comparator.comparing(Scenario::getName))
						.forEach(scenario -> {

						if(plugin.getVoteManager().getVotes().get(scenario) > 0) {
							board.add(" &7* &f" + scenario.getName() + "&7: &d" + plugin.getVoteManager().getVotes().get(scenario));
						}
				});
				if(data.isCanStartCountdown()) {
					board.add("");
					board.add("&fVoting ends in &d" + StringUtils.formatInt(data.getVoteTime() + 1));
				}
				board.add("&1&7&m----------------------");
				break;
			}

			case STARTING: {
				board.add("&7&m----------------------");
				board.add("&fOnline: &d" + Bukkit.getOnlinePlayers().size());
				board.add("&fRerolls: &d" + meetupData.getRerolls());
				board.add("&fStarting in: &d" + StringUtils.formatInt(data.getStartingTime() + 1));
				board.add("&1&7&m----------------------");
				break;
			}

			case PLAYING: {
				if(player.hasPermission(Permission.STAFF_PERMISSION)) {
					if(MeetupUtils.isPlayerInSpecMode(player)) {
						board.add("&9&7&m----------------------");
						board.add("&5&lStaff Mode:");
						board.add(" &7* &dOnline: &d" + Bukkit.getOnlinePlayers().size());

						DecimalFormat dc = new DecimalFormat("##.#");
						double tps = Bukkit.spigot().getTPS()[0];

						board.add(" &7* &dTPS: &d" + dc.format(tps));
					}
				}

				board.add("&7&m----------------------");
				board.add("&fGame Time: &d" + StringUtils.formatInt(data.getGameTime()));
				board.add("&fRemaining: &d" + plugin.getGameManager().getAlivePlayers() + "/" + data.getInitial());

				if(NoCleanScenario.isActive(player)) {
					board.add("&fNo Clean: &d" + StringUtils.getRemaining(NoCleanScenario.getMillisecondsLeft(player), true));
				}

				if(meetupData.isAlive()) {
					board.add("&fKills: &d" + meetupData.getGameKills());
				}

				if(data.isCanBorderTime()) {
					int i = BorderTimeTask.seconds;

					if(data.getBorder() > 5) {
						if(i > 0) {
							board.add("&fBorder: &d" + data.getBorder() + " &7(&c" + i + "s&7)");
						} else {
							board.add("&fBorder: &d" + data.getBorder());
						}
					}
				} else {
					board.add("&fBorder: &d" + data.getBorder());
				}

				board.add("");
				board.add("&dsecondlife.network");
				board.add("&1&7&m----------------------");
				break;
			}

			case WINNER: {
				board.add("&7&m----------------------");

				if(plugin.getGameManager().getAlivePlayers() == 1) {
					board.add("&fWinner: &d" + data.getWinner());
				}

				board.add("");
				board.add("&dsecondlife.network");
				board.add("&1&7&m----------------------");
				break;
			}
		}

		return board.stream().map(Color::translate).toArray(String[]::new);
	}
}
