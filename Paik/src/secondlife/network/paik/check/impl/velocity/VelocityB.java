package secondlife.network.paik.check.impl.velocity;

import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PositionCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.vituz.utilties.update.PositionUpdate;

public class VelocityB extends PositionCheck {

    public VelocityB(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Velocity (Check 2)");
    }
    
    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        double offsetY = update.getTo().getY() - update.getFrom().getY();

        if(this.playerData.getVelocityY() > 0.0 && this.playerData.isWasOnGround() && !this.playerData.isUnderBlock() && !this.playerData.isWasUnderBlock() && !this.playerData.isInLiquid() && !this.playerData.isWasInLiquid() && !this.playerData.isInWeb() && !this.playerData.isWasInWeb() && !this.playerData.isOnStairs() && offsetY > 0.0 && offsetY < 0.41999998688697815 && update.getFrom().getY() % 1.0 == 0.0) {
            double ratioY = offsetY / this.playerData.getVelocityY();
            int vl = (int)this.getVl();

            if(ratioY < 0.99) {
                int percent = (int)Math.round(ratioY * 100.0);

                if(++vl >= 5 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "P " + percent + ". VL " + vl + ".", false) && !this.playerData.isBanning() && vl >= 15) {
                    this.ban(player);
                }
            } else {
                --vl;
            }

            this.setVl(vl);
        }
    }
}
