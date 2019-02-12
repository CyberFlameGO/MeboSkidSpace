package secondlife.network.hub.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hub.Hub;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

public class MultiSpawnCommand {

	private static Hub plugin = Hub.getInstance();

	@Command(names = {"ms", "multispawn"}, permissionNode = "secondlife.op")
	public static void handleUsage(CommandSender sender) {
		sender.sendMessage(Color.translate("&cMultiSpawn - Help Commands:"));
		sender.sendMessage(Color.translate("&c/multispawn create <name> - Create spawn point."));
		sender.sendMessage(Color.translate("&c/multispawn delete <name>. - Delete spawn point."));
	}
	
	@Command(names = {"ms create", "multispawn create"}, permissionNode = "secondlife.op")
	public static void handleCreate(Player player, @Parameter(name = "name") String name) {
		Location location = player.getLocation();

		plugin.getUtilities().set("SPAWNS." + name + ".X", location.getBlockX());
		plugin.getUtilities().set("SPAWNS." + name + ".Y", location.getBlockY());
		plugin.getUtilities().set("SPAWNS." + name + ".Z", location.getBlockZ());
		plugin.getUtilities().set("SPAWNS." + name + ".WORLD", location.getWorld().getName());
		plugin.getUtilities().save();

		player.sendMessage(Color.translate("&eYou have created spawn point named &d" + name + "&e."));
	}

	@Command(names = {"ms delete", "multispawn delete"}, permissionNode = "secondlife.op")
	public static void handleDelete(CommandSender sender, @Parameter(name = "name") String name) {
		if(!plugin.getUtilities().isSet("SPAWNS." + name)) {
			sender.sendMessage(Color.translate("&cThe spawn point named &l" + name + " &cdoes not exists."));
			return;
		}

		plugin.getUtilities().set("SPAWNS." + name, null);
		sender.sendMessage(Color.translate("&eYou have deleted spawn point named &d" + name + "&e."));
	}
}
