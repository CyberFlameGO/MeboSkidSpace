package secondlife.network.vituz.ranks.grant.procedure;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import secondlife.network.vituz.ranks.Rank;
import secondlife.network.vituz.utilties.item.ItemBuilder;
import secondlife.network.vituz.utilties.WoolUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GrantProcedure {
	
	@Getter private static Set<GrantProcedure> procedures = new HashSet<GrantProcedure>();
	@Getter private static GrantRecipient recipient;
	@Getter private static UUID issuer;
	@Getter private static GrantProcedureData data;

	public GrantProcedure(GrantRecipient recipient, UUID issuer, GrantProcedureData data) {		
		GrantProcedure.recipient = recipient;
		GrantProcedure.issuer = issuer;
		GrantProcedure.data = data;
		
		procedures.add(this);
	}

	public static Inventory getInventory() {
		int size = (int) Math.ceil(Rank.getRanks().size() / 9.0);
		Inventory inventory = Bukkit.createInventory(null, (size == 0) ? 9 : (size * 9), ChatColor.BLUE + "" + ChatColor.BOLD + "Choose a rank");
		
		for(Rank rank : Rank.getRanks()) {
			if(rank.getData().isDefaultRank()) continue;

			ChatColor color;
			
			if(rank.getData().getPrefix().isEmpty()) {
				color = ChatColor.WHITE;
			} else {
				char code = 'f';
				
				for(String string : rank.getData().getPrefix().split("&")) {
					if(!string.isEmpty() && ChatColor.getByChar(string.toCharArray()[0]) != null) {
						code = string.toCharArray()[0];
					}
				}
				
				color = ChatColor.getByChar(code);
			}
			
			inventory.addItem(new ItemStack[] { new ItemBuilder(Material.WOOL)
					.durability(WoolUtil.convertChatColorToWoolData(color)).name(color + rank.getData().getName())
					.lore(Arrays.asList("&7&m------------------------------",
					"&9Click to grant &d" + getRecipient().getName() + "&9 the " + color + rank.getData().getName() + "&9 rank.",
					"&7&m------------------------------"))
					.build() });
		}
		
		return inventory;
	}

	public static GrantProcedure getByPlayer(Player player) {
		for(GrantProcedure grantProcedure : procedures) {
			if(getIssuer() != null && getIssuer().equals(player.getUniqueId())) return grantProcedure;
		}
		
		return null;
	}
}
