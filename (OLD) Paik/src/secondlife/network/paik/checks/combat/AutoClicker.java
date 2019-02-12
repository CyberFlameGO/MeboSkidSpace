package secondlife.network.paik.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.paik.Paik;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.data.PlayerStatsHandler;
import secondlife.network.paik.utils.Handler;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AutoClicker extends Handler {
    
	public static ArrayList<String> butterfly;
	
	public AutoClicker(Paik plugin) {
		super(plugin);
		
		butterfly = new ArrayList<String>();
		
		butterfly.add("ghostclienterr");

		this.getClicks();
	}

	public static void handleAutoClickInteract(Player player, PlayerStats stats, Action action) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.autoclicker")) {
			if(ServerUtils.isServerLagging() || action != Action.LEFT_CLICK_AIR) return;

			handleAutoClick(player, stats);
			handleConstantCPS(player, stats);
			handleDoubleClick(player, stats);
		}
	}

	public void getClicks() {
		new BukkitRunnable() {
			public void run() {
				if(!ConfigFile.configuration.getBoolean("enabled")) return;
				if (ServerUtils.isServerLagging()) return;

				for(Player player : Bukkit.getOnlinePlayers()) {
					PlayerStats stats = PlayerStatsHandler.getStats(player);

					if(stats != null) {
						int cps = stats.getLeftClickCPS();

						if (cps >= 50 && ConfigFile.configuration.getBoolean("autobans")) {
							try {
								Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "AutoClicker &c[&4" + cps + " &cCPS]", player.getPing(), Bukkit.spigot().getTPS()[0]));
								CheatHandler.log(player, "WAS AUTOBANNED FOR HAVING MORE THAN 50 CPS [" + cps + " CPS] ", "AutoClicker", LocationUtils.getLocation(player), player.getPing(), new DecimalFormat("##.##").format(Bukkit.spigot().getTPS()[0]));
							} catch (IOException e) {
								e.printStackTrace();
							}
							CheatHandler.handleBan(player);
						} else if (cps >= 30) {
							stats.setAutoclickerVL(stats.getAutoclickerVL() + 1);
							Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "AutoClicker &c[&4" + cps + " &cCPS]", player.getPing(), Bukkit.spigot().getTPS()[0]));
						} else if (cps >= 27 && butterfly.contains(player.getUniqueId())) {
							Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "AutoClicker &c[&4" + cps + " &cCPS]", player.getPing(), Bukkit.spigot().getTPS()[0]));
						} else if (cps >= 20) {
							Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "AutoClicker &c[&4" + cps + " &cCPS]", player.getPing(), Bukkit.spigot().getTPS()[0]));
						} else if (cps >= 10 && cps < 30) {
							stats.setConstantCPS(stats.getConstantCPS() + 1);

							ItemStack item = player.getItemInHand();

							if(item != null && stats.getLastUseEntityPacket() < 2500) {
								if(item.getType() == Material.POTION || item.getType() == Material.ENDER_PEARL || item.getType() == Material.COOKED_BEEF || item.getType() == Material.BAKED_POTATO) {
									Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "AutoClicker (Item Swing)", player.getPing(), Bukkit.spigot().getTPS()[0]));
								}
							}
						} else {
							if(stats.getAutoclickerVL() > 0) {
								stats.setAutoclickerVL(stats.getAutoclickerVL() - 1);
							}
							stats.setConstantCPS(0);
						}

						stats.setLeftClickCPS(0);
					}
				}
			}
		}.runTaskTimer(this.getInstance(), 20L, 20L);
	}

	public static void handleDoubleClick(Player player, PlayerStats stats) {
		if(stats.getDoubleclick() > 25) {
			stats.setDoubleclick(0);
			Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "DoubleClick", player.getPing(), Bukkit.spigot().getTPS()[0]));
		}

		if(stats.getClick1() != 0) {
			stats.setClick2(System.currentTimeMillis());
			//Message.sendMessage("set click 2 " + time);
		}

		if(stats.getClick1() == 0) {
			stats.setClick1(System.currentTimeMillis());
			//Message.sendMessage("set click 1 " + time);
		}

		if(stats.getClick1() == 0 || stats.getClick2() == 0) return;

		long difference = (stats.getClick2() - stats.getClick1());

		if(difference <= 5) {
			if(!butterfly.contains(player.getUniqueId()) && player.getPing() < 200) {
				stats.setDoubleclick(stats.getDoubleclick() + 1);
			}
		} else {
			stats.setDoubleclick(0);
		}

		stats.setClick1(0);
		stats.setClick2(0);
	}

	public static void handleConstantCPS(Player player, PlayerStats stats) {
		if(ConfigFile.configuration.getBoolean("autobans") && stats.getAutoclickerVL() > 10) {
			try {
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "AutoClicker (Constant - 30+)", player.getPing(), Bukkit.spigot().getTPS()[0]));
				CheatHandler.log(player, "WAS AUTOBANNED FOR CONSTANT 30+ CPS", "AutoClicker", LocationUtils.getLocation(player), player.getPing(), new DecimalFormat("##.##").format(Bukkit.spigot().getTPS()[0]));
			} catch (IOException e) {
				e.printStackTrace();
			}
			CheatHandler.handleBan(player);
			return;
		}

		if(stats.getConstantCPS() > 25) {
			stats.setConstantCPS(0);
			Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "AutoClicker (Constant - 10-30)", player.getPing(), Bukkit.spigot().getTPS()[0]));
		}
	}

	public static void handleAutoClick(Player player, PlayerStats stats) {
		if(player.getPing() > 350
				|| !player.getLocation().getChunk().isLoaded()
				|| player.isDead()
				|| System.currentTimeMillis() - stats.getJoined() < 1000
				|| System.currentTimeMillis() - stats.getLastBlockBreak() < 500)
			return;

		stats.setLeftClickCPS(stats.getLeftClickCPS() + 1);
	}
}
