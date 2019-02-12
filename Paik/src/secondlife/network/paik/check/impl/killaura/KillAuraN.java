package secondlife.network.paik.check.impl.killaura;

import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.AbstractCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class KillAuraN extends AbstractCheck<int[]> {

    private int doubleSwings;
    private int doubleAttacks;
    private int bareSwings;
    
    public KillAuraN(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, int[].class, "Kill-Aura (Check 14)");
    }
    
    @Override
    public void handleCheck(Player player, int[] ints) {
        int swings = ints[0];
        int attacks = ints[1];

        if(swings > 1 && attacks == 0) {
            ++this.doubleSwings;
        } else if(swings == 1 && attacks == 0) {
            ++this.bareSwings;
        } else if(attacks > 1) {
            ++this.doubleAttacks;
        }

        if(this.doubleSwings + this.doubleAttacks == 20) {
            double vl = this.getVl();

            if(this.doubleSwings == 0) {
                if(this.bareSwings > 10 && ++vl > 3.0) {
                    this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "BS " + this.bareSwings + ". VL " + vl + ".", false);
                }
            } else {
                --vl;
            }

            this.setVl(vl);

            this.doubleSwings = 0;
            this.doubleAttacks = 0;
            this.bareSwings = 0;
        }
    }
}
