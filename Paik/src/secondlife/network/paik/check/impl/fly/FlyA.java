package secondlife.network.paik.check.impl.fly;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PositionCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.vituz.utilties.update.PositionUpdate;

public class FlyA extends PositionCheck {
    
    public FlyA(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Flight (Check 1)");
    }
    
    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        int vl = (int)this.getVl();
       
        if(!this.playerData.isInLiquid() && !this.playerData.isOnGround() && this.playerData.getVelocityV() == 0) {
            if(update.getFrom().getY() >= update.getTo().getY()) return;

            double distance = update.getTo().getY() - this.playerData.getLastGroundY();
            double limit = 2.0;

            if(player.hasPotionEffect(PotionEffectType.JUMP)) {
                for(PotionEffect effect : player.getActivePotionEffects()) {
                    if(effect.getType().equals(PotionEffectType.JUMP)) {
                        int level = effect.getAmplifier() + 1;
                        limit += Math.pow(level + 4.2, 2.0) / 16.0;
                        break;
                    }
                }
            }

            if(distance > limit) {
                if(++vl >= 10 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "VL " + vl + ".", true)) {
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
