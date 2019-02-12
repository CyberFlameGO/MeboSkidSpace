package secondlife.network.vituz.listeners;

import club.minemen.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;

public class CustomMovementListener implements MovementHandler {

	@Override
	public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
		Vituz.getInstance().getFreezeManager().handleMove(player, from, to);
	}

	@Override
	public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) { }
}
