package secondlife.network.paik.check.impl.step;

import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PositionCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.vituz.utilties.update.PositionUpdate;

public class StepA extends PositionCheck {

	public StepA(Paik plugin, PlayerData playerData) {
		super(plugin, playerData, "Step (Check 1)");
	}

	@Override
	public void handleCheck(Player player, PositionUpdate update) {
		double height = 0.9;
		double difference = update.getTo().getY() - update.getFrom().getY();

		if(difference > height) {
			this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "", true);
		}
	}

}
