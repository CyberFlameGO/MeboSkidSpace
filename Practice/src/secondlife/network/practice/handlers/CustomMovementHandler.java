package secondlife.network.practice.handlers;

import club.minemen.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.events.oitc.OITCEvent;
import secondlife.network.practice.events.oitc.OITCPlayer;
import secondlife.network.practice.events.sumo.SumoEvent;
import secondlife.network.practice.events.sumo.SumoPlayer;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchState;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.BlockUtil;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.CustomLocation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class CustomMovementHandler implements MovementHandler {

	private final Practice plugin = Practice.getInstance();
	private static HashMap<Match, HashMap<UUID, CustomLocation>> parkourCheckpoints = new HashMap<>();

	@Override
	public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
		PracticeData playerData = PracticeData.getByName(player.getName());

		if (playerData == null) {
			this.plugin.getLogger().warning(player.getName() + "'s player data is null");
			return;
		}

		if (playerData.getPlayerState() == PlayerState.FIGHTING) {
			Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

			if(match == null) {
				return;
			}

			if (match.getKit().isSpleef() || match.getKit().isSumo()) {

				if (BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1)) {
					this.plugin.getMatchManager().removeFighter(player, playerData, true);
				}


				if (to.getX() != from.getX() || to.getZ() != from.getZ()) {
					if (match.getMatchState() == MatchState.STARTING) {
						player.teleport(from);
						((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
					}
				}
			}
			if (match.getKit().isParkour()) {

				if(BlockUtil.isStandingOn(player, Material.GOLD_PLATE)) {
					Iterator<UUID> uuidIterator = this.plugin.getMatchManager().getOpponents(match, player).iterator();

					while (uuidIterator.hasNext()) {
						UUID uuid = uuidIterator.next();

						Player opponent = Bukkit.getPlayer(uuid);

						if(opponent != null) {
							this.plugin.getMatchManager().removeFighter(opponent, PracticeData.getByName(opponent.getName()), true);
						}
					}

					parkourCheckpoints.remove(match);

				} else if (BlockUtil.isStandingOn(player, Material.WATER) || BlockUtil.isStandingOn(player, Material.STATIONARY_WATER)) {
					this.teleportToSpawnOrCheckpoint(match, player);
				} else if (BlockUtil.isStandingOn(player, Material.STONE_PLATE) || BlockUtil.isStandingOn(player, Material.IRON_PLATE) || BlockUtil.isStandingOn(player, Material.WOOD_PLATE)) {

					boolean checkpoint = false;

					if(!parkourCheckpoints.containsKey(match)) {
						checkpoint = true;
						parkourCheckpoints.put(match, new HashMap<>());
					}

					if(!parkourCheckpoints.get(match).containsKey(player.getUniqueId())) {
						checkpoint = true;
						parkourCheckpoints.get(match).put(player.getUniqueId(), CustomLocation.fromBukkitLocation(player.getLocation()));
					}
					else if(parkourCheckpoints.get(match).containsKey(player.getUniqueId()) && !BlockUtil.isSameLocation(player.getLocation(), parkourCheckpoints.get(match).get(player.getUniqueId()).toBukkitLocation())) {
						checkpoint = true;
						parkourCheckpoints.get(match).put(player.getUniqueId(), CustomLocation.fromBukkitLocation(player.getLocation()));
					}

					if(checkpoint) {
						player.sendMessage(CC.PRIMARY + "Checkpoint has been saved.");
					}

				}

				if (to.getX() != from.getX() || to.getZ() != from.getZ()) {
					if (match.getMatchState() == MatchState.STARTING) {
						player.teleport(from);
						((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
					}
				}
			}
		}

		PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);

		if(event != null) {

			if(event instanceof SumoEvent) {
				SumoEvent sumoEvent = (SumoEvent) event;

				if (sumoEvent.getPlayer(player).getFighting() != null && sumoEvent.getPlayer(player).getState() == SumoPlayer.SumoState.PREPARING) {
					player.teleport(from);
					((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
				}
			} else if(event instanceof OITCEvent) {
				OITCEvent oitcEvent = (OITCEvent) event;

				if(oitcEvent.getPlayer(player).getState() == OITCPlayer.OITCState.RESPAWNING) {
					((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
				}

				/*if(oitcEvent.getPlayer(player).getState() == OITCPlayer.OITCState.FIGHTING) {
					if(player.getLocation().getBlockY() >= 90) {
						oitcEvent.teleportNextLocation(player);
						player.sendMessage(ChatColor.RED + "You have been teleported back to the arena.");
					}
				}*/

			}

		}
	}

	@Override
	public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {

	}

	private void teleportToSpawnOrCheckpoint(Match match, Player player) {

		if(!parkourCheckpoints.containsKey(match)) {
			player.sendMessage(CC.PRIMARY + "Teleporting back to the beginning.");
			player.teleport(match.getArena().getA().toBukkitLocation());
			return;
		}

		if(!parkourCheckpoints.get(match).containsKey(player.getUniqueId())) {
			player.sendMessage(CC.PRIMARY + "Teleporting back to the beginning.");
			player.teleport(match.getArena().getA().toBukkitLocation());
			return;
		}

		player.teleport(parkourCheckpoints.get(match).get(player.getUniqueId()).toBukkitLocation());
		player.sendMessage(CC.PRIMARY + "Teleporting back to last checkpoint.");
	}

	public static HashMap<Match, HashMap<UUID, CustomLocation>> getParkourCheckpoints() {
		return parkourCheckpoints;
	}
}
