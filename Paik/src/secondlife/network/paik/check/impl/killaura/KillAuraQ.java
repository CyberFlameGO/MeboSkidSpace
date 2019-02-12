package secondlife.network.paik.check.impl.killaura;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class KillAuraQ extends PacketCheck {
    
    private boolean sentAttack;
    private boolean sentInteract;
    
    public KillAuraQ(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Kill-Aura (Check 17)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInBlockPlace) {
            if(this.sentAttack && !this.sentInteract && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", true)) {
                int violations = this.playerData.getViolations(this, 60000L);

                if(!this.playerData.isBanning() && violations > 2) {
                    this.ban(player);
                }
            }
        } else if (packet instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity.EnumEntityUseAction action = ((PacketPlayInUseEntity)packet).a();

            if(action == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                this.sentAttack = true;
            } else if(action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
                this.sentInteract = true;
            }
        } else if(packet instanceof PacketPlayInFlying) {
            boolean b = false;

            this.sentInteract = b;
            this.sentAttack = b;
        }
    }
}
