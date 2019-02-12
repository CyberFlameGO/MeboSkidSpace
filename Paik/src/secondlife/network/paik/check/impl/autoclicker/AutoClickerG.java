package secondlife.network.paik.check.impl.autoclicker;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class AutoClickerG extends PacketCheck {
    
    private boolean failed;
    private boolean sent;
    
    public AutoClickerG(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Auto-Clicker (Check 7)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInBlockPlace && ((PacketPlayInBlockPlace)packet).getFace() == 255 && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L && this.playerData.getLastMovePacket() != null && System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L && this.playerData.getLastAnimationPacket() + 1000L > System.currentTimeMillis()) {
            if(this.sent) {
                if(!this.failed) {
                    this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "", false);
                    this.failed = true;
                }
            } else {
                this.sent = true;
            }
        } else if (packet instanceof PacketPlayInFlying) {
            boolean b = false;

            this.failed = b;
            this.sent = b;
        }
    }
}
