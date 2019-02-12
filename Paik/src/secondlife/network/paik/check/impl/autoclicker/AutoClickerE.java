package secondlife.network.paik.check.impl.autoclicker;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class AutoClickerE extends PacketCheck {

    private boolean failed;
    private boolean sent;
    private int count;
    
    public AutoClickerE(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Auto-Clicker (Check 5)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInArmAnimation
            && (System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket()) > 220L
            && (System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp()) < 110L
            && !this.playerData.isDigging() && !this.playerData.isPlacing() && !this.playerData.isFakeDigging()) {

            if(this.sent) {
                ++this.count;

                if(!this.failed) {
                    int vl = (int)this.getVl();

                    if(++vl >= 5) {
                        this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "CO " + this.count + ".", false);
                        vl = 0;
                    }

                    this.setVl(vl);
                    this.failed = true;
                }
            } else {
                this.sent = true;
                this.count = 0;
            }
        } else if (packet instanceof PacketPlayInFlying) {
            boolean b = false;

            this.failed = b;
            this.sent = b;
            this.count = 0;
        }
    }
}
