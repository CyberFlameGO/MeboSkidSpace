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

public class KillAuraA extends PacketCheck {
    
    private boolean sent;
    
    public KillAuraA(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Kill-Aura (Check 1)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity)packet).a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
            if(!this.sent) {
                if(this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", true)) {
                    int violations = this.playerData.getViolations(this, 60000L);

                    if(!this.playerData.isBanning() && violations > 5) {
                        this.ban(player);
                    }
                }
            } else {
                this.sent = false;
            }
        } else if(packet instanceof PacketPlayInArmAnimation) {
            this.sent = true;
        } else if(packet instanceof PacketPlayInFlying) {
            this.sent = false;
        }
    }
}
