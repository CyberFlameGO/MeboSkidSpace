package secondlife.network.hub.handler;

import club.minemen.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import secondlife.network.hub.Hub;

/**
 * Created by Marko on 02.06.2018.
 */
public class CustomMovementHandler implements MovementHandler {

    private Hub plugin = Hub.getInstance();

    @Override
    public void handleUpdateLocation(Player player, Location from, Location to, PacketPlayInFlying packetPlayInFlying) {
        plugin.getMultiSpawnManager().handleMove(player, from , to);
        plugin.getAutoKickManager().handleMove(player);
        plugin.getStaffSecurityManager().handleMove(player, from, to);
    }

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {

    }
}
