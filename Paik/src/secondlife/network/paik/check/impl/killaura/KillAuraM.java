package secondlife.network.paik.check.impl.killaura;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class KillAuraM extends PacketCheck {

    private int swings;
    private int attacks;
    
    public KillAuraM(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Kill-Aura (Check 13)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(!this.playerData.isDigging() && !this.playerData.isPlacing()) {
            if(packet instanceof PacketPlayInFlying) {
                if(this.attacks > 0 && this.swings > this.attacks) {
                    this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "S " + this.swings + ". A " + this.attacks + ".", false);
                }

                KillAuraN auraN = this.playerData.getCheck(KillAuraN.class);

                if(auraN != null) {
                    auraN.handleCheck(player, new int[] { this.swings, this.attacks });
                }

                this.swings = 0;
                this.attacks = 0;
            } else if (packet instanceof PacketPlayInArmAnimation) {
                ++this.swings;
            } else if(packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity)packet).a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                ++this.attacks;
            }
        }
    }
}
