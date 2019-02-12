package secondlife.network.practice.providers;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.providers.LayoutProvider;
import secondlife.network.vituz.providers.tab.TabLayout;
import secondlife.network.vituz.system.ranks.type.Rank;

public class TabProvider implements LayoutProvider {

	public TabLayout getLayout(Player player) {
		TabLayout layout = TabLayout.create(player);

		if(player == null) return null;


		if(secondlife.network.vituz.handlers.data.PlayerData.getByName(player.getName()).isTab()) {
			layout.set(1, 0, "&5&lSecondLife");

			layout.set(0, 19, "&dsecondlife.network");
			layout.set(1, 19, "&dsecondlife.network");
			layout.set(2, 19, "&dsecondlife.network");

			int count = 6;

			for (Rank rank : ImmutableList.copyOf(Rank.getRanks()).reverse()) {
				for(Player players : Bukkit.getOnlinePlayers()) {
					if (VituzAPI.getRank(players).equals(rank)) {
						if (count <= 53) {
							layout.forceSet(count, VituzAPI.getNamePrefix(players) + players.getName());
							count++;
						}
					}
				}
			}
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
