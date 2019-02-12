package secondlife.network.paik.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class BadPacketsB extends PacketCheck {

    public BadPacketsB(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Packets (Check 2)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInFlying && Math.abs(((PacketPlayInFlying)packet).e()) > 90.0f && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", false) && !this.playerData.isBanning()) {
            this.ban(player);
        }
    }
}
