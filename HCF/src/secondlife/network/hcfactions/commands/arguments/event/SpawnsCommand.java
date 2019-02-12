package secondlife.network.hcfactions.commands.arguments.event;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.CustomLocation;
import secondlife.network.vituz.utilties.Permission;

public class SpawnsCommand extends Command {
	private final HCF plugin = HCF.getInstance();

	public SpawnsCommand() {
		super("spawns");
		this.setDescription("Manage server spawns.");
		this.setUsage("Usage: /spawn <subcommand>");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player) || !sender.hasPermission(Permission.OP_PERMISSION)) return true;

		if(args.length < 1) {
			sender.sendMessage(usageMessage);
			return true;
		}

		Player player = (Player) sender;

		switch (args[0].toLowerCase()) {
			case "sumolocation":
				this.plugin.getSpawnManager().setSumoLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the sumo location.");
				break;
			case "sumofirst":
				this.plugin.getSpawnManager().setSumoFirst(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the sumo location A.");
				break;
			case "sumosecond":
				this.plugin.getSpawnManager().setSumoSecond(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the sumo location B.");
				break;
			case "sumomin":
				this.plugin.getSpawnManager().setSumoMin(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the sumo min.");
				break;
			case "sumomax":
				this.plugin.getSpawnManager().setSumoMax(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the sumo max.");
				break;
		}

		return false;
	}
}
