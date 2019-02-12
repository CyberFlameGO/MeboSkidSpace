package secondlife.network.paik.check.impl.fly;

import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PositionCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.vituz.utilties.update.PositionUpdate;

public class FlyB extends PositionCheck {
    
    public FlyB(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Flight (Check 2)");
    }
    
    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        int vl = (int)this.getVl();

        if(!this.playerData.isInLiquid() && !this.playerData.isOnGround()) {
            double offsetH = Math.hypot(update.getTo().getX() - update.getFrom().getX(), update.getTo().getZ() - update.getFrom().getZ());
            double offsetY = update.getTo().getY() - update.getFrom().getY();

            if(offsetH > 0.0 && offsetY == 0.0) {
                if(++vl >= 10 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, String.format("H %.2f. VL %s.", offsetH, vl), true)) {
                    int violations = this.playerData.getViolations(this, 60000L);

                    if(!this.playerData.isBanning() && violations > 8) {
                        this.ban(player);
                    }
                }
            } else {
                vl = 0;
            }
        } else {
            vl = 0;
        }

        this.setVl(vl);
    }
}
