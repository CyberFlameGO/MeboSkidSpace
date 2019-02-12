package secondlife.network.paik.checks.movement;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MobEffectList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Speed extends Handler {
	
	public static ArrayList<Double> speeds;
	public static ArrayList<Double> speeds1;
	public static ArrayList<Double> speeds2;
	public static ArrayList<Double> speeds3;
	public static ArrayList<Double> speeds4;
	public static ArrayList<Double> speeds5;
	
	public Speed(Paik plugin) {
		super(plugin);

		speeds = new ArrayList<Double>();
		speeds1 = new ArrayList<Double>();
		speeds2 = new ArrayList<Double>();
		speeds3 = new ArrayList<Double>();
		speeds4 = new ArrayList<Double>();
		speeds5 = new ArrayList<Double>();
		
		// ARTIX - MINEMEN
		speeds.add(0.5274);
		speeds1.add(0.5529);
		speeds2.add(0.5783);
		speeds3.add(0.6038);
		speeds4.add(0.6293);
		speeds5.add(0.6548);
		
		// ARTIX - SLOWHOP
		speeds.add(0.2870);
		speeds1.add(0.3444);
	}

	public static void handleSpeed(Player player, PlayerStats stats, PlayerMoveByBlockEvent event) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.speed")) {
			if(ServerUtils.isServerLagging()) {
				if(!CheatHandler.ignore.isEmpty()) {
					CheatHandler.ignore.clear();
				}
				if(!CheatHandler.ignoreJump.isEmpty()) {
					CheatHandler.ignoreJump.clear();
				}
				return;
			}

			if(player.getWalkSpeed() != 0.2F
					|| player.getGameMode() == GameMode.CREATIVE
					|| player.getAllowFlight()
					|| player.getVehicle() != null
					|| CheatUtils.isUnderBlock(player)
					|| CheatUtils.isInLiquid(player)
					|| CheatUtils.isOnHalfBlocks(player)
					|| !CheatUtils.isOnSolidBlock(player)
					|| !CheatUtils.isOnSolidBlocks(player)
					|| CheatUtils.isOnSnow(player)
					|| CheatUtils.isInAir(player))
				return;

			if(CheatHandler.ignore.containsKey(player.getUniqueId()) && System.currentTimeMillis() < CheatHandler.ignore.get(player.getUniqueId())) return;

			if(CheatUtils.isOnIce(player)) {
				CheatHandler.ignore.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
				return;
			}

			for(PotionEffect potionEffect : player.getActivePotionEffects()) {
				if((potionEffect.getType().equals(PotionEffectType.SPEED)) && (potionEffect.getAmplifier() >= 5)) return;
			}

			Location from = event.getFrom().clone();
			Location to = event.getTo().clone();

			if(to.getX() == from.getX() || to.getZ() == from.getZ()) return;

			from.setY(0);
			to.setY(0);

			Double distance = Double.valueOf(from.distance(to));

			if(stats.getSpeedFast() > 1) {
				stats.setSpeedFast(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Speed (Fast)", player.getPing(), Bukkit.spigot().getTPS()[0]));
				return;
			}

			if(distance > 4.25) {
				stats.setSpeedFast(stats.getSpeedFast() + 1);
				return;
			}

			if(player.getPing() > 200) {
				removeJumpOne(player);
				removeOne(player);
				return;
			}

			DecimalFormat dc = new DecimalFormat("#.####");

		/*if(player.isOnGround()) {
			Message.sendMessage("�c" + player.getName() + " GROUND " + String.valueOf(dc.format(distance)));
		} else {
			Message.sendMessage("�a" + player.getName() + " AIR " + String.valueOf(dc.format(distance)));
		}*/

			if(stats.getSpeedJumpVL() > 20) {
				stats.setSpeedJumpVL(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Speed (Ground)", player.getPing(), Bukkit.spigot().getTPS()[0]));
				return;
			}

			if(stats.getSpeedVL() > 10) {
				stats.setSpeedVL(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Speed (Hop / Air)", player.getPing(), Bukkit.spigot().getTPS()[0]));
				return;
			}

			if(stats.getSpeedOtherVL() > 10) {
				stats.setSpeedOtherVL(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Speed (Hop / Air)", player.getPing(), Bukkit.spigot().getTPS()[0]));
				return;
			}

			if(stats.getSpeedSlowhopVL() > 10) {
				stats.setSpeedSlowhopVL(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Speed (Slow / Packet)", player.getPing(), Bukkit.spigot().getTPS()[0]));
				return;
			}

		/*if(stats.getSpritingandblockingVL() > 5) {
			stats.setSpritingandblockingVL(0);
			Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Speed (AutoBlock)", player.getPing(), Bukkit.spigot().getTPS()[0]));
			return;
		}*/

			if(player.isSprinting()) {
				if(player.getFoodLevel() <= 5) {
					Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Speed (Fake Sprint)", player.getPing(), Bukkit.spigot().getTPS()[0]));
					return;
				}
			/*if(player.isBlocking()) {
				stats.setSpritingandblockingVL(stats.getSpritingandblockingVL() + 1);
				return;
			} else {
				if(stats.getSpritingandblockingVL() > 0) {
					stats.setSpritingandblockingVL(stats.getSpritingandblockingVL() - 1);
				}
			}*/
			}

		/*if(stats.getSpeedSneak() > 10) {
			stats.setSpeedSneak(0);
			Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Speed (Sneak)", player.getPing(), Bukkit.spigot().getTPS()[0]));
			return;
		}*/

			if(player.isOnGround() && from.getY() == to.getY()) {
				if((!CheatHandler.ignoreJump.containsKey(player.getUniqueId())) || (CheatHandler.ignoreJump.containsKey(player.getUniqueId()) && System.currentTimeMillis() > CheatHandler.ignoreJump.get(player.getUniqueId()))) {
					if(isGroundSpeeding(player, distance)) {
						stats.setSpeedJumpVL(stats.getSpeedJumpVL() + 1);
						return;
					}
				}
			}

		/*if(player.isSneaking()) {
			if(this.isSneakSpeeding(player, distance)) {
				stats.setSpeedSneak(stats.getSpeedSneak() + 1);
				return;
			}
		} else {
			stats.setSpeedSneak(0);
		}*/

			if(!player.isOnGround() && isAirSpeeding(player, distance)) {
				stats.setSpeedVL(stats.getSpeedVL() + 1);
				return;
			}

			if(player.isOnGround() && isOtherSpeeding(player, distance)) {
				stats.setSpeedOtherVL(stats.getSpeedOtherVL() + 1);
				return;
			}

			if(isBypassingSpeeding(player, distance) || isSlowHopSpeeding(player, distance)) {
				stats.setSpeedSlowhopVL(stats.getSpeedSlowhopVL() + 1);
				return;
			}
		}
	}
	
	public static void removeOne(Player player) {
		PlayerStats stats = PlayerStatsHandler.getStats(player);
		
		if(stats.getSpeedVL() > 0) {
			stats.setSpeedVL(stats.getSpeedVL() - 1);
		}
		
		if(stats.getSpeedOtherVL() > 0) {
			stats.setSpeedOtherVL(stats.getSpeedOtherVL() - 1);
		}
		
		if(stats.getSpeedSlowhopVL() > 0) {
			stats.setSpeedSlowhopVL(stats.getSpeedSlowhopVL() - 1);
		}
		
		/*if(stats.getSpeedSneak() > 0) {
			stats.setSpeedSneak(stats.getSpeedSneak() - 1);
		}*/
	}
	
	public static void removeJumpOne(Player player) {
		PlayerStats stats = PlayerStatsHandler.getStats(player);
		
		stats.setSpeedJumpVL(0);
	}
	
	public static boolean isGroundSpeeding(Player player, double distance) {
		EntityPlayer mcPlayer = ((CraftPlayer) player).getHandle();
		boolean hasSpeed = mcPlayer.hasEffect(MobEffectList.FASTER_MOVEMENT);
		
		double limit = 10;
		
		if(hasSpeed) {
			//Message.sendMessage(String.valueOf(player.getName() + " " + mcPlayer.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier()));
			switch(mcPlayer.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier()) {
			case 0:
				limit = 0.3467;
				break;
			case 1:
				limit = 0.4029;
				break;
			case 2:
				limit = 0.4590;
				break;
			case 3:
				limit = 0.5151;
				break;
			case 4:
				limit = 0.5712;
				break;
			}
		} else {
			limit = 0.2906;
		}
		
		if(distance > limit) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isBypassingSpeeding(Player player, double distance) {
		double limit = 10;
		double limit2 = 10;
		
		DecimalFormat dc = new DecimalFormat("#.####");
		
		Double moved = Double.valueOf(dc.format(distance).replaceAll(",", "."));
		
		EntityPlayer mcPlayer = ((CraftPlayer) player).getHandle();
		boolean hasSpeed = mcPlayer.hasEffect(MobEffectList.FASTER_MOVEMENT);
		
		if(hasSpeed) {
			//Message.sendMessage(String.valueOf(player.getName() + " " + mcPlayer.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier()));
			switch(mcPlayer.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier()) {
			case 0:
				if(speeds1.contains(moved)) {
					limit = (speeds1.get((int) moved.doubleValue()) - 0.0001);
					limit2 = (speeds1.get((int) moved.doubleValue()) + 0.0001);
				}
				break;
			case 1:
				if(speeds2.contains(moved)) {
					limit = (speeds2.get((int) moved.doubleValue()) - 0.0001);
					limit2 = (speeds2.get((int) moved.doubleValue()) + 0.0001);
				}
				break;
			case 2:
				if(speeds3.contains(moved)) { 
					limit = (speeds3.get((int) moved.doubleValue()) - 0.0001);
					limit2 = (speeds3.get((int) moved.doubleValue()) + 0.0001);
				}
				break;
			case 3:
				if(speeds4.contains(moved)) {
					limit = (speeds4.get((int) moved.doubleValue()) - 0.0001);
					limit2 = (speeds4.get((int) moved.doubleValue()) + 0.0001);
				}
				break;
			case 4:
				if(speeds5.contains(moved)) {
					limit = (speeds5.get((int) moved.doubleValue()) - 0.0001);
					limit2 = (speeds5.get((int) moved.doubleValue()) + 0.0001);
				}
				break;
			}
		} else {
			if(speeds.contains(moved)) {
				limit = (speeds.get((int) moved.doubleValue()) - 0.0001);
				limit2 = (speeds.get((int) moved.doubleValue()) + 0.0001);
			}
		}
		
		if((distance > limit) && (distance < limit2)) {
			return true;
		}
		
		return false;
	}
	
	/*public boolean isSneakSpeeding(Player player, double distance) {
		double limit = 10;
		
		EntityPlayer mcPlayer = ((CraftPlayer) player).getHandle();
		boolean hasSpeed = mcPlayer.hasEffect(MobEffectList.FASTER_MOVEMENT);
		
		if(hasSpeed) {
			//Message.sendMessage(String.valueOf(player.getName() + " " + mcPlayer.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier()));
			switch(mcPlayer.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier()) {
			case 0:
				limit = 0.0877;
				break;
			case 1:
				limit = 0.1007;
				break;
			case 2:
				limit = 0.1036;
				break;
			case 3:
				limit = 0.1266;
				break;
			case 4:
				limit = 0.1395;
				break;
			}
		} else {
			limit = 0.0748;
		}
		
		if(distance > limit) {
			return true;
		}
		
		return false;
	}*/
	
	
	public static boolean isAirSpeeding(Player player, double distance) {
		double limit = 10;
		
		EntityPlayer mcPlayer = ((CraftPlayer) player).getHandle();
		boolean hasSpeed = mcPlayer.hasEffect(MobEffectList.FASTER_MOVEMENT);
		
		if(hasSpeed) {
			//Message.sendMessage(String.valueOf(player.getName() + " " + mcPlayer.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier()));
			switch(mcPlayer.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier()) {
			case 0:
				limit = 0.3869;
				break;
			case 1:
				limit = 0.4042;
				break;
			case 2:
				limit = 0.4215;
				break;
			case 3:
				limit = 0.4387;
				break;
			case 4:
				limit = 0.4513;
				break;
			}
		} else {
			limit = 0.3696;
		}
		
		if(distance > limit) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isOtherSpeeding(Player player, double distance) {
		double limit = 10;
		
		EntityPlayer mcPlayer = ((CraftPlayer) player).getHandle();
		boolean hasSpeed = mcPlayer.hasEffect(MobEffectList.FASTER_MOVEMENT);
		
		if(hasSpeed) {
			//Message.sendMessage(String.valueOf(player.getName() + " " + mcPlayer.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier()));
			switch(mcPlayer.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier()) {
			case 0:
				limit = 0.6538;
				break;
			case 1:
				limit = 0.6854;
				break;
			case 2:
				limit = 0.7170;
				break;
			case 3:
				limit = 0.7485;
				break;
			case 4:
				limit = 0.7801;
				break;
			}
		} else {
			limit = 0.6222;
		}
		
		if(distance > limit) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isSlowHopSpeeding(Player player, double distance) {
		double limit = 10;
		double limit2 = 10;
		
		EntityPlayer mcPlayer = ((CraftPlayer) player).getHandle();
		boolean hasSpeed = mcPlayer.hasEffect(MobEffectList.FASTER_MOVEMENT);
		
		if(hasSpeed) {
			//Message.sendMessage(String.valueOf(player.getName() + " " + mcPlayer.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier()));
			switch(mcPlayer.getEffect(MobEffectList.FASTER_MOVEMENT).getAmplifier()) {
			case 0:
				limit = 0.3443;
				limit2 = 0.3445;
				break;
			}
		} else {
			limit = 0.2869;
			limit2 = 0.2871;
		}
		
		if((distance > limit) && (distance < limit2)) {
			return true;
		}
		
		return false;
	}
	
	/*public boolean isMinemenSpeeding(Player player, double distance) {
		double limit = 10;
		double limit2 = 10;
		
		if(player.hasPotionEffect(PotionEffectType.SPEED)) {
			for(PotionEffect potionEffect : player.getActivePotionEffects()) {
				switch(potionEffect.getAmplifier()) {
				case 0:
					limit = 0.5528;
					limit2 = 0.5530;
					break;
				case 1:
					limit = 0.5782;
					limit2 = 0.5784;
					break;
				case 2:
					limit = 0.6037;
					limit2 = 0.6039;
					break;
				case 3:
					limit = 0.6292;
					limit2 = 0.6294;
					break;
				case 4:
					limit = 0.6547;
					limit2 = 0.6549;
					break;
				}
			}
		} else {
			limit = 0.5273;
			limit2 = 0.5275;
		}
		
		if((distance > limit) && (distance < limit2)) {
			return true;
		}
		
		return false;
	}*/
}
