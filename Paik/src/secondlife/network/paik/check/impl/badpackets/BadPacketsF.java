package secondlife.network.paik.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInHeldItemSlot;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class BadPacketsF extends PacketCheck {
    
    private boolean sent;
    
    public BadPacketsF(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Packets (Check 6)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInHeldItemSlot) {
            if(this.sent && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", true)) {
                int violations = this.playerData.getViolations(this, 60000L);
               
                if(!this.playerData.isBanning() && violations > 2) {
                    this.ban(player);
                }
            }
        } else if(packet instanceof PacketPlayInBlockPlace) {
            this.sent = true;
        } else if(packet instanceof PacketPlayInFlying) {
            this.sent = false;
        }
    }
}
