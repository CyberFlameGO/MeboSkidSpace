package secondlife.network.hcfactions.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCF;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.ActionMessage;
import secondlife.network.vituz.utilties.Color;

@Setter
@Getter
public abstract class EventCountdownTask extends BukkitRunnable {
	private final KitMapEvent event;
	private final int countdownTime;
	private int timeUntilStart;

	private boolean ended;

	public EventCountdownTask(KitMapEvent event, int countdownTime) {
		this.event = event;
		this.countdownTime = countdownTime;
		this.timeUntilStart = countdownTime;
	}

	@Override
	public void run() {
		if (isEnded()) {
			return;
		}

		if (timeUntilStart <= 0) {
			if (canStart()) {
				Bukkit.getScheduler().runTask(HCF.getInstance(), () -> event.start());
			} else {
				Bukkit.getScheduler().runTask(HCF.getInstance(), () -> onCancel());
			}

			ended = true;
			return;
		}

		if (shouldAnnounce(timeUntilStart)) {
			String color = VituzAPI.getColorPrefix(event.getHost()) + "&l";
			String toSend = "&7[" + color + "*" + "&7] " + color + event.getHost().getName() + " &fis hosting " + color + event.getName() + " Event&f. [Join]";

			ActionMessage actionMessage = new ActionMessage();
			actionMessage.addText(toSend).addHoverText(ChatColor.GRAY + "Click to join this event.").setClickEvent(ActionMessage.ClickableType.RunCommand, "/joinevent " + event.getName());

			Bukkit.getServer().getOnlinePlayers().forEach(player -> {
				actionMessage.sendToPlayer(player);
				player.sendMessage(Color.translate("&4&lBefore you join save your items because they will be cleared when you join the event!"));
			});

		}

		timeUntilStart--;
	}

	public abstract boolean shouldAnnounce(int timeUntilStart);

	public abstract boolean canStart();

	public abstract void onCancel();

	/**
	 * Because TimeUtil#millisToRoundedTime is shit
	 */
	private String getTime(int time) {
		StringBuilder timeStr = new StringBuilder();
		int minutes = 0;

		if (time % 60 == 0) {
			minutes = time / 60;
			time = 0;
		} else {
			while (time - 60 > 0) {
				minutes++;
				time -= 60;
			}
		}

		if (minutes > 0) {
			timeStr.append(minutes).append("m");
		}
		if (time > 0) {
			timeStr.append(minutes > 0 ? " " : "").append(time).append("s");
		}

		return timeStr.toString();
	}
}
