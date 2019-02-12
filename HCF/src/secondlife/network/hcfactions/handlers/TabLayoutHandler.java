package secondlife.network.hcfactions.handlers;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.type.games.EventFaction;
import secondlife.network.hcfactions.factions.FactionMember;
import secondlife.network.hcfactions.game.events.faction.KothFaction;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.timers.GameHandler;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.hcfactions.utilties.file.LimitersFile;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.providers.LayoutProvider;
import secondlife.network.vituz.providers.tab.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TabLayoutHandler implements LayoutProvider {

	public static final Comparator<PlayerFaction> FACTION_COMPARATOR = Comparator.comparingInt(faction -> faction.getOnlinePlayers().size());

	public static final Comparator<FactionMember> ROLE_COMPARATOR = Comparator.comparingInt(member -> member.getRole().ordinal());

	public TabLayout getLayout(Player player) {
		TabLayout layout = TabLayout.create(player);

		if(player == null) return null;

		PlayerFaction faction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);
		EventFaction eventFaction = GameHandler.getEventFaction();
		HCFData data = HCFData.getByName(player.getName());

		if(PlayerData.getByName(player.getName()).isTab()) {
			layout.set(1, 0 , "&5&lSecondLife");

			if(faction == null) {
				layout.set(0, 0, "&fPlayer Info:");
				layout.set(0, 1, "&fKills: &d" + data.getKills());
				layout.set(0, 2, "&fDeaths: &d" + data.getDeaths());

				layout.set(0, 4, "&fYour Location:");
				layout.set(0, 5, RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation()).getDisplayName(player));
				layout.set(0, 6, "&d(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ")" + " [" +  this.getCardinalDirection(player)  + "]");

				if(eventFaction instanceof KothFaction) {
					KothFaction kothFaction = (KothFaction) eventFaction;
					Location center = kothFaction.getCaptureZone().getCuboid().getCenter();

					layout.set(0, 8, "&f" + kothFaction.getName());
					layout.set(0, 9, "&d" + DurationFormatUtils.formatDuration(GameHandler.getRemaining(), "mm:ss"));
					layout.set(0, 10, "&d" + center.getBlockX() + ", " + center.getBlockY() + ", " + center.getBlockZ());
				}

				layout.set(2, 0, "&fEnd Portals:");

				if(HCFConfiguration.kitMap) {
					layout.set(2, 1, "&dSpawn");
				} else {
					layout.set(2, 1, "&d700, 700");
				}

				layout.set(2, 2, "&din each quadrant");

				layout.set(2, 4, "&fKit:");
				if(HCFConfiguration.kitMap) {
					layout.set(2, 5, "&dProt 2, Sharp 1");
				} else {
					layout.set(2, 5, "&dProt " + LimitersFile.getInt("enchantment-limiter.PROTECTION_ENVIRONMENTAL") + ", Sharp " + LimitersFile.getInt("enchantment-limiter.DAMAGE_ALL"));
				}

				layout.set(2, 7, "&fBorder:");
				layout.set(2, 8, "&d" + HCFConfiguration.bordersizes.get(Environment.NORMAL));

				layout.set(2, 10, "&fPlayers Online:");
				layout.set(2, 11, "&d" + Bukkit.getOnlinePlayers().size());
			} else {
				layout.set(0, 0, "&fHome:");
				if(faction.getHome() == null) {
					layout.set(0, 1, "&dNot Set");
				} else {
					Location home = secondlife.network.vituz.utilties.StringUtils.destringifyLocation(faction.getHome());

					layout.set(0, 1, "&d" + home.getBlockX() + ", " + home.getBlockY() + ", " + home.getBlockZ());
				}

				layout.set(0, 3, "&fFaction Info");
				layout.set(0, 4, "&fDTR: &d" + faction.getRegenStatus().getSymbol() + JavaUtils.format(faction.getDeathsUntilRaidable(), 2));
				layout.set(0, 5, "&fOnline: &d" + faction.getOnlinePlayers().size() + "/" + faction.getMembers().size());
				layout.set(0, 6, "&fBalance: &d$" + faction.getBalance());

				layout.set(0, 8, "&fPlayer Info:");
				layout.set(0, 9, "&fKills: &d" + data.getKills());

				layout.set(0, 11, "&fYour Location:");
				layout.set(0, 12, RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation()).getDisplayName(player));
				layout.set(0, 13, "&d(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ")" + " [" +  this.getCardinalDirection(player)  + "]");

				if(eventFaction instanceof KothFaction) {
					KothFaction kothFaction = (KothFaction) eventFaction;
					Location center = kothFaction.getCaptureZone().getCuboid().getCenter();

					layout.set(0, 15, "&f" + kothFaction.getName());
					layout.set(0, 16, "&d" + DurationFormatUtils.formatDuration(GameHandler.getRemaining(), "mm:ss"));
					layout.set(0, 17, "&d" + center.getBlockX() + ", " + center.getBlockY() + ", " + center.getBlockZ());
				}

				layout.set(1, 2, "&fPlayers Online:");
				layout.set(2, 11, "");
				layout.set(1, 3, "&d" + Bukkit.getOnlinePlayers().size());

				layout.set(1, 5, "&5&l" + faction.getName());

				List<FactionMember> members = new ArrayList<>(faction.getMembers().values().stream().filter(member -> Bukkit.getPlayer(member.getName()) != null).collect(Collectors.toList()));
				Collections.sort(members, ROLE_COMPARATOR);

				for(int i = 5; i < 20; i++) {
					int exact = i - 5;

					if(members.size() <= exact) {
						continue;
					}

					if(i == 19 && members.size() > 19) {
						layout.set(1, i, "&fand &d" + (members.size() - 19) + " &fmore...");
						continue;
					}

					FactionMember member = members.get(exact);
					layout.set(1, i + 1, "&2" + member.getName() + "&7" + member.getRole().getAstrix(), member.getPing());
				}

				layout.set(2, 0, "&fFaction List:");

				List<PlayerFaction> factions = new ArrayList<>(RegisterHandler.getInstancee().getFactionManager().getFactions().stream().filter(x -> x instanceof PlayerFaction).map(x -> (PlayerFaction) x).filter(x -> x.getOnlineMembers().size() > 0).collect(Collectors.toSet()));
				Collections.sort(factions, FACTION_COMPARATOR);
				Collections.reverse(factions);

				for(int i = 0; i < 20; i++) {
					if(i >= factions.size()) {
						break;
					}

					PlayerFaction next = factions.get(i);

					layout.set(2, i + 1, next.getTabDisplayName(player) + " &f(&d" + next.getOnlinePlayers().size() + "&f)");
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
