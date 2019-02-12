package secondlife.network.uhc.providers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.managers.*;
import secondlife.network.uhc.party.Party;
import secondlife.network.uhc.player.UHCData;
import secondlife.network.uhc.scenario.Scenario;
import secondlife.network.uhc.state.GameState;
import secondlife.network.uhc.utilties.UHCUtils;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.providers.LayoutProvider;
import secondlife.network.vituz.providers.tab.TabLayout;

public class TabProvider implements LayoutProvider {

	private UHC plugin = UHC.getInstance();

	public TabLayout getLayout(Player player) {
		TabLayout layout = TabLayout.create(player);

		if(PlayerData.getByName(player.getName()).isTab()) {
			UHCData uhcData = UHCData.getByName(player.getName());

			layout.set(1, 0, "&5&lSecondLife &f" + VituzAPI.getServerName());

			layout.set(1, 2, "&fPlayers Online:");
			layout.set(1, 3, "&d" + Bukkit.getOnlinePlayers().size());

			layout.set(1, 5, "&5&lScenarios");

			int x = 6;
			for (Scenario scenario : ScenarioManager.scenarios) {
				if (scenario.isEnabled()) {
					layout.set(1, x, "&d" + scenario.getName());
					x++;
				}
			}

			if(x == 6) {
				layout.set(1, 6, "&dNone");
			}

			boolean nether = OptionManager.getByName("Nether").getValue() == 1;
			boolean speed = OptionManager.getByName("Speed Potions").getValue() == 1;

			if(!GameManager.getGameState().equals(GameState.PLAYING)) {
				layout.set(0, 0, "&fGame Type:");
				layout.set(0, 1, "&d" + UHCUtils.isPartiesEnabled());

				layout.set(0, 3, "&fBorder:");
				layout.set(0, 4, "&d" + BorderManager.border);

				layout.set(0, 6, "&fMax Players:");
				layout.set(0, 7, "&d" + Bukkit.getMaxPlayers());

				layout.set(2, 9, "&fSpeed Potions:");
				layout.set(2, 10, "&d" + (speed ? "Enabled" : "Disabled"));

				layout.set(2, 12, "&fNext UHC:");
				layout.set(2, 13, "&d" + plugin.getGameManager().getNextuhc());

				if(PartyManager.isEnabled()) {
					if(PartyManager.getParties().containsKey(player.getName())) {
						layout.set(0, 9, "&fYour Team:");

						int xd = 10;
						Party party = PartyManager.getByPlayer(player);

						for(String players : party.getPlayers()) {
							if(UHCData.getByName(Bukkit.getOfflinePlayer(players).getName()).isAlive()) {
								layout.set(1, xd, "&2" + Bukkit.getOfflinePlayer(players).getName() + (Bukkit.getPlayer(players).equals(party.getOwner()) ? "&7*" : ""), VituzAPI.getPing(Bukkit.getOfflinePlayer(players).getPlayer()));
							} else {
								if(Bukkit.getOfflinePlayer(players).isOnline()) {
									layout.set(0, xd, "&d&m" + Bukkit.getOfflinePlayer(players).getName() + (Bukkit.getOfflinePlayer(players).equals(party.getOwner()) ? "&7*" : "") + "&r", VituzAPI.getPing(Bukkit.getOfflinePlayer(players).getPlayer()));
								} else {
									layout.set(0, xd, "&d&m" + Bukkit.getOfflinePlayer(players).getName() + (Bukkit.getOfflinePlayer(players).equals(party.getOwner()) ? "&7*" : "") + "&r");
								}
							}

							xd++;
						}

						layout.set(0, xd + 1, "&fNether:");
						layout.set(0, xd + 2, "&d" + (nether ? "Enabled" : "Disabled"));
					} else {
						layout.set(0, 9, "&fNether:");
						layout.set(0, 10, "&d" + (nether ? "Enabled" : "Disabled"));
					}
				} else {
					layout.set(0, 9, "&fNether:");
					layout.set(0, 10, "&d" + (nether ? "Enabled" : "Disabled"));
				}

				layout.set(2, 0, "&fShrink Time:");
				layout.set(2, 1, "&d" + OptionManager.getByNameAndTranslate("First Shrink") + " mins");

				layout.set(2, 3, "&fPvP Time:");
				layout.set(2, 4, "&d" + OptionManager.getByNameAndTranslate("PvP Period Duration") + " mins");

				layout.set(2, 6, "&fHeal Time:");
				layout.set(2, 7, "&d" + OptionManager.getByNameAndTranslate("Final Heal") + " mins");
			} else {
				layout.set(0, 0, "&fGame Type:");
				layout.set(0, 1, "&d" + UHCUtils.isPartiesEnabled());

				layout.set(0, 3, "&fBorder:");
				layout.set(0, 4, "&d" + BorderManager.border);

				if(PartyManager.isEnabled()) {
					layout.set(0, 14, "&5&lYour Team");

					int xd = 15;
					Party party = PartyManager.getByPlayer(player);

					if(party != null) {
						for(String playerName : party.getPlayers()) {
							OfflinePlayer partyPlayer = Bukkit.getOfflinePlayer(playerName);

							String name = partyPlayer != null ? playerName : "Unknown";

							if(UHCData.getByName(playerName).isAlive()) {
								layout.set(0, xd, "&2" + name);
							} else {
								layout.set(0, xd, "&d&m" + name);
							}

							xd++;
						}
					}

					layout.set(0, 6, "&fTeam Info:");
					layout.set(0, 7, "&fKills: &d" + party.getKills());
					layout.set(0, 8, "&fOnline: &d" + party.getSize());

					layout.set(0, 10, "&fPlayer Info:");
					layout.set(0, 11, "&fKills: &d" + uhcData.getKills());
					layout.set(0, 12, "&fDiamonds: &d" + uhcData.getDiamondsMined());
				} else {
					layout.set(0, 6, "&fPlayer Info:");
					layout.set(0, 7, "&fKills: &d" + uhcData.getKills());
					layout.set(0, 8, "&fDiamonds: &d" + uhcData.getDiamondsMined());
				}

				layout.set(2, 0, "&fShrink Time:");
				layout.set(2, 1, "&d" + OptionManager.getByNameAndTranslate("First Shrink") + " mins");

				layout.set(2, 3, "&fPvP Time:");
				layout.set(2, 4, "&d" + OptionManager.getByNameAndTranslate("PvP Period Duration") + " mins");

				layout.set(2, 6, "&fHeal Time:");
				layout.set(2, 7, "&d" + OptionManager.getByNameAndTranslate("Final Heal") + " mins");

				if(plugin.getGameManager().isPvp()) {
					layout.set(2, 9, "&fYour Location:");

					if(uhcData.isHideLocation()) {
						layout.set(2, 10, "&dHidden Location");
					} else {
						layout.set(2, 10, "&d(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ") [" + this.getCardinalDirection(player) + "]");
					}

					layout.set(2, 12, "&fNether:");
					layout.set(2, 13, "&d" + (nether ? "Enabled" : "Disabled"));

					layout.set(2, 15, "&fSpeed Potions:");
					layout.set(2, 16, "&d" + (speed ? "Enabled" : "Disabled"));

				} else {
					layout.set(2, 9, "&fNether:");
					layout.set(2, 10, "&d" + (nether ? "Enabled" : "Disabled"));
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

	public String getCardinalDirection(Player player) {
		double rot = (player.getLocation().getYaw() - 90.0F) % 360.0F;
		if (rot < 0.0D) {
			rot += 360.0D;
		}
		return getDirection(rot);
	}

	private String getDirection(double rot) {
		if ((0.0D <= rot) && (rot < 22.5D)) {
			return "W";
		}
		if ((22.5D <= rot) && (rot < 67.5D)) {
			return "NW";
		}
		if ((67.5D <= rot) && (rot < 112.5D)) {
			return "N";
		}
		if ((112.5D <= rot) && (rot < 157.5D)) {
			return "NE";
		}
		if ((157.5D <= rot) && (rot < 202.5D)) {
			return "E";
		}
		if ((202.5D <= rot) && (rot < 247.5D)) {
			return "SE";
		}
		if ((247.5D <= rot) && (rot < 292.5D)) {
			return "S";
		}
		if ((292.5D <= rot) && (rot < 337.5D)) {
			return "SW";
		}
		if ((337.5D <= rot) && (rot < 360.0D)) {
			return "W";
		}
		return null;
	}
}
