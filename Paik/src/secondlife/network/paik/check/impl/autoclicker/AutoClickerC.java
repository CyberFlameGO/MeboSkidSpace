package secondlife.network.paik.check.impl.autoclicker;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class AutoClickerC extends PacketCheck {

    private boolean sent;
    
    public AutoClickerC(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Auto-Clicker (Check 3)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInBlockDig) {
            PacketPlayInBlockDig.EnumPlayerDigType digType = ((PacketPlayInBlockDig)packet).c();

            if(digType == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                this.sent = true;
            } else if(digType == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                int vl = (int)this.getVl();

                if(this.sent) {
                    if(++vl > 10 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "VL " + vl + ".", false) && !this.playerData.isBanning() && vl >= 20) {
                        this.ban(player);
                    }
                } else {
                    vl = 0;
                }

                this.setVl(vl);
            }
        } else if(packet instanceof PacketPlayInArmAnimation) {
            this.sent = false;
        }
    }
}
