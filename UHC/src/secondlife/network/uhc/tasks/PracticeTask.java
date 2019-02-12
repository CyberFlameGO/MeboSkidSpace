package secondlife.network.uhc.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.managers.GameManager;
import secondlife.network.uhc.state.GameState;
import secondlife.network.vituz.utilties.ActionMessage;
import secondlife.network.vituz.utilties.Color;

public class PracticeTask extends BukkitRunnable {

	private UHC plugin = UHC.getInstance();

	public PracticeTask() {
		this.runTaskTimer(UHC.getInstance(), 2000L, 2000L);
	}
	
	@Override
	public void run() {
		if(plugin.getPracticeManager().isOpen()) {
			ActionMessage actionMessage = new ActionMessage();
			actionMessage.addText("&dPractice&e is now open! ");
			actionMessage.addText("&7[Join]").setClickEvent(ActionMessage.ClickableType.RunCommand, "/practice join").addHoverText(Color.translate("&eClick to join!"));

			Bukkit.getOnlinePlayers().forEach(player -> actionMessage.sendToPlayer(player));
		} else {
			this.cancel();
		}
		
		if(GameManager.getGameState().equals(GameState.PLAYING)) {
			this.cancel();
		}
	}
}
