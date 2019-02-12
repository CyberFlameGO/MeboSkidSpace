package secondlife.network.paik.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class BadPacketsJ extends PacketCheck {

    private boolean placing;
    
    public BadPacketsJ(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Packets (Check 10)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInBlockDig) {
            if(((PacketPlayInBlockDig)packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM) {
                if(!this.placing && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", true)) {
                    int violations = this.playerData.getViolations(this, 60000L);

                    if(!this.playerData.isBanning() && violations > 2) {
                        this.ban(player);
                    }
                }

                this.placing = false;
            }
        } else if (packet instanceof PacketPlayInBlockPlace) {
            this.placing = true;
        }
    }
}
