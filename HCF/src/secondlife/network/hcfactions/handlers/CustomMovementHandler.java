package secondlife.network.hcfactions.handlers;

import club.minemen.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.events.KitMapEvent;
import secondlife.network.hcfactions.events.sumo.SumoEvent;
import secondlife.network.hcfactions.events.sumo.SumoPlayer;
import secondlife.network.hcfactions.factions.handlers.ProtectionHandler;
import secondlife.network.hcfactions.factions.utils.events.FactionPlayerClaimEnterEvent;
import secondlife.network.hcfactions.timers.HomeHandler;
import secondlife.network.hcfactions.timers.LogoutHandler;
import secondlife.network.hcfactions.timers.StuckHandler;

public class CustomMovementHandler implements MovementHandler {

	private final HCF plugin = HCF.getInstance();

	@Override
	public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
		ProtectionHandler.handleMove(player, from, to, FactionPlayerClaimEnterEvent.EnterCause.MOVEMENT);
		HomeHandler.handleMove(player, from, to);
		LogoutHandler.handleMove(player, from, to);
		StuckHandler.handleMove(player, to);
		BorderHandler.handleMove(player, from, to);
		DynamicPlayerHandler.handleFirstMove(player, from, to);
		DynamicPlayerHandler.handleSecondMove(player);

		HCFData playerData = HCFData.getByName(player.getName());

		if (playerData == null) {
			return;
		}

		KitMapEvent event = this.plugin.getEventManager().getEventPlaying(player);

		if(event != null) {

			if(event instanceof SumoEvent) {
				SumoEvent sumoEvent = (SumoEvent) event;

				if (sumoEvent.getPlayer(player).getFighting() != null && sumoEvent.getPlayer(player).getState() == SumoPlayer.SumoState.PREPARING) {
					player.teleport(from);
					((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
				}
			}

		}
	}

	@Override
	public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {

	}
}
