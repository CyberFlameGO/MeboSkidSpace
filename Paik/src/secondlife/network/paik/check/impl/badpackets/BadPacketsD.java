package secondlife.network.paik.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class BadPacketsD extends PacketCheck {
    
    private boolean sent;
    
    public BadPacketsD(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Packets (Check 4)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInEntityAction) {
            PacketPlayInEntityAction.EnumPlayerAction playerAction = ((PacketPlayInEntityAction)packet).b();

            if(playerAction == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING || playerAction == PacketPlayInEntityAction.EnumPlayerAction.STOP_SNEAKING) {
                if(this.sent) {
                    if(this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", true)) {
                        int violations = this.playerData.getViolations(this, 60000L);

                        if(!this.playerData.isBanning() && violations > 2) {
                            this.ban(player);
                        }
                    }
                } else {
                    this.sent = true;
                }
            }
        } else if(packet instanceof PacketPlayInFlying) {
            this.sent = false;
        }
    }
}
