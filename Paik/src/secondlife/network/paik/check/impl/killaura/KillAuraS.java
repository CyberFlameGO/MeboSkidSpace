package secondlife.network.paik.check.impl.killaura;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class KillAuraS extends PacketCheck {

    private boolean sentArmAnimation;
    private boolean sentAttack;
    private boolean sentBlockPlace;
    private boolean sentUseEntity;
    
    public KillAuraS(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Kill-Aura (Check 19)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInArmAnimation) {
            this.sentArmAnimation = true;
        } else if(packet instanceof PacketPlayInUseEntity) {
            if(((PacketPlayInUseEntity)packet).a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                this.sentAttack = true;
            } else {
                this.sentUseEntity = true;
            }
        } else if(packet instanceof PacketPlayInBlockPlace && ((PacketPlayInBlockPlace)packet).getItemStack() != null && ((PacketPlayInBlockPlace)packet).getItemStack().getName().toLowerCase().contains("sword")) {
            this.sentBlockPlace = true;
        } else if(packet instanceof PacketPlayInFlying) {
            if(this.sentArmAnimation && !this.sentAttack && this.sentBlockPlace && this.sentUseEntity && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", true)) {
                int violations = this.playerData.getViolations(this, 60000L);

                if(!this.playerData.isBanning() && violations > 2) {
                    this.ban(player);
                }
            }

            boolean b = false;

            this.sentUseEntity = b;
            this.sentBlockPlace = b;
            this.sentAttack = b;
            this.sentArmAnimation = b;
        }
    }
}
