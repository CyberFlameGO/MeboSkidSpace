package secondlife.network.hub.providers;

import org.bukkit.entity.Player;
import secondlife.network.hub.Hub;
import secondlife.network.hub.managers.QueueManager;
import secondlife.network.hub.utilties.HubUtils;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.providers.LayoutProvider;
import secondlife.network.vituz.providers.tab.TabLayout;
import secondlife.network.vituz.status.ServerData;

public class TabProvider implements LayoutProvider {

	private Hub plugin = Hub.getInstance();

	public TabLayout getLayout(Player player) {
		TabLayout layout = TabLayout.create(player);

		if(PlayerData.getByName(player.getName()).isTab()) {
			layout.set(1, 0, "&5&lSecondLife");

			layout.set(1, 2, "&fGlobal Online:");
			layout.set(1, 3, "&d" + plugin.getCountManager().getGlobalCount() + "/1000");

			layout.set(0, 5, "&fTwitter:");
			layout.set(0, 6, "&d@SecondLifeMC");

			layout.set(1, 5, "&fRank:");
			layout.set(1, 6, VituzAPI.getColorPrefix(player) + VituzAPI.getRankName(player.getName()));

			layout.set(2, 5, "&fQueued:");
			if(QueueManager.getByPlayer(player) != null) {
				layout.set(2, 6, "&f" + QueueManager.getQueueName(player).toUpperCase() + ":");
				layout.set(2, 7, "&d#" + (QueueManager.getByPlayer(player).getPlayers().indexOf(player) + 1) + " of &d" + QueueManager.getByPlayer(player).getPlayers().size());
			} else {
				layout.set(2, 6, "&dNone");
			}

			layout.set(1, 8, "&5&lServer Info");

			ServerData uhcData = ServerData.getByName("UHC-1");

			if(uhcData != null) {
				layout.set(0, 10, "&5&lUHC-1");
				layout.set(0, 11, "&fStatus: " + uhcData.getTranslatedStatus());
				layout.set(0, 12, uhcData.isOnline() ? "&fOnline: &d" + uhcData.getOnlinePlayers() + "/" + uhcData.getMaximumPlayers() : HubUtils.loading);
			} else {
				layout.set(0, 10, "&5&lUHC-1");
				layout.set(0, 11, "&fStatus: &cOffline");
				layout.set(0, 12, "&f" + HubUtils.loading);
			}

			ServerData uhcData2 = ServerData.getByName("UHC-2");

			if(uhcData2 != null) {
				layout.set(1, 10, "&5&lUHC-2");
				layout.set(1, 11, "&fStatus: " + uhcData2.getTranslatedStatus());
				layout.set(1, 12, uhcData2.isOnline() ? "&fOnline: &d" + uhcData2.getOnlinePlayers() + "/" + uhcData2.getMaximumPlayers() : HubUtils.loading);
			} else {
				layout.set(1, 10, "&5&lUHC-2");
				layout.set(1, 11, "&fStatus: &cOffline");
				layout.set(1, 12, "&f" + HubUtils.loading);
			}

			ServerData meetupData = ServerData.getByName("UHCMeetup-Lobby");

			if(meetupData != null) {
				layout.set(0, 14, "&5&lUHCMeetup");
				layout.set(0, 15, "&fStatus: " + meetupData.getTranslatedStatus());
				layout.set(0, 16, meetupData.isOnline() ? "&fOnline: &d" + HubUtils.getMeetupCount() + "/" + meetupData.getMaximumPlayers() : HubUtils.loading);
			} else {
				layout.set(0, 14, "&5&lUHCMeetup");
				layout.set(0, 15, "&fStatus: &cOffline");
				layout.set(0, 16, "&f" + HubUtils.loading);
			}

			ServerData kitmapData = ServerData.getByName("KitMap");

			if(kitmapData != null) {
				layout.set(2, 10, "&5&lKitMap");
				layout.set(2, 11, "&fStatus: " + kitmapData.getTranslatedStatus());
				layout.set(2, 12, kitmapData.isOnline() ? "&fOnline: &d" + kitmapData.getOnlinePlayers() + "/" + kitmapData.getMaximumPlayers() : HubUtils.loading);
			} else {
				layout.set(2, 10, "&5&lKitMap");
				layout.set(2, 11, "&fStatus: &cOffline");
				layout.set(2, 12, "&f" + HubUtils.loading);
			}

			ServerData factionsData = ServerData.getByName("Factions");

			if(factionsData != null) {
				layout.set(2, 14, "&5&lFactions");
				layout.set(2, 15, "&fStatus: " + factionsData.getTranslatedStatus());
				layout.set(2, 16, factionsData.isOnline() ? "&fOnline: &d" + factionsData.getOnlinePlayers() + "/" + factionsData.getMaximumPlayers() : HubUtils.loading);
			} else {
				layout.set(2, 14, "&5&lFactions");
				layout.set(2, 15, "&fStatus: &cOffline");
				layout.set(2, 16, "&f" + HubUtils.loading);
			}
		}
		
		return layout;
	}
}
