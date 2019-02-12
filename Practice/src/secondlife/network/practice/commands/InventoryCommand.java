package secondlife.network.practice.commands;

import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.Practice;
import secondlife.network.practice.inventory.InventorySnapshot;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventoryCommand extends Command {

	private final static Pattern UUID_PATTERN = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
	private final static String INVENTORY_NOT_FOUND = CC.RED + "Inventory not found.";

	private final Practice plugin = Practice.getInstance();

	public InventoryCommand() {
		super("inv");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;

		if(args.length == 0) return true;

		if(!args[0].matches(InventoryCommand.UUID_PATTERN.pattern())) {
			sender.sendMessage(InventoryCommand.INVENTORY_NOT_FOUND);
			return true;
		}

		InventorySnapshot snapshot = this.plugin.getInventoryManager().getSnapshot(UUID.fromString(args[0]));

		if(snapshot == null) {
			sender.sendMessage(InventoryCommand.INVENTORY_NOT_FOUND);
		} else {
			((Player) sender).openInventory(snapshot.getInventoryUI().getCurrentPage());
		}
		return true;
	}
}
