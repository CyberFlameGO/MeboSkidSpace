package secondlife.network.paik.check.impl.killaura;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.BlockUtil;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class KillAuraI extends PacketCheck {

    private boolean sent;
    
    public KillAuraI(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Kill-Aura (Check 9)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(!BlockUtil.checkMaterial(player) && packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig)packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
            if(this.sent) {
                this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "", false);
            }
        } else if(packet instanceof PacketPlayInArmAnimation) {
            this.sent = true;
        } else if(packet instanceof PacketPlayInFlying) {
            this.sent = false;
        }
    }
}
