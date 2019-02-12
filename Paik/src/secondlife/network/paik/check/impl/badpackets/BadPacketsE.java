package secondlife.network.paik.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class BadPacketsE extends PacketCheck {

    public BadPacketsE(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Packets (Check 5)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig)packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM && this.playerData.isPlacing() && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", true)) {
            int violations = this.playerData.getViolations(this, 60000L);

            if(!this.playerData.isBanning() && violations > 2) {
                this.ban(player);
            }
        }
    }
}
