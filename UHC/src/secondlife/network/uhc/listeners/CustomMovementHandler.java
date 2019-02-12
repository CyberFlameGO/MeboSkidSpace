package secondlife.network.uhc.listeners;

import club.minemen.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.managers.GameManager;
import secondlife.network.uhc.state.GameState;

/**
 * Created by Marko on 01.06.2018.
 */
public class CustomMovementHandler implements MovementHandler {
    @Override
    public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
        UHC.getInstance().getSpectatorManager().handleMove(player, from, to);

        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            MultiSpawnListener.handleMove(player, from, to);
        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {

    }
}
