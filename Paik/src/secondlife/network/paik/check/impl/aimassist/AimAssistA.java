package secondlife.network.paik.check.impl.aimassist;

import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.RotationCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.MathUtil;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.vituz.utilties.update.RotationUpdate;

public class AimAssistA extends RotationCheck {

    private float suspiciousYaw;
    
    public AimAssistA(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Aim (Check 1)");
    }
    
    @Override
    public void handleCheck(Player player, RotationUpdate update) {
        if(System.currentTimeMillis() - this.playerData.getLastAttackPacket() > 10000L) return;
        
        float diffYaw = MathUtil.getDistanceBetweenAngles(update.getTo().getYaw(), update.getFrom().getYaw());

        if(diffYaw > 1.0f && Math.round(diffYaw) == diffYaw && diffYaw % 1.5f != 0.0f) {
            if(diffYaw == this.suspiciousYaw && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "Y " + diffYaw + ".", true)) {
                int violations = this.playerData.getViolations(this, 60000L);

                if(!this.playerData.isBanning() && violations > 20) {
                    this.ban(player);
                }
            }

            this.suspiciousYaw = Math.round(diffYaw);
        } else {
            this.suspiciousYaw = 0.0f;
        }
    }
}
