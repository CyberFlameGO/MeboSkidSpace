package secondlife.network.meetuplobby.providers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.meetuplobby.utilties.MeetupUtils;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.providers.LayoutProvider;
import secondlife.network.vituz.providers.tab.TabLayout;
import secondlife.network.vituz.status.ServerData;

public class TabProvider implements LayoutProvider {

	public TabLayout getLayout(Player player) {
		TabLayout layout = TabLayout.create(player);

		if(PlayerData.getByName(player.getName()).isTab()) {
			layout.set(1, 0, "&5&lSecondLife");

			layout.set(0, 2, "&fPlayers Online:");
			layout.set(0, 3, "&d" + Bukkit.getOnlinePlayers().size());

			layout.set(1, 2, "&fRank:");
			layout.set(1, 3, VituzAPI.getColorPrefix(player) + VituzAPI.getRankName(player.getName()));

			layout.set(2, 2, "&fFacebook:");
			layout.set(2, 3, "&d@SecondLifeNetwork");

			layout.set(1, 5, "&5&lServer Info");

			ServerData data1 = ServerData.getByName("UHCMeetup-1");
			ServerData data2 = ServerData.getByName("UHCMeetup-2");
			ServerData data3 = ServerData.getByName("UHCMeetup-3");

			ServerData data4 = ServerData.getByName("UHCMeetup-4");
			ServerData data5 = ServerData.getByName("UHCMeetup-5");
			ServerData data6 = ServerData.getByName("UHCMeetup-6");

			if(data1 != null) {
				layout.set(0, 7, "&5&lMeetup 1");
				layout.set(0, 8, "&fState: " + data1.getMotd());
				layout.set(0, 9, data1.isOnline() ? "&fOnline: &d" + data1.getOnlinePlayers()
						       + "/" + data1.getMaximumPlayers() : MeetupUtils.loading);
			} else {
				layout.set(0, 7, "&5&lMeetup 1");
				layout.set(0, 8, "&fState: &cSetup");
				layout.set(0, 9, "&f" + MeetupUtils.loading);
			}

			if(data2 != null) {
				layout.set(1, 7, "&5&lMeetup 2");
				layout.set(1, 8, "&fState: " + data2.getMotd());
				layout.set(1, 9, data2.isOnline() ? "&fOnline: &d" + data2.getOnlinePlayers()
						+ "/" + data2.getMaximumPlayers() : MeetupUtils.loading);
			} else {
				layout.set(1, 7, "&5&lMeetup 2");
				layout.set(1, 8, "&fState: &cSetup");
				layout.set(1, 9, "&f" + MeetupUtils.loading);
			}

			if(data3 != null) {
				layout.set(2, 7, "&5&lMeetup 3");
				layout.set(2, 8, "&fState: " + data3.getMotd());
				layout.set(2, 9, data3.isOnline() ? "&fOnline: &d" + data3.getOnlinePlayers()
						+ "/" + data3.getMaximumPlayers() : MeetupUtils.loading);
			} else {
				layout.set(2, 7, "&5&lMeetup 3");
				layout.set(2, 8, "&fState: &cSetup");
				layout.set(2, 9, "&f" + MeetupUtils.loading);
			}

			if(data4 != null) {
				layout.set(0, 11, "&5&lMeetup 4");
				layout.set(0, 12, "&fState: " + data4.getMotd());
				layout.set(0, 13, data4.isOnline() ? "&fOnline: &d" + data4.getOnlinePlayers()
						+ "/" + data4.getMaximumPlayers() : MeetupUtils.loading);
			} else {
				layout.set(0, 11, "&5&lMeetup 4");
				layout.set(0, 12, "&fState: &cSetup");
				layout.set(0, 13, "&f" + MeetupUtils.loading);
			}

			if(data5 != null) {
				layout.set(1, 11, "&5&lMeetup 5");
				layout.set(1, 12, "&fState: " + data5.getMotd());
				layout.set(1, 13, data5.isOnline() ? "&fOnline: &d" + data5.getOnlinePlayers()
						+ "/" + data5.getMaximumPlayers() : MeetupUtils.loading);
			} else {
				layout.set(1, 11, "&5&lMeetup 5");
				layout.set(1, 12, "&fState: &cSetup");
				layout.set(1, 13, "&f" + MeetupUtils.loading);
			}

			if(data6 != null) {
				layout.set(2, 11, "&5&lMeetup 6");
				layout.set(2, 12, "&fState: " + data6.getMotd());
				layout.set(2, 13, data6.isOnline() ? "&fOnline: &d" + data6.getOnlinePlayers()
						+ "/" + data6.getMaximumPlayers() : MeetupUtils.loading);
			} else {
				layout.set(2, 11, "&5&lMeetup 6");
				layout.set(2, 12, "&fState: &cSetup");
				layout.set(2, 13, "&f" + MeetupUtils.loading);
			}

			String name = player.getName();

			layout.set(1, 15, "&5&lYour Stats");

			layout.set(0, 16, "&fKills: &d" + MeetupUtils.getStats("kills", name));
			layout.set(1, 16, "&fKill Streak: &d" + MeetupUtils.getStats("highest_killstreak", name));
			layout.set(2, 16, "&fWins: &d" + MeetupUtils.getStats("wins", name));

			layout.set(0, 17, "&fPlayed: &d" + MeetupUtils.getStats("played", name));
			layout.set(1, 17, "&fRerolls: &d" + MeetupUtils.getStats("rerolls", name));
			layout.set(2, 17, "&fElo: &d" + MeetupUtils.getStats("elo", name));
		} else {
			int count = 0;

			for (Player online : Bukkit.getOnlinePlayers()) {
				if (count < 60) {
					layout.forceSet(count, VituzAPI.getNamePrefix(online) + online.getName());
					count++;
				}
			}
		}

		return layout;
	}
}
