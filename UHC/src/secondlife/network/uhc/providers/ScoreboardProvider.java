package secondlife.network.uhc.providers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.arguments.GameCommand;
import secondlife.network.uhc.managers.*;
import secondlife.network.uhc.player.UHCData;
import secondlife.network.uhc.scenario.Scenario;
import secondlife.network.uhc.scenario.type.NoCleanScenario;
import secondlife.network.uhc.tasks.AutoStartTask;
import secondlife.network.uhc.tasks.BorderTimeTask;
import secondlife.network.uhc.tasks.GameTask;
import secondlife.network.uhc.utilties.UHCUtils;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.providers.ScoreProvider;
import secondlife.network.vituz.providers.scoreboard.ScoreboardConfiguration;
import secondlife.network.vituz.providers.scoreboard.TitleGetter;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardProvider implements ScoreProvider {

	private UHC plugin = UHC.getInstance();

	public static ScoreboardConfiguration create() {
		ScoreboardConfiguration sc = new ScoreboardConfiguration();

		sc.setTitleGetter(new TitleGetter("&5&lSecondLife &7" + '❘' + " &f" + VituzAPI.getServerName()));
		sc.setScoreGetter(new ScoreboardProvider());

		return sc;
	}

	@Override
	public String[] getScores(Player player) {
		List<String> board = new ArrayList<>();

		UHCData uhcData = UHCData.getByName(player.getName());

		if(plugin.getPracticeManager().getUsers().contains(player.getUniqueId())) {
			add(board, "&7&m----------------------");
			add(board, "&fOnline: &d" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
			add(board, "&fIn Arena: &d" + plugin.getPracticeManager().getUsers().size() + "/" + plugin.getPracticeManager().getSlots());
			add(board, "&fNext UHC: &d" + plugin.getGameManager().getNextuhc());
			if(AutoStartTask.running) {
				add(board, "&fStarting in &d" + StringUtils.getRemaining(AutoStartTask.seconds, false));
			}
			add(board, "");
			add(board, "&dsecondlife.network");
			add(board, "&1&7&m----------------------");
		} else {
			switch (GameManager.getGameState()) {
				case LOBBY: {
					add(board, "&7&m----------------------");
					add(board, "&fOnline: &d" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());

					if(plugin.getPracticeManager().isOpen()) {
						if(player.hasPermission(Permission.STAFF_PERMISSION)) {
							add(board, "&fPractice: &d" + plugin.getPracticeManager().getUsers().size() + "/" + plugin.getPracticeManager().getSlots());
						}
					}

					add(board, "&fGame Type: &d" + UHCUtils.isPartiesEnabled());
					add(board, "&fNext UHC: &d" + plugin.getGameManager().getNextuhc());
					if(AutoStartTask.running) {
						add(board, "&fStarting in &d" + StringUtils.getRemaining(AutoStartTask.seconds, false));
					}
					add(board, "");
					add(board, "&fScenarios:");

					if(ScenarioManager.getActiveScenarios() == 0) {
						add(board, " &7* &dNo Scenarios");
					} else {
						for(Scenario scenario : ScenarioManager.scenarios) {
							if(scenario.isEnabled()) {
								add(board, " &7* &d" + scenario.getName());
							}
						}
					}

					add(board, "&1&7&m----------------------");
					break;
				}

				case SCATTERING: {
					add(board, "&7&m----------------------");

					if(GameCommand.getInt() < 1) {
						add(board, "&fStarting&d...");
					} else {
						add(board, "&fStarting in: &d" + StringUtils.formatInt(GameCommand.startsIn()));
					}

					add(board, "");

					if(GameCommand.getInt() < 1) {
						add(board, "&fEveryone is scattered&7.");
					} else {
						add(board, "&fScattering: &d" + GameCommand.getInt() + " players");
					}
					add(board, "&fScattered: &d" + PlayerManager.getAlivePlayers() + " players");

					add(board, "");
					add(board, "&dsecondlife.network");
					add(board, "&1&7&m----------------------");
					break;
				}

				case PLAYING: {
					if(player.hasPermission(Permission.STAFF_PERMISSION)) {
						if(UHCUtils.isPlayerInSpecMode(player)) {
							add(board, "&9&7&m----------------------");
							add(board, "&5&lStaff Mode:");
							add(board, " &7* &dOnline: &d" + Bukkit.getOnlinePlayers().size());

							DecimalFormat dc = new DecimalFormat("##.#");
							double tps = Bukkit.spigot().getTPS()[0];

							add(board, " &7* &dTPS: &d" + dc.format(tps));
						}
					}

					add(board, "&7&m----------------------");
					add(board, "&fGame Time: &d" + StringUtils.formatInt(GameTask.seconds));
					add(board, "&fRemaining: &d" + PlayerManager.getAlivePlayers() + "/" + plugin.getGameManager().getInitial());

					if(NoCleanScenario.isActive(player)) {
						add(board, "&fNo Clean: &d" + StringUtils.getRemaining(NoCleanScenario.getMillisecondsLeft(player), true));
					}

					if(uhcData.isAlive()) {
						add(board, "&fKills: &d" + uhcData.getKills());

						if(PartyManager.isEnabled()) {
							add(board, "&fTeam Kills: &d" + PartyManager.getByPlayer(player).getKills());
						}
					}

					if(plugin.getGameManager().isBorderTime()) {
						int i = BorderTimeTask.seconds;

						if(BorderManager.border > 25) {
							if(i < 60) {
								add(board, "&fBorder: &d" + BorderManager.border + " &7(&c" + i + "s&7)");
							} else {
								add(board, "&fBorder: &d" + BorderManager.border + " &7(&c" + (i / 60 + 1) + "m&7)");
							}

							if(i < 0) {
								add(board, "&fBorder: &d" + BorderManager.border);
							}
						}
					} else {
						add(board, "&fBorder: &d" + BorderManager.border);
					}

					add(board, "");
					add(board, "&dsecondlife.network");
					add(board, "&1&7&m----------------------");
					break;
				}

				case WINNER: {
					add(board, "&7&m----------------------");
					if(PartyManager.isEnabled()) {
						if(plugin.getPartyManager().getPartiesAlive() == 1) {
							add(board, "&fWinners:");

							for(String team : plugin.getPartyManager().getLastParty().getPlayers()) {
								UHCData teamPlayer = UHCData.getByName(Bukkit.getPlayer(team).getName());

								if(teamPlayer.isAlive()) {
									add(board, " &7* &d" + teamPlayer.getName());
								}
							}
						}
					} else {
						if(PlayerManager.getAlivePlayers() < 2) {
							add(board, "&fWinner: &d" + plugin.getGameManager().getWinner());
						}
					}
					add(board, "");
					add(board, "&dsecondlife.network");
					add(board, "&1&7&m----------------------");
					break;
				}
			}
		}

		return board.toArray(new String[] {});
	}

	private void add(List list, String text) {
		list.add(Color.translate(text));
	}
}
