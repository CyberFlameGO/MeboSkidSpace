package secondlife.network.hcfactions.staff;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class StaffPlayerData {

	private ItemStack[] contents;
	private ItemStack[] armor;
	private GameMode gameMode;

	public StaffPlayerData() { }
}
