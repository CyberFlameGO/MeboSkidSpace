package secondlife.network.overpass.handler;

import club.minemen.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import secondlife.network.overpass.data.OverpassData;
import secondlife.network.overpass.listeners.PlayerListener;

/**
 * Created by Marko on 22.07.2018.
 */
public class CustomMovementHandler implements MovementHandler {

    @Override
    public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
        OverpassData overpassData = OverpassData.getByName(player.getName());

        if(PlayerListener.doStuff(overpassData)) {
            player.teleport(from);
            return;
        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {

    }
}
