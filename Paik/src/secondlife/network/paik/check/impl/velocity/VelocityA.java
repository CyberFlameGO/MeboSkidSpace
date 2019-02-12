package secondlife.network.paik.check.impl.velocity;

import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PositionCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.MathUtil;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.vituz.utilties.update.PositionUpdate;

public class VelocityA extends PositionCheck {
    
    public VelocityA(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Velocity (Check 1)");
    }
    
    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        int vl = (int)this.getVl();

        if(this.playerData.getVelocityY() > 0.0 && !this.playerData.isUnderBlock() && !this.playerData.isWasUnderBlock() && !this.playerData.isInLiquid() && !this.playerData.isWasInLiquid() && !this.playerData.isInWeb() && !this.playerData.isWasInWeb() && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L && System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L) {
            int threshold = 10 + MathUtil.pingFormula(this.playerData.getPing()) * 2;

            if(++vl >= threshold) {
                if(this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "VL " + vl + ".", true)) {
                    int violations = this.playerData.getViolations(this, 60000L);

                    if(!this.playerData.isBanning() && violations > Math.max(this.playerData.getPing() / 10L, 15L)) {
                        this.ban(player);
                    }
                }

                this.playerData.setVelocityY(0.0);
                vl = 0;
            }
        } else {
            vl = 0;
        }

        this.setVl(vl);
    }
}
