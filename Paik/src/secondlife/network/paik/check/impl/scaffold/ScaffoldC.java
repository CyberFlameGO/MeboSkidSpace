package secondlife.network.paik.check.impl.scaffold;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class ScaffoldC extends PacketCheck {

    private int looks;
    private int stage;
    
    public ScaffoldC(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Placement (Check 3)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        double vl = this.getVl();
      
        if(packet instanceof PacketPlayInFlying.PacketPlayInLook) {
            if(this.stage == 0) {
                ++this.stage;
            } else if(this.stage == 4) {
                if((vl += 1.75) > 3.5) {
                    this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, String.format("VL %.2f.", vl), false);
                }
                
                this.stage = 0;
            } else {
                boolean b = false;

                this.looks = (b ? 1 : 0);
                this.stage = (b ? 1 : 0);

                vl -= 0.2;
            }
        } else if(packet instanceof PacketPlayInBlockPlace) {
            if(this.stage == 1) {
                ++this.stage;
            } else {
                boolean b2 = false;

                this.looks = (b2 ? 1 : 0);
                this.stage = (b2 ? 1 : 0);
            }
        } else if(packet instanceof PacketPlayInArmAnimation) {
            if(this.stage == 2) {
                ++this.stage;
            } else {
                boolean b3 = false;

                this.looks = (b3 ? 1 : 0);
                this.stage = (b3 ? 1 : 0);
                vl -= 0.2;
            }
        } else if(packet instanceof PacketPlayInFlying.PacketPlayInPositionLook || packet instanceof PacketPlayInFlying.PacketPlayInPosition) {
            if(this.stage == 3) {
                if(++this.looks == 3) {
                    this.stage = 4;
                    this.looks = 0;
                }
            } else {
                boolean b4 = false;

                this.looks = (b4 ? 1 : 0);
                this.stage = (b4 ? 1 : 0);
            }
        }

        this.setVl(vl);
    }
}
