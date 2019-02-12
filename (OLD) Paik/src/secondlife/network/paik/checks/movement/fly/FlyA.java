package secondlife.network.paik.checks.movement.fly;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.paik.Paik;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.data.PlayerStatsHandler;
import secondlife.network.paik.handlers.events.PlayerMoveByBlockEvent;
import secondlife.network.paik.utils.CheatUtils;
import secondlife.network.paik.utils.Handler;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlyA extends Handler {

	public static Map<UUID, Map.Entry<Long, Double>> upTicks;
	public static Map<UUID, Long> onGround;
	
	public FlyA(Paik plugin) {
		super(plugin);

		upTicks = new HashMap<UUID, Map.Entry<Long, Double>>();
		onGround = new HashMap<UUID, Long>();
	}
	
	public static void handleFly(Player player, PlayerStats stats, PlayerMoveByBlockEvent event) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.flyA")) {
			if(ServerUtils.isServerLagging()) {
				if(!CheatHandler.ignore.isEmpty()) {
					CheatHandler.ignore.clear();
				}
				if(!upTicks.isEmpty()) {
					upTicks.clear();
				}
				if(!onGround.isEmpty()) {
					onGround.clear();
				}
				return;
			}

			if(player.getPing() > 200
					|| player.getAllowFlight()
					|| player.getVehicle() != null
					|| player.isOnGround())
				return;

			if(CheatHandler.ignore.containsKey(player.getUniqueId()) && System.currentTimeMillis() < CheatHandler.ignore.get(player.getUniqueId())) return;

			Location from = event.getFrom().clone();
			Location to = event.getTo().clone();

			from.setX(0.0D);
			from.setZ(0.0D);
			to.setX(0.0D);
			to.setZ(0.0D);

			long timeMillis = System.currentTimeMillis();
			double blocks = 0.0D;

			if (upTicks.containsKey(player.getUniqueId())) {
				timeMillis = upTicks.get(player.getUniqueId()).getKey().longValue();
				blocks = upTicks.get(player.getUniqueId()).getValue().doubleValue();
			}

			long timeDifference = System.currentTimeMillis() - timeMillis;
			double distance = from.toVector().subtract(to.toVector()).length();

			if (distance > 0.0D) {
				blocks += distance;
			}

			if (CheatUtils.blocksNear(player)) {
				blocks = 0.0D;
			}

			Location a = player.getLocation().subtract(0.0D, 1.0D, 0.0D);

			if (CheatUtils.blocksNear(a)) {
				blocks = 0.0D;
			}

			double trigger = 0.5D;

			if (player.hasPotionEffect(PotionEffectType.JUMP)) {
				for (PotionEffect effect : player.getActivePotionEffects()) {
					if (effect.getType().equals(PotionEffectType.JUMP)) {
						int level = effect.getAmplifier() + 1;
						trigger += Math.pow(level + 4.2D, 2.0D) / 16.0D;
						break;
					}
				}
			}

			if (blocks > trigger) {
				if (timeDifference > 125L) {
					addOne(player);

					if(stats.getFlyAVL() > 3) {
						stats.setFlyAVL(0);
						Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Fly A", player.getPing(), Bukkit.spigot().getTPS()[0]));
					}
					timeMillis = System.currentTimeMillis();
				}
			} else {
				timeMillis = System.currentTimeMillis();
			}
			upTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Long.valueOf(timeMillis), Double.valueOf(blocks)));
		}
	}
	
	public static void addOne(Player player) {
		PlayerStats stats = PlayerStatsHandler.getStats(player);
		
		stats.setFlyAVL(stats.getFlyAVL() + 1);
	}
	
	public static void removeOne(Player player) {
		PlayerStats stats = PlayerStatsHandler.getStats(player);
		
		if(stats.getFlyAVL() > 0) {
			stats.setFlyAVL(stats.getFlyAVL() - 1);
		}
	}
}