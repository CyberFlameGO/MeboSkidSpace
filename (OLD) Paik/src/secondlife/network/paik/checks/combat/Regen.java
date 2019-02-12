package secondlife.network.paik.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class Regen {

	public static void handleRegen(Player player, PlayerStats stats) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.regen")) {
			if(ServerUtils.isServerLagging()) return;

			if(player.getPing() > 250
					|| player.hasPotionEffect(PotionEffectType.REGENERATION)
					|| player.hasPotionEffect(PotionEffectType.HEAL))
				return;

			long now = System.currentTimeMillis();

			if(stats.getRegenVL() > 2) {
				stats.setRegenVL(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Regen (Normal)", player.getPing(), Bukkit.spigot().getTPS()[0]));
			}

			if (now - stats.getLastRegen() < 1000L) {
				stats.setRegenVL(stats.getRegenVL() + 1);
			} else {
				stats.setLastRegen(now);
				if(stats.getRegenVL() > 0) {
					stats.setRegenVL(stats.getRegenVL() - 1);
				}
			}
		}
	}
}
