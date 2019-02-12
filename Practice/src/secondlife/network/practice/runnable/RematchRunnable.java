package secondlife.network.practice.runnable;

import secondlife.network.practice.Practice;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.player.PlayerState;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class RematchRunnable implements Runnable {
	private final Practice plugin = Practice.getInstance();

	private final UUID playerUUID;

	@Override
	public void run() {
		Player player = this.plugin.getServer().getPlayer(this.playerUUID);
		if (player != null) {
			PracticeData playerData = PracticeData.getByName(player.getName());
			if (playerData != null) {
				if (playerData.getPlayerState() == PlayerState.SPAWN
						&& this.plugin.getMatchManager().isRematching(player.getUniqueId())
						&& this.plugin.getPartyManager().getParty(player.getUniqueId()) == null) {
					player.getInventory().setItem(3, null);
					player.getInventory().setItem(5, null);
					player.updateInventory();
					playerData.setRematchID(-1);
				}
			}
			this.plugin.getMatchManager().removeRematch(playerUUID);
		}
	}
}
