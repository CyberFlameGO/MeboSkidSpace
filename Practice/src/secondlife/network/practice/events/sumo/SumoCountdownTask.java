package secondlife.network.practice.events.sumo;

import org.bukkit.ChatColor;
import secondlife.network.practice.events.EventCountdownTask;
import secondlife.network.practice.events.PracticeEvent;

import java.util.Arrays;

public class SumoCountdownTask extends EventCountdownTask {
	public SumoCountdownTask(PracticeEvent event) {
		super(event, 60);
	}

	@Override
	public boolean shouldAnnounce(int timeUntilStart) {
		return Arrays.asList(60, 55, 50, 45, 30, 15, 10, 5).contains(timeUntilStart);
	}

	@Override
	public boolean canStart() {
		return getEvent().getPlayers().size() >= 2;
	}

	@Override
	public void onCancel() {
		getEvent().sendMessage(ChatColor.RED + "Not enough players. Event has been cancelled");
		getEvent().end();
		this.getEvent().getPlugin().getEventManager().setCooldown(0L);
	}
}
