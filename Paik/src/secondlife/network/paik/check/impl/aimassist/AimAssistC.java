package secondlife.network.paik.check.impl.aimassist;

import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.RotationCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.MathUtil;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.vituz.utilties.update.RotationUpdate;

public class AimAssistC extends RotationCheck {
    
    public AimAssistC(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Aim (Check 3)");
    }
    
    @Override
    public void handleCheck(Player player, RotationUpdate update) {
        if(System.currentTimeMillis() - this.playerData.getLastAttackPacket() > 10000L) return;

        float diffYaw = MathUtil.getDistanceBetweenAngles(update.getTo().getYaw(), update.getFrom().getYaw());
        double vl = this.getVl();

        if(update.getFrom().getPitch() == update.getTo().getPitch() && diffYaw >= 3.0f && update.getFrom().getPitch() != 90.0f && update.getTo().getPitch() != 90.0f) {
            if((vl += 0.9) >= 6.3) {
                this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, String.format("Y %.1f. VL %.1f.", diffYaw, vl), false);
            }
        } else {
            vl -= 1.6;
        }

        this.setVl(vl);
    }
}
