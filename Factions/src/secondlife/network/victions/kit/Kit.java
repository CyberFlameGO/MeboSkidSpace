package secondlife.network.victions.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.vituz.utilties.Color;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Kit {

	private final String name;
	private ItemStack[] contents = new ItemStack[36];
	private ItemStack[] armor = new ItemStack[4];
	private ItemStack icon;
	private boolean enabled;
	private int delay;

	public void applyToPlayer(Player player) {
		player.getInventory().setContents(contents);
		player.getInventory().setArmorContents(armor);
		player.updateInventory();
		player.sendMessage(Color.translate("&eYou have received &d" + name + " &ekit."));

		FactionsData data = FactionsData.getByName(player.getName());
		data.applyKitCooldown(this);
	}
}
