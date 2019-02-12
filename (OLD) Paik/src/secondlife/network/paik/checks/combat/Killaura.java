package secondlife.network.paik.checks.combat;

import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.paik.Paik;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.utils.CheatUtils;
import secondlife.network.paik.utils.Handler;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.ServerUtils;
import secondlife.network.paik.utils.file.ConfigFile;

import java.util.UUID;

public class Killaura extends Handler {

	public static EntityPlayer npc;
	
	public Killaura(Paik plugin) {
		super(plugin);

		npc = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(),
			  new GameProfile(UUID.randomUUID(), UUID.randomUUID().toString().substring(0, 14).replace("-", "")),
			  new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
		npc.viewDistance = 1;
		npc.setSprinting(true);
		npc.onGround = true;
		npc.ping = CheatUtils.random(30, 100);
		npc.expLevel = CheatUtils.random(3, 100);
	}
	
	public static void handleKillaura(Player player, PlayerStats stats) {
		if(!ConfigFile.configuration.getBoolean("enabled") || ServerUtils.isServerLagging()) return;
		
		if(ConfigFile.configuration.getBoolean("checks.killaura.packet")) {
			handleKillauraPacket(player, stats);
		}

		if(ConfigFile.configuration.getBoolean("checks.killaura.dead")) {
			handleKillauraDead(player, stats);
		}
		
		if(ConfigFile.configuration.getBoolean("checks.inventory.killaura")) {
			handleKillauraInventory(player, stats);
		}
		
		if(ConfigFile.configuration.getBoolean("checks.killaura.bot")) {
			handleTeleportBot(player, stats);
		}
	}

	public static void handleKillauraBotCheck(Player player, int entityID) {
		if(entityID == npc.getBukkitEntity().getEntityId()) {
			Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "KillAura (Bot)", player.getPing(), Bukkit.spigot().getTPS()[0]));

			if(ConfigFile.configuration.getBoolean("autobans") || !player.hasPermission("secondlife.staff")) return;

			CheatHandler.handleBan(player);
		}
	}

	public static void handleTeleportBot(Player player, PlayerStats stats) {
		if(Bukkit.getOnlinePlayers().size() > 100 || player.getPing() > 300 || player.isDead()) return;

		stats.setHits(stats.getHits() + 1);

		if(stats.getHits() > 20) {
			stats.setHits(0);
			teleportBot(player);
		}
	}

	public static void handleKillauraPacket(Player player, PlayerStats stats) {
		if(player.getPing() > 300) return;

		if(stats.getLastArmPacket() == 0 || stats.getLastUseEntityPacket() == 0) return;

		if(stats.getNoSwingDamageVL() > 3) {
			stats.setNoSwingDamageVL(0);
			Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "KillAura (Swing)", player.getPing(), Bukkit.spigot().getTPS()[0]));
		}

		if(stats.getLastUseEntityPacket() - stats.getLastArmPacket() > 500) {
			stats.setNoSwingDamageVL(stats.getNoSwingDamageVL() + 1);
		} else {
			stats.setNoSwingDamageVL(0);
		}
	}

	public static void handleKillauraDead(Player player, PlayerStats stats) {
		if(stats.getHitsWhileDead() > 3) {
			stats.setHitsWhileDead(0);
			Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "KillAura (Dead)", player.getPing(), Bukkit.spigot().getTPS()[0]));
		}

		if(player.isDead()) {
			stats.setHitsWhileDead(stats.getHitsWhileDead() + 1);
		} else {
			stats.setHitsWhileDead(0);
		}
	}

	public static void handleKillauraInventory(Player player, PlayerStats stats) {
		if(player.getGameMode() != GameMode.SURVIVAL || System.currentTimeMillis() - stats.getJoined() < 1500) return;

		if(stats.getHitsWhileInventoryOpen() > 3) {
			stats.setHitsWhileInventoryOpen(0);
			Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Inventory Killaura", player.getPing(), Bukkit.spigot().getTPS()[0]));
		}

		if(!stats.isInventoryOpen()) return;

		stats.setHitsWhileInventoryOpen(stats.getHitsWhileInventoryOpen() + 1);
	}

	public static void handleKillauraAngle(Player player, PlayerStats stats, float yaw, float pitch) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.killaura.angle")) {
			if(stats.getAngle() > 20) {
				stats.setAngle(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Killaura (Angle)", player.getPing(), Bukkit.spigot().getTPS()[0]));
			}

			float diff = Math.abs((stats.getLastYaw() - yaw) + (stats.getLastPitch() - pitch));

			if(diff < 35) {
				if(stats.getAngle() > 0) {
					stats.setAngle(stats.getAngle() - 1);
					//Message.sendMessage(Color.translate("&cVerbose -1"));
				}
			} else if(diff >= 35 && diff < 500 && System.currentTimeMillis() - stats.getJoined() > 1500 && System.currentTimeMillis() - stats.getLastUseEntityPacket() < 1000) {
				//Message.sendMessage(Color.translate("&aVerbose +1"));
				stats.setAngle(stats.getAngle() + 1);
			}

			stats.setLastYaw(yaw);
			stats.setLastPitch(pitch);
		}
	}

	public static void handleKillauraWall(Player player, PlayerStats stats) {
		if(ConfigFile.configuration.getBoolean("enabled") && ConfigFile.configuration.getBoolean("checks.killaura.wall")) {
			if(stats.getSwingAngle() > 7) {
				stats.setSwingAngle(0);
				Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "Killaura (Wall)", player.getPing(), Bukkit.spigot().getTPS()[0]));
			}

			if(stats.getLastEntity() == null || player.hasLineOfSight(stats.getLastEntity())) return;

			Block block = player.getTargetBlock(null, 4);

			if(block == null || block.getType() == Material.AIR || !block.getType().isSolid()) return;

			if(System.currentTimeMillis() - stats.getLastBlockDigPacket() > 1000) {
				if(System.currentTimeMillis() - stats.getLastUseEntityPacket() < 100) {
					stats.setSwingAngle(stats.getSwingAngle() + 1);
					//Message.sendMessage(Color.translate("&aVerbose +1"));
				}
			} else {
				stats.setSwingAngle(0);
				//Message.sendMessage(Color.translate("&cVerbose 0"));
			}
		}
	}

	public static void teleportBot(Player player) {
		if(player.getLocation().getPitch() < -20.0) return;

		npc.setInvisible(false);
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		Location loc = player.getLocation();
		npc.setLocation(loc.getX(), loc.getY() + 4.5, loc.getZ(), loc.getPitch(), loc.getYaw());
		connection.sendPacket(new PacketPlayOutPlayerInfo().addPlayer(npc));
		connection.sendPacket(new PacketPlayOutNamedEntitySpawn((EntityHuman) npc));

		new BukkitRunnable() {
			public void run() {
				npc.setInvisible(true);
				connection.sendPacket(new PacketPlayOutPlayerInfo().removePlayer(npc));
				connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getBukkitEntity().getEntityId()));
			}
		}.runTaskLater(Paik.getInstance(), 2L);
	}
}
