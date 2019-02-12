package secondlife.network.vituz;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.data.RankData;
import secondlife.network.vituz.ranks.Rank;
import secondlife.network.vituz.ranks.grant.Grant;
import secondlife.network.vituz.status.ServerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;
import secondlife.network.vituz.utilties.Tasks;

import java.util.ArrayList;
import java.util.List;

public class VituzAPI {

	public static int scoreboardTime = 2;
	public static int nametagsTime = 2;
	public static int tabTime = 6;

	public static String getServerName() {
		return Vituz.getInstance().getConfig().getString("SERVERNAME");
	}

	public static void setMaxPlayers(int slots) {
		StringUtils.setSlots(slots);
	}

	public static void dispatchCommandOnMainThread(String command) {
		Tasks.run(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
	}

	public static String getVersion(Player player) {
		String version = null;

		switch(ProtocolSupportAPI.getProtocolVersion(player)) {
			case MINECRAFT_1_7_5:
				version = "1.7.5";
				break;
			case MINECRAFT_1_7_10:
				version = "1.7.10";
				break;
			case MINECRAFT_1_8:
				version = "1.8";
				break;
		}

		return version;
	}

	public static int getPing(Player player) {
		int ping = ((CraftPlayer) player).getHandle().ping;

		return (int) Math.floor(ping / 2.3D);
	}

	public static String getAppealAt() {
		return Vituz.getInstance().getEssentialsManager().getAppealAt();
	}

	public static ServerData getServerData(String server) {
		ServerData data = ServerData.getByName(server);

		if(data == null) {
			return null;
		}

		return data;
	}

	public static String getNamePrefix(Player player) {
		RankData profile = RankData.getByName(player.getName());

		if(hasPunishData(player) && isBanned(player)) {
			return Color.translate("&o&l&m");
		}

		Rank rank = profile.getActiveGrant().getRank();

		if(rank.getData().getPrefix().isEmpty()) return "";

		char code = 'f';

		for(String string : rank.getData().getPrefix().split("&")) {
			if(!string.isEmpty() && org.bukkit.ChatColor.getByChar(string.toCharArray()[0]) != null) {
				code = string.toCharArray()[0];
			}
		}

		org.bukkit.ChatColor color = org.bukkit.ChatColor.getByChar(code);

		return color.toString();
	}

	public static String getFullNamePrefix(Player player) {
		if(hasRanksData(player)) {
			return getNamePrefix(player) + player.getName();
		}

		return null;
	}

	public static boolean hasPunishData(Player player) {
		PunishData profile = PunishData.getByName(player.getName());

		return true;
	}

	public static boolean hasRanksData(Player player) {
		RankData profile = RankData.getByName(player.getName());

		return true;
	}
	
	public static boolean hasEssentialsData(Player player) {
		PlayerData profile = PlayerData.getByName(player.getName());

		return true;
	}

	public static boolean isBanned(Player player) {
		PunishData profile = PunishData.getByName(player.getName());

		return profile.isBanned();
	}
	
	public static boolean isBlacklisted(Player player) {
		PunishData profile = PunishData.getByName(player.getName());

		return profile.isBlacklisted();
	}
	
	public static boolean isMuted(Player player) {
		PunishData profile = PunishData.getByName(player.getName());

		if(profile.isMuted()) {
			return true;
		}

		return false;
	}

	public static boolean isIPBanned(Player player) {
		PunishData profile = PunishData.getByName(player.getName());

		return profile.isIPBanned();
	}
	
	public static void removePermission(Player player, String permission) {
		RankData data = RankData.getByName(player.getName());
		
		data.getPermissions().remove(permission);
	}
	
	public static String getColorPrefix(Player player) {
		RankData data = RankData.getByName(player.getName());

		Grant grant = data.getActiveGrant();
		
		return grant.getRank().getData().getColorPrefix();
	}

	public static String getPrefix(Player player) {
		RankData data = RankData.getByName(player.getName());
		Grant grant = data.getActiveGrant();
		
		return grant.getRank().getData().getPrefix();
	}

	public static Rank getRank(Player player) {
		RankData data = RankData.getByName(player.getName());
		Grant grant = data.getActiveGrant();

		return grant.getRank();
	}
	
	public static String getRankName(String name) {
		RankData data = RankData.getByName(name);

		if(!data.isLoaded()) {
			data.load();
		}

		Grant grant = data.getActiveGrant();
		
		return grant.getRank().getData().getName();
	}

	public static boolean canPunish(CommandSender sender, String target) {
		if(sender instanceof ConsoleCommandSender || sender.isOp()) {
			return true;
		}

		List<String> staff = new ArrayList<>();

		Rank.getRanks().forEach(rank -> {
			String prefix = rank.getData().getPrefix();
			if(prefix.contains("Mod") || prefix.contains("Admin") || prefix.contains("Owner")) {
				staff.add(rank.getData().getName());
			}
		});

		if(staff.contains(getRankName(sender.getName())) && staff.contains(getRankName(target))) {
			return false;
		}

		return true;
	}
	
	public static String getSuffix(Player player) {
		RankData data = RankData.getByName(player.getName());
		Grant grant = data.getActiveGrant();
		
		return grant.getRank().getData().getSuffix();
	}
}
