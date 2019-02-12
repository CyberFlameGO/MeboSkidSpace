package secondlife.network.paik.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.paik.Paik;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.data.PlayerStatsHandler;
import secondlife.network.paik.utils.Handler;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class Timer extends Handler {

	public Timer(Paik plugin) {
		super(plugin);

		this.getPackets();
	}

	public static void handleTimerFlying(Player player, PlayerStats stats) {
		if(!ConfigFile.configuration.getBoolean("enabled")) return;
		if(!ConfigFile.configuration.getBoolean("checks.timer.standing")) return;

		if(ServerUtils.isServerLagging()) return;

		if(CheatHandler.ignore.containsKey(player.getUniqueId()) && System.currentTimeMillis() < CheatHandler.ignore.get(player.getUniqueId())) return;

		if(player.getPing() > 175
				|| player.getAllowFlight()
				|| player.getGameMode() == GameMode.CREATIVE)
			return;

		if(System.currentTimeMillis() - stats.getJoined() < 1500) return;

		stats.setFlyingPackets(stats.getFlyingPackets() + 1);
	}

	public static void handleTimerPosition(Player player, PlayerStats stats) {
		if(!ConfigFile.configuration.getBoolean("enabled")) return;
		if(!ConfigFile.configuration.getBoolean("checks.timer.moving")) return;

		if(ServerUtils.isServerLagging()) return;

		if(player.getPing() > 250
				|| player.getAllowFlight()
				|| player.getGameMode() == GameMode.CREATIVE)
			return;

		if(CheatHandler.ignore.containsKey(player.getUniqueId()) && System.currentTimeMillis() < CheatHandler.ignore.get(player.getUniqueId())) return;

		if(System.currentTimeMillis() - stats.getJoined() < 1500) return;

		stats.setPositionPackets(stats.getPositionPackets() + 1);
	}

	public static void handleTimerPositionLook(Player player, PlayerStats stats) {
		if(!ConfigFile.configuration.getBoolean("enabled")) return;
		if(!ConfigFile.configuration.getBoolean("checks.timer.looking")) return;

		if(ServerUtils.isServerLagging()) return;

		if(player.getPing() > 250
				|| player.getAllowFlight()
				|| player.getGameMode() == GameMode.CREATIVE)
			return;

		if(CheatHandler.ignore.containsKey(player.getUniqueId()) && System.currentTimeMillis() < CheatHandler.ignore.get(player.getUniqueId())) return;

		if(System.currentTimeMillis() - stats.getJoined() < 1500) return;

		stats.setPositionLookPackets(stats.getPositionLookPackets() + 1);
	}

	public void getPackets() {
		new BukkitRunnable() {
			public void run() {
				if(!ConfigFile.configuration.getBoolean("enabled")) return;
				if (ServerUtils.isServerLagging()) return;

				for (Player player : Bukkit.getOnlinePlayers()) {
					PlayerStats stats = PlayerStatsHandler.getStats(player);

					if (stats != null) {
						// FLYING PACKET
						if(ConfigFile.configuration.getBoolean("checks.timer.standing")) {
							int packets = stats.getFlyingPackets();

							if(stats.getTimerAVL() > 20) {
								stats.setTimerAVL(0);
								Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Timer (Standing)", player.getPing(), Bukkit.spigot().getTPS()[0]));
							}

							if(packets > 23) {
								stats.setTimerAVL(stats.getTimerAVL() + 6);
								//Message.sendMessage(Color.translate("&aVerbose: +6"));
							} else if(packets > 19 && packets <= 23) {
								stats.setTimerAVL(stats.getTimerAVL() + 3);
								//Message.sendMessage(Color.translate("&aVerbose: +3"));
							} else if(packets <= 19){
								if(stats.getTimerAVL() > 0) {
									stats.setTimerAVL(stats.getTimerAVL() - 1);
									//Message.sendMessage(Color.translate("&cVerbose: -1"));
								}
							}

							stats.setFlyingPackets(0);
						}

						// POSITION PACKET
						if(ConfigFile.configuration.getBoolean("checks.timer.moving")) {
							int packets = stats.getPositionPackets();

							if(stats.getTimerBVL() > 20) {
								stats.setTimerBVL(0);
								Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Timer (Moving)", player.getPing(), Bukkit.spigot().getTPS()[0]));
							}

							if(packets > 24) {
								stats.setTimerBVL(stats.getTimerBVL() + 6);
								//Message.sendMessage(Color.translate("&aVerbose: +6"));
							} else if(packets > 21 && packets <= 24) {
								stats.setTimerBVL(stats.getTimerBVL() + 3);
								//Message.sendMessage(Color.translate("&aVerbose: +3"));
							} else if(packets <= 21){
								if(stats.getTimerBVL() > 0) {
									stats.setTimerBVL(stats.getTimerBVL() - 1);
									//Message.sendMessage(Color.translate("&cVerbose: -1"));
								}
							}

							stats.setPositionPackets(0);
						}

						// POSITION LOOK & LOOK PACKET
						if(ConfigFile.configuration.getBoolean("checks.timer.looking")) {
							int packets = stats.getPositionLookPackets();

							if(stats.getTimerCVL() > 20) {
								stats.setTimerCVL(0);
								Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Timer (Moving & Looking)", player.getPing(), Bukkit.spigot().getTPS()[0]));
							}

							if(packets > 24) {
								stats.setTimerCVL(stats.getTimerCVL() + 6);
								//Message.sendMessage(Color.translate("&aVerbose: +6"));
							} else if(packets > 21 && packets <= 24) {
								stats.setTimerCVL(stats.getTimerCVL() + 3);
								//Message.sendMessage(Color.translate("&aVerbose: +3"));
							} else if(packets <= 21){
								if(stats.getTimerCVL() > 0) {
									stats.setTimerCVL(stats.getTimerCVL() - 1);
									//Message.sendMessage(Color.translate("&cVerbose: -1"));
								}
							}

							stats.setPositionLookPackets(0);
						}

						if(ConfigFile.configuration.getBoolean("checks.morepackets")) {
							if(stats.getMorePackets() > 50 && System.currentTimeMillis() - stats.getJoined() > 1500) {
								Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "MorePackets (Position)", player.getPing(), Bukkit.spigot().getTPS()[0]));
							}

							stats.setMorePackets(0);
						}
					}
				}
			}
		}.runTaskTimer(this.getInstance(), 20L, 20L);
	}
}
