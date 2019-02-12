package secondlife.network.victions.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import secondlife.network.victions.Victions;
import secondlife.network.vituz.utilties.Color;

import java.util.stream.Stream;

@Getter
@Setter
@AllArgsConstructor
public class PlayerKit {

	private final String name;
	private final int index;

	private ItemStack[] contents;
	private String displayName;

	public void applyToPlayer(Player player) {
		for (ItemStack itemStack : contents) {
			if (itemStack != null) {
				if (itemStack.getAmount() <= 0) {
					itemStack.setAmount(1);
				}
			}
		}

		player.getInventory().setContents(contents);
		player.getInventory().setArmorContents(Victions.getInstance().getKitManager().getKit(name).getArmor());
		player.updateInventory();
		player.sendMessage(Color.translate("&eGiving you &d" + displayName + "&e."));
	}

}
