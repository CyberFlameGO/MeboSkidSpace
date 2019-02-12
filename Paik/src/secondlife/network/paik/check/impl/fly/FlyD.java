package secondlife.network.paik.check.impl.fly;

import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PositionCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.vituz.utilties.update.PositionUpdate;

public class FlyD extends PositionCheck {

    public FlyD(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Flight (Check 4)");
    }
    
    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        double offsetY = update.getTo().getY() - update.getFrom().getY();

        if(this.playerData.getVelocityY() == 0.0 && this.playerData.isWasOnGround() && !this.playerData.isUnderBlock() && !this.playerData.isWasUnderBlock() && !this.playerData.isInLiquid() && !this.playerData.isWasInLiquid() && !this.playerData.isInWeb() && !this.playerData.isWasInWeb() && !this.playerData.isOnStairs() && offsetY > 0.0 && offsetY < 0.41999998688697815 && update.getFrom().getY() % 1.0 == 0.0) {
            this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, String.format("O %.2f.", offsetY), false);
        }
    }
}
