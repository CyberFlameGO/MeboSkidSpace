package secondlife.network.victions.handler;

import club.minemen.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import secondlife.network.victions.Victions;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.vituz.utilties.Color;

/**
 * Created by Marko on 18.07.2018.
 */
public class CustomMovemomentHandler implements MovementHandler {

    @Override
    public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
        FactionsData data = FactionsData.getByName(player.getName());

        if(data != null) {
            if(data.isHomeActive(player)) {
                data.cancelHome(player);
                player.sendMessage(Color.translate("&eTeleport canceled because you moved."));
            }

            if(data.isLogoutActive(player)) {
                data.cancelLogout(player);
                player.sendMessage(Color.translate("&eTeleport canceled because you moved."));
            }

            if(data.isNeedToTeleport()) {
                data.setNeedToTeleport(false);
                player.sendMessage(Color.translate("&eTeleport canceled because you moved."));
            }
        }

        Victions.getInstance().getGlassManager().handleMove(player, from, to);
    }

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {

    }
}
