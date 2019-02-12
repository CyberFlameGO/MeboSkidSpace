package secondlife.network.paik.check.impl.scaffold;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.paik.utilties.CustomLocation;

public class ScaffoldB extends PacketCheck {
   
    private long lastPlace;
    private boolean place;
    
    public ScaffoldB(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Placement (Check 2)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        double vl = this.getVl();
       
        if(packet instanceof PacketPlayInBlockPlace && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L && !this.playerData.isAllowTeleport()) {
            CustomLocation lastMovePacket = this.playerData.getLastMovePacket();
           
            if(lastMovePacket == null) return;
            
            long delay = System.currentTimeMillis() - lastMovePacket.getTimestamp();

            if(delay <= 25.0) {
                this.lastPlace = System.currentTimeMillis();
                this.place = true;
            } else {
                vl -= 0.25;
            }
        } else if(packet instanceof PacketPlayInFlying && this.place) {
            long time = System.currentTimeMillis() - this.lastPlace;

            if(time >= 25L) {
                if(++vl >= 10.0) {
                    this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, String.format("T %s. VL %.2f.", time, vl), false);
                }
            } else {
                vl -= 0.25;
            }

            this.place = false;
        }

        this.setVl(vl);
    }
}
