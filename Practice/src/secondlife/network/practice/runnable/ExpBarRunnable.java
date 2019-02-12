package secondlife.network.practice.runnable;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.events.oitc.OITCEvent;
import secondlife.network.practice.events.oitc.OITCPlayer;
import secondlife.network.practice.events.parkour.ParkourEvent;
import secondlife.network.practice.events.parkour.ParkourPlayer;
import secondlife.network.practice.handlers.EnderpearlHandler;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.vituz.handlers.data.PlayerData;

@RequiredArgsConstructor
public class ExpBarRunnable implements Runnable {

	@Override
	public void run() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player != null) {
				long time = EnderpearlHandler.getMillisecondsLeft(player);
				int seconds = (int) Math.round((double) time / 1000.0D);

				player.setLevel(seconds);
				player.setExp((float) time / 15000.0F);
			}

			PracticeData data = PracticeData.getByName(player.getName());

			if(data != null && data.getPlayerState().equals(PlayerState.FIGHTING) && data.getBedwarsRespawn() > 0) {
				if(player.getLevel() <= 0) {
					data.setBedwarsRespawn(0);
				}

				player.setLevel(data.getBedwarsRespawn());
			}

			PracticeEvent event = Practice.getInstance().getEventManager().getEventPlaying(player);

			if(event != null && event instanceof OITCEvent) {
				OITCEvent oitcEvent = (OITCEvent) event;
				OITCPlayer oitcPlayer = oitcEvent.getPlayer(player.getUniqueId());

				if(oitcPlayer != null && oitcPlayer.getState() != OITCPlayer.OITCState.WAITING && oitcEvent.getGameTask() != null) {
					int seconds = oitcEvent.getGameTask().getTime();

					if(seconds >= 0) {
						player.setLevel(seconds);
					}
				}
			} else if(event != null && event instanceof ParkourEvent) {
				ParkourEvent parkourEvent = (ParkourEvent) event;
				ParkourPlayer parkourPlayer = parkourEvent.getPlayer(player.getUniqueId());

				if(parkourPlayer != null && parkourPlayer.getState() != ParkourPlayer.ParkourState.WAITING && parkourEvent.getGameTask() != null) {
					int seconds = parkourEvent.getGameTask().getTime();

					if(seconds >= 0) {
						player.setLevel(seconds);
					}
				}
			}
		}
	}
}
