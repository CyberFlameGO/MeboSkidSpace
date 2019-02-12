package secondlife.network.practice.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.CustomLocation;
import secondlife.network.practice.utilties.PlayerUtil;
import secondlife.network.vituz.utilties.Permission;

public class SpawnsCommand extends Command {
	private final Practice plugin = Practice.getInstance();

	public SpawnsCommand() {
		super("spawns");
		this.setDescription("Manage server spawns.");
		this.setUsage(CC.RED + "Usage: /spawn <subcommand>");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player) || !PlayerUtil.testPermission(sender, Permission.OP_PERMISSION)) return true;

		if(args.length < 1) {
			sender.sendMessage(usageMessage);
			return true;
		}

		Player player = (Player) sender;

		switch (args[0].toLowerCase()) {
			case "spawnlocation":
				this.plugin.getSpawnManager().setSpawnLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the spawn location.");
				break;
			case "spawnmin":
				this.plugin.getSpawnManager().setSpawnMin(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the spawn min.");
				break;
			case "spawnmax":
				this.plugin.getSpawnManager().setSpawnMax(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the spawn max.");
				break;
			case "editorlocation":
				this.plugin.getSpawnManager().setEditorLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the editor location.");
				break;
			case "editormin":
				this.plugin.getSpawnManager().setEditorMin(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the editor min.");
				break;
			case "editormax":
				this.plugin.getSpawnManager().setEditorMax(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the editor max.");
				break;
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
			case "oitclocation":
				this.plugin.getSpawnManager().setOitcLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the OITC location.");
				break;
			case "oitcmin":
				this.plugin.getSpawnManager().setOitcMin(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the OITC min.");
				break;
			case "oitcmax":
				this.plugin.getSpawnManager().setOitcMax(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the OITC max.");
				break;
			case "oitcspawnpoints":
				this.plugin.getSpawnManager().getOitcSpawnpoints().add(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the OITC spawn-point #" + this.plugin.getSpawnManager().getOitcSpawnpoints().size() + ".");
				break;
			case "parkourlocation":
				this.plugin.getSpawnManager().setParkourLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the parkour location.");
				break;
			case "parkourgamelocation":
				this.plugin.getSpawnManager().setParkourGameLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the parkour Game location.");
				break;
			case "parkourmax":
				this.plugin.getSpawnManager().setParkourMax(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the parkour max location.");
				break;
			case "parkourmin":
				this.plugin.getSpawnManager().setParkourMin(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the parkour min location.");
				break;
			case "redroverlocation":
				this.plugin.getSpawnManager().setRedroverLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the redrover location.");
				break;
			case "redroverfirst":
				this.plugin.getSpawnManager().setRedroverFirst(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the redrover location A.");
				break;
			case "redroversecond":
				this.plugin.getSpawnManager().setRedroverSecond(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the redrover location B.");
				break;
			case "redrovermin":
				this.plugin.getSpawnManager().setRedroverMin(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the redrover min.");
				break;
			case "redrovermax":
				this.plugin.getSpawnManager().setRedroverMax(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the redrover max.");
				break;
		}

		return false;
	}
}
