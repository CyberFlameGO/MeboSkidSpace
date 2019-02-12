package secondlife.network.paik.check.impl.vclip;

import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PositionCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.BlockUtil;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.vituz.utilties.update.PositionUpdate;

public class VClipA extends PositionCheck {

    public VClipA(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "V-Clip (Check 1)");
    }
    
    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        double difference = update.getTo().getY() - update.getFrom().getY();

        if(difference >= 2.0 && !BlockUtil.isBlockFaceAir(player)) {
            player.teleport(update.getFrom());

            this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", true);
        }
    }

}
