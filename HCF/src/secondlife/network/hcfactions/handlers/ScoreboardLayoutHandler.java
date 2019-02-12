package secondlife.network.hcfactions.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.classes.Archer;
import secondlife.network.hcfactions.classes.Bard;
import secondlife.network.hcfactions.classes.Miner;
import secondlife.network.hcfactions.classes.Rogue;
import secondlife.network.hcfactions.classes.utils.ArmorClassHandler;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.events.KitMapEvent;
import secondlife.network.hcfactions.events.sumo.SumoEvent;
import secondlife.network.hcfactions.events.sumo.SumoPlayer;
import secondlife.network.hcfactions.game.events.eotw.EOTWHandler;
import secondlife.network.hcfactions.game.events.eotw.EOTWRunnable;
import secondlife.network.hcfactions.game.events.faction.KothFaction;
import secondlife.network.hcfactions.staff.handlers.StaffModeHandler;
import secondlife.network.hcfactions.staff.handlers.VanishHandler;
import secondlife.network.hcfactions.timers.*;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.hcfactions.utilties.file.ConfigFile;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.providers.ScoreProvider;
import secondlife.network.vituz.providers.scoreboard.ScoreboardConfiguration;
import secondlife.network.vituz.providers.scoreboard.TitleGetter;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ScoreboardLayoutHandler implements ScoreProvider {

	public static ScoreboardConfiguration create() {
		ScoreboardConfiguration sc = new ScoreboardConfiguration();

		sc.setTitleGetter(new TitleGetter(Color.translate(ConfigFile.configuration.getString("SCOREBOARD.TITLE"))));
		sc.setScoreGetter(new ScoreboardLayoutHandler());

		return sc;
	}

	@Override
	public String[] getScores(Player player) {
		List<String> board = new ArrayList<>();

		KitMapEvent event = HCF.getInstance().getEventManager().getEventPlaying(player);

		if(HCFConfiguration.kitMap) {
			if(HCFConfiguration.kitMap
					|| StaffModeHandler.isInStaffMode(player)
					|| AppleHandler.isActive(player)
					|| !GameHandler.getGameHandler().getActiveKoths().isEmpty()
					|| SpawnTagHandler.isActive(player)
					|| ThrowableCobwebHandler.isActive(player)
					|| EnderpearlHandler.isActive(player)
					|| StuckHandler.isActive(player)
					|| GappleHandler.isActive(player)
					|| ClassWarmupHandler.isActive(player)
					|| HomeHandler.isActive(player)
					|| LogoutHandler.teleporting.containsKey(player)
					|| GameHandler.getEventFaction() instanceof KothFaction
					|| ArmorClassHandler.getEquippedClass(player) != null) {
				board.add("&1&7&m----------------------");
			}

			if(StaffModeHandler.isInStaffMode(player)) {
				board.add("&5&lStaff Mode");

				double tps = Bukkit.spigot().getTPS()[0];

				board.add(" §7* §fTPS: §d" + JavaUtils.format(tps, 2));

				if(VanishHandler.vanishedPlayers.contains(player.getUniqueId())) {
					board.add(" &7* &fVanish: &aEnabled");
				} else {
					board.add(" &7* &fVanish: &cDisabled");
				}

				if(player.getGameMode() == GameMode.CREATIVE) {
					board.add(" &7* &fGamemode: &aCreative");
				} else if(player.getGameMode() == GameMode.SURVIVAL) {
					board.add(" &7* &fGamemode:Survival");
				} else if(player.getGameMode() == GameMode.ADVENTURE) {
					board.add(" &7* &fGamemode: &eAdventure");
				}

				board.add(" &6* &fOnline: &d" + String.valueOf(Bukkit.getOnlinePlayers().size()));
				board.add("&2&7&m----------------------");
			}

			if(HCFConfiguration.kitMap) {
				HCFData data = HCFData.getByName(player.getName());
				board.add("&c&lKills: &f" + data.getKills());
				board.add("&c&lDeaths: &f" + data.getDeaths());
				board.add("&c&lKill Streak: &f" + data.getKillStreak());


				if(event == null && System.currentTimeMillis() < HCF.getInstance().getEventManager().getCooldown()) {
					add(board, "&c&lCooldown: &f" + secondlife.network.hcfactions.utilties.TimeUtil.convertToFormat(HCF.getInstance().getEventManager().getCooldown()));
				}
			}

			if(AppleHandler.isActive(player)) {
				board.add("&6&lApple: &f" + StringUtils.getRemaining(AppleHandler.getMillisecondsLeft(player), true));
			}

			if(ThrowableCobwebHandler.isActive(player)) {
				board.add("&b&lThrowable Cobweb: &f" + StringUtils.getRemaining(AppleHandler.getMillisecondsLeft(player), true));
			}

			if(ClassWarmupHandler.isActive(player)) {
				board.add("&b&lClass Warmup: &f" + StringUtils.getRemaining(ClassWarmupHandler.getMillisecondsLeft(player), true));
			}

			if(HomeHandler.isActive(player)) {
				board.add("&9&lHome: &9" + StringUtils.getRemaining(HomeHandler.getMillisecondsLeft(player), true));
			}

			if(EnderpearlHandler.isActive(player)) {
				board.add("&e&lEnderpearl: &f" + StringUtils.getRemaining(EnderpearlHandler.getMillisecondsLeft(player), true));
			}

			if(SpawnTagHandler.isActive(player)) {
				board.add("&c&lSpawn Tag: &f" + StringUtils.getRemaining(SpawnTagHandler.getMillisecondsLeft(player), false));
			}

			if(GappleHandler.isActive(player)) {
				board.add("&6&lGapple: &f" + StringUtils.getRemaining(GappleHandler.getMillisecondsLeft(player), false));
			}

			if(StuckHandler.isActive(player)) {
				board.add("&4&lStuck: &f" + StringUtils.getRemaining(StuckHandler.getMillisecondsLeft(player), false));
			}

			if(LogoutHandler.teleporting.containsKey(player)) {
				board.add("&4&lLogout: &f" + StringUtils.getRemaining(LogoutHandler.getMillisecondsLeft(player), true));
			}

			if(ArmorClassHandler.getEquippedClass(player) instanceof Bard) {
				Bard bardClass = (Bard) ArmorClassHandler.getEquippedClass(player);

				long effectCooldown = bardClass.getRemainingBuffDelay(player);

				if(effectCooldown > 0) {
					board.add("&a&lBard Effect: &f" + StringUtils.getRemaining(effectCooldown, false));
				}

				board.add("&b&lBard Energy: &f" + HCFUtils.getBardFormat(bardClass.getEnergyMillis(player), true, false) + ".0");
			} else if(ArmorClassHandler.getEquippedClass(player) instanceof Archer) {
				board.add("&6&lClass: &fArcher");

				long aSpeed = Archer.speed_cooldowns.get(player.getUniqueId());
				long aSpeedCooldown = aSpeed == Archer.speed_cooldowns.getNoEntryValue() ? -1L : aSpeed - System.currentTimeMillis();

				if(aSpeedCooldown > 0L) {
					board.add("&e&lSpeed: &f" + StringUtils.getRemaining(aSpeedCooldown, true));
				}

				long aJump = Archer.jump_cooldowns.get(player.getUniqueId());
				long aJumpCooldown = aJump == Archer.jump_cooldowns.getNoEntryValue() ? -1L : aJump - System.currentTimeMillis();

				if(aJumpCooldown > 0L) {
					board.add("&e&lJump: &f" + StringUtils.getRemaining(aJumpCooldown, true));
				}
			} else if(ArmorClassHandler.getEquippedClass(player) instanceof Rogue) {
				board.add("&6&lClass: &fRogue");

				long rSpeed = Rogue.speed_cooldowns.get(player.getUniqueId());
				long rSpeedCooldown = rSpeed == Rogue.speed_cooldowns.getNoEntryValue() ? -1L : rSpeed - System.currentTimeMillis();

				if(rSpeedCooldown > 0L) {
					board.add("&e&lSpeed: &f" + StringUtils.getRemaining(rSpeedCooldown, true));
				}

				long rJump = Rogue.jump_cooldowns.get(player.getUniqueId());
				long rJumpCooldown = rJump == Rogue.jump_cooldowns.getNoEntryValue() ? -1L : rJump - System.currentTimeMillis();

				if(rJumpCooldown > 0L) {
					board.add("&e&lJump: &f" + StringUtils.getRemaining(rJumpCooldown, true));
				}
			} else if(ArmorClassHandler.getEquippedClass(player) instanceof Miner) {
				board.add("&6&lClass: &fMiner");
			}

			if(GameHandler.getGameHandler() != null) {
				if(GameHandler.getEventFaction() instanceof KothFaction) {
					if(GameHandler.getEventFaction().getName().equalsIgnoreCase("eotw")) {
						board.add("&4&lEOTW: &f" + StringUtils.getRemaining(GameHandler.getRemaining(), false));
					} else if(GameHandler.getEventFaction().getName().equalsIgnoreCase("citadel")) {
						board.add("&5&lCitadel: &f" + StringUtils.getRemaining(GameHandler.getRemaining(), false));
					} else {
						board.add("&9&l" + GameHandler.getEventFaction().getScoreboardName() + ": &f" + StringUtils.getRemaining(GameHandler.getRemaining(), false));
					}
				}
			}

			if(HCFConfiguration.kitMap
					|| AppleHandler.isActive(player)
					|| EnderpearlHandler.isActive(player)
					|| !GameHandler.getGameHandler().getActiveKoths().isEmpty()
					|| SpawnTagHandler.isActive(player)
					|| StuckHandler.isActive(player)
					|| ThrowableCobwebHandler.isActive(player)
					|| LogoutHandler.teleporting.containsKey(player)
					|| HomeHandler.isActive(player)
					|| GappleHandler.isActive(player)
					|| ClassWarmupHandler.isActive(player)
					|| GameHandler.getEventFaction() instanceof KothFaction
					|| ArmorClassHandler.getEquippedClass(player) != null) {
				board.add("&3&7&m----------------------");
			}

			if(event != null && HCFData.getByName(player.getName()).isEvent()) {
				board.add("&fEvent &d" + "(" + event.getName() + ")");

				if (event instanceof SumoEvent) {
					SumoEvent sumoEvent = (SumoEvent) event;

					int playingSumo = sumoEvent.getByState(SumoPlayer.SumoState.WAITING).size() + sumoEvent.getByState(SumoPlayer.SumoState.FIGHTING).size() + sumoEvent.getByState(SumoPlayer.SumoState.PREPARING).size();
					board.add(" &fPlayers: &d" + playingSumo + "/" + event.getLimit());


					int countdown = sumoEvent.getCountdownTask().getTimeUntilStart();

					if (countdown > 0 && countdown <= 60) {
						board.add(" &fStarting: &d" + countdown + "s");
					}

					if (sumoEvent.getPlayer(player) != null) {
						SumoPlayer sumoPlayer = sumoEvent.getPlayer(player);
						board.add(" &fState: &d" + org.apache.commons.lang.StringUtils.capitalize(sumoPlayer.getState().name().toLowerCase()));
					}

					if (sumoEvent.getFighting().size() > 0) {
						StringJoiner nameJoiner = new StringJoiner(" &fvs &d");
						StringJoiner pingJoiner = new StringJoiner(" &fvs &d ");

						for (String fighterName : sumoEvent.getFighting()) {
							nameJoiner.add(fighterName);

							Player fighter = Bukkit.getPlayer(fighterName);

							if (fighter != null) {
								pingJoiner.add("&d" + VituzAPI.getPing(fighter) + " ms");
							}
						}

						board.add("");
						board.add("&d" + nameJoiner.toString());
						board.add(pingJoiner.toString());
					}
				}

				board.add("&5&7&m----------------------");
			}
		} else {
			if(StaffModeHandler.isInStaffMode(player)
					|| AppleHandler.isActive(player)
					|| EnderpearlHandler.isActive(player)
					|| GappleHandler.isActive(player)
					|| HomeHandler.isActive(player)
					|| StuckHandler.isActive(player)
					|| SpawnTagHandler.isActive(player)
					|| ClassWarmupHandler.isActive(player)
					|| LogoutHandler.teleporting.containsKey(player)
					|| GameHandler.getEventFaction() instanceof KothFaction
					|| ArmorClassHandler.getEquippedClass(player) != null
					|| EOTWHandler.getRunnable() != null) {
				board.add("&1&7&m----------------------");
			}

			if(StaffModeHandler.isInStaffMode(player)) {
				board.add("&5&lStaff Mode");

				double tps = Bukkit.spigot().getTPS()[0];

				board.add(" §7* §fTPS: §d" + JavaUtils.format(tps, 2));

				if(VanishHandler.vanishedPlayers.contains(player.getUniqueId())) {
					board.add(" &7* &fVanish: &aEnabled");
				} else {
					board.add(" &7* &fVanish: &cDisabled");
				}

				if(player.getGameMode() == GameMode.CREATIVE) {
					board.add(" &7* &fGamemode: &aCreative");
				} else if(player.getGameMode() == GameMode.SURVIVAL) {
					board.add(" &7* &fGamemode:Survival");
				} else if(player.getGameMode() == GameMode.ADVENTURE) {
					board.add(" &7* &fGamemode: &eAdventure");
				}

				board.add(" &6* &fOnline: &d" + String.valueOf(Bukkit.getOnlinePlayers().size()));
				board.add("&2&7&m----------------------");
			}

			if(AppleHandler.isActive(player)) {
				board.add("&6&lApple: &f" + StringUtils.getRemaining(AppleHandler.getMillisecondsLeft(player), true));
			}

			if(EnderpearlHandler.isActive(player)) {
				board.add("&e&lEnderpearl: &f" + StringUtils.getRemaining(EnderpearlHandler.getMillisecondsLeft(player), true));
			}

			if(StuckHandler.isActive(player)) {
				board.add("&4&lStuck: &f" + StringUtils.getRemaining(StuckHandler.getMillisecondsLeft(player), false));
			}

			if(ClassWarmupHandler.isActive(player)) {
				board.add("&b&lClass Warmup: &f" + StringUtils.getRemaining(ClassWarmupHandler.getMillisecondsLeft(player), true));
			}

			if(SpawnTagHandler.isActive(player)) {
				board.add("&c&lSpawn Tag: &f" + StringUtils.getRemaining(SpawnTagHandler.getMillisecondsLeft(player), false));
			}

			if(GappleHandler.isActive(player)) {
				board.add("&6&lGapple: &f" + StringUtils.getRemaining(GappleHandler.getMillisecondsLeft(player), false));
			}

			if(HomeHandler.isActive(player)) {
				board.add("&9&lHome: &F" + StringUtils.getRemaining(HomeHandler.getMillisecondsLeft(player), true));
			}

			if(LogoutHandler.teleporting.containsKey(player)) {
				board.add("&4&lLogout: &f" + StringUtils.getRemaining(LogoutHandler.getMillisecondsLeft(player), true));
			}

			if(EOTWHandler.getRunnable() != null) {
				long remaining = EOTWRunnable.getStartingTime();

				if(remaining > 0L) {
					board.add("&c&lEOTW&c starts in&l " + StringUtils.getRemaining(remaining, false));
				} else {
					if((remaining = EOTWRunnable.getCappableTime()) > 0L) {
						board.add("&c&lEOTW&c cappable in&l " + StringUtils.getRemaining(remaining, false));
					}
				}
			}

			if(ArmorClassHandler.getEquippedClass(player) instanceof Bard) {
				Bard bardClass = (Bard) ArmorClassHandler.getEquippedClass(player);

				long effectCooldown = bardClass.getRemainingBuffDelay(player);

				if(effectCooldown > 0) {
					board.add("&a&lBard Effect: &f" + StringUtils.getRemaining(effectCooldown, false));
				}

				board.add("&b&lBard Energy: &f" + HCFUtils.getBardFormat(bardClass.getEnergyMillis(player), true, false) + ".0");
			} else if(ArmorClassHandler.getEquippedClass(player) instanceof Archer) {
				board.add("&6&lClass: &fArcher");

				long aSpeed = Archer.speed_cooldowns.get(player.getUniqueId());
				long aSpeedCooldown = aSpeed == Archer.speed_cooldowns.getNoEntryValue() ? -1L : aSpeed - System.currentTimeMillis();

				if(aSpeedCooldown > 0L) {
					board.add("&e&lSpeed: &f" + StringUtils.getRemaining(aSpeedCooldown, true));
				}

				long aJump = Archer.jump_cooldowns.get(player.getUniqueId());
				long aJumpCooldown = aJump == Archer.jump_cooldowns.getNoEntryValue() ? -1L : aJump - System.currentTimeMillis();

				if(aJumpCooldown > 0L) {
					board.add("&e&lJump: &f" + StringUtils.getRemaining(aJumpCooldown, true));
				}
			} else if(ArmorClassHandler.getEquippedClass(player) instanceof Rogue) {
				board.add("&6&lClass: &fRogue");

				long rSpeed = Rogue.speed_cooldowns.get(player.getUniqueId());
				long rSpeedCooldown = rSpeed == Rogue.speed_cooldowns.getNoEntryValue() ? -1L : rSpeed - System.currentTimeMillis();

				if(rSpeedCooldown > 0L) {
					board.add("&e&lSpeed: &f" + StringUtils.getRemaining(rSpeedCooldown, true));
				}

				long rJump = Rogue.jump_cooldowns.get(player.getUniqueId());
				long rJumpCooldown = rJump == Rogue.jump_cooldowns.getNoEntryValue() ? -1L : rJump - System.currentTimeMillis();

				if(rJumpCooldown > 0L) {
					board.add("&e&lJump: &f" + StringUtils.getRemaining(rJumpCooldown, true));
				}
			} else if(ArmorClassHandler.getEquippedClass(player) instanceof Miner) {
				board.add("&6&lClass: &fMiner");

			}

			if(!GameHandler.getGameHandler().getActiveKoths().isEmpty()) {
				for(KothFaction faction : GameHandler.getGameHandler().getActiveKoths()) {
					if(faction != null && faction.getCaptureZone().isActive()) {
						board.add("&9&l" + faction.getName() + ": &f" + StringUtils.getRemaining(faction.getCaptureZone().getRemainingCaptureMillis(), false));
					}
				}
			}

			/*for(KothFaction faction : GameHandler.getGameHandler().getActiveKoths()) {
				if(faction != null && faction.getCaptureZone().isActive()) {
					board.add("&9&l" + faction.getName() + ": &f" + StringUtils.getRemaining(GameHandler.getRemaining(), false));
				}
			}*/

			/*if(GameHandler.getGameHandler() != null) {
				if(GameHandler.getEventFaction() instanceof KothFaction) {
					board.add(GameHandler.getEventFaction().getScoreboardName() + ": &f" + StringUtils.getRemaining(GameHandler.getRemaining(), false));
				}
			}*/

			if(AppleHandler.isActive(player)
					|| EnderpearlHandler.isActive(player)
					|| HomeHandler.isActive(player)
					|| StuckHandler.isActive(player)
					|| SpawnTagHandler.isActive(player)
					|| ClassWarmupHandler.isActive(player)
					|| GappleHandler.isActive(player)
					|| LogoutHandler.teleporting.containsKey(player)
					|| !GameHandler.getGameHandler().getActiveKoths().isEmpty()
					|| ArmorClassHandler.getEquippedClass(player) != null
					|| EOTWHandler.getRunnable() != null) {
				board.add("&3&7&m----------------------");
			}
		}

		return board.stream().map(Color::translate).toArray(String[]::new);
	}

	private void add(List list, String text) {
		list.add(Color.translate(text));
	}
}
