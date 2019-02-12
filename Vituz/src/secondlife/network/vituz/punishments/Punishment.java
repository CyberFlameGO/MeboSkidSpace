package secondlife.network.vituz.punishments;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.utilties.*;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.*;

@Getter
@Setter
public class Punishment {

	private PunishmentType type;
	private String addedBy;
	private long addedAt;
	private String reason;
	private String removedBy;
	private String removedReason;
	private String serverName;
	private long removedAt;
	private long duration;

	public Punishment(PunishmentType type, String addedBy, long addedAt, long duration, String reason, String serverName) {
		this.type = type;
		this.addedBy = addedBy;
		this.addedAt = addedAt;
		this.duration = duration;
		this.reason = reason;
		this.serverName = serverName;
	}

	public boolean isActive() {
		return this.removedReason == null && (this.duration == 2147483647L || System.currentTimeMillis() < this.addedAt + this.duration);
	}

	public boolean isPermanent() {
		return this.duration == 2147483647L;
	}

	public String getTimeLeft() {
		if(this.isPermanent()) return "Permanent";
		if(this.removedReason != null) return "Removed";
		if(!this.isActive()) return "Expired";
		
		
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		
		from.setTime(new Date(System.currentTimeMillis()));
		to.setTime(new Date(this.addedAt + this.duration));
		
		return DateUtil.formatDateDiff(from, to);
	}

	public Punishment announce(String name, String sender, boolean silent, boolean undo) {
		String context = undo ? this.type.getUndoContext() : this.type.getContext();
	
		if(Vituz.getInstance().getDatabaseManager().isDevMode()) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(player.hasPermission(Permission.STAFF_PERMISSION)) {
					player.sendMessage(Color.translate("&c" + name + " &cwas " +  context + " by " + sender + "&c."));
				} else {
					if(silent && !player.getName().equals(ChatColor.stripColor(name))) continue;

					player.sendMessage(Color.translate("&c" + name + " was " + context + " by " + sender + "."));
				}
			}	
		} else {
			if(silent) {
				ServerUtils.bungeeBroadcast("&7(Silent) &c" + name + " &cwas " + context + " &cby " + sender + "&c.", "secondlife.staff");
			} else {
				ServerUtils.bungeeBroadcast("&c" + name + " &cwas " + context + " &cby " + sender + "&c.");
			}
		}
		
		Msg.logConsole("&c" + name + " &cwas " + ((silent && this.type.name().contains("BAN") && !undo) ? ("&c&lsilently &c") : "") + context + " by " + sender + "&c.");
		Msg.logConsole("&cReason: &d" + this.reason);
		
		if(!this.isPermanent()) {
			Msg.logConsole("&cDuration: &d" + this.getTimeLeft());
		}
		
		return this;
	}

	public static Inventory getMenu(PunishData profile) {
		Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.RED + profile.getName() + (profile.getName().endsWith("s") ? "'" : "'s") + " Punishments");
		List<ItemStack> items = new ArrayList<>();
		
		for(PunishmentType type : Arrays.asList(PunishmentType.MUTE, PunishmentType.BAN, PunishmentType.BLACKLIST)) {
			items.add(new ItemBuilder(Material.WOOL).durability((type == PunishmentType.MUTE) ? 1 : ((type == PunishmentType.BAN) ? 14 : 7)).name(((type == PunishmentType.MUTE) ? ChatColor.GOLD : ((type == PunishmentType.BAN) ? ChatColor.RED : ChatColor.BLACK)) + WordUtils.capitalize(type.name().toLowerCase()) + "s").build());
		}
		
		for(int i = 0; i < 9; ++i) {
			inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").data(7).build());
		}
		
		inventory.setItem(2, items.get(0));
		inventory.setItem(4, items.get(1));
		inventory.setItem(6, items.get(2));
		
		return inventory;
	}
}
