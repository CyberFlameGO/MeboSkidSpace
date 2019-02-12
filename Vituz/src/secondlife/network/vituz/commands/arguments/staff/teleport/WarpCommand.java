package secondlife.network.vituz.commands.arguments.staff.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.NumberUtils;
import secondlife.network.vituz.utilties.Permission;

public class WarpCommand extends BaseCommand {

	public WarpCommand(Vituz plugin) {
		super(plugin);

		this.command = "warp";
		this.permission = Permission.STAFF_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /warp <set|delete|list> <name>"));
		} else {
			if(args[0].equalsIgnoreCase("set")) {
				if(player.hasPermission(Permission.OP_PERMISSION)) {
					if(NumberUtils.isInteger(args[1])) {
						player.sendMessage(Color.translate("&cThis must be an integer."));
						return;
					}

					if(plugin.getUtilities().getConfigurationSection("warps") != null) {
						if(plugin.getUtilities().getConfigurationSection("warps").contains(args[1].toLowerCase())) {
							player.sendMessage(Color.translate("&cWarp '" + args[1] + "' already exsits."));
							return;
						}
					}

					plugin.getUtilities().set("warps." + args[1].toLowerCase() + ".world", player.getWorld().getName());
					plugin.getUtilities().set("warps." + args[1].toLowerCase() + ".x", player.getLocation().getX());
					plugin.getUtilities().set("warps." + args[1].toLowerCase() + ".y", player.getLocation().getY());
					plugin.getUtilities().set("warps." + args[1].toLowerCase() + ".z", player.getLocation().getZ());
					plugin.getUtilities().set("warps." + args[1].toLowerCase() + ".yaw", player.getLocation().getYaw());
					plugin.getUtilities().set("warps." + args[1].toLowerCase() + ".pitch", player.getLocation().getPitch());
					plugin.getUtilities().save();

					player.sendMessage(Color.translate("&eYou have set warp named &d" + args[1]));
				} else {
					player.sendMessage(Msg.NO_PERMISSION);
				}
			} else if(args[0].equalsIgnoreCase("delete")) {
				if(player.hasPermission(Permission.OP_PERMISSION)) {
					if(plugin.getUtilities().getConfigurationSection("warps") != null) {
						if(plugin.getUtilities().getConfigurationSection("warps").contains(args[1].toLowerCase())) {
							ConfigurationSection section = plugin.getUtilities().getConfigurationSection("warps");

							for(String name : section.getKeys(false)) {
								if(plugin.getEssentialsManager().getWarps().contains(name)) {
									plugin.getEssentialsManager().getWarps().remove(name);
								}
							}

							plugin.getUtilities().set("warps." + args[1].toLowerCase(), null);
							plugin.getUtilities().save();

							player.sendMessage(Color.translate("&eYou have deleted warp named &d" + args[1] + "&e."));
						} else {
							player.sendMessage(Color.translate("&cWarp '" + args[1] + "' doesn't exsits."));
						}
					}
				} else {
					player.sendMessage(Msg.NO_PERMISSION);
				}
			} else if(args[0].equalsIgnoreCase("list")) {
				ConfigurationSection section = plugin.getUtilities().getConfigurationSection("warps");

				if(section == null) {
					player.sendMessage(Color.translate("&cThere are no warps set yet."));
					return;
				}

				for(String name : section.getKeys(false)) {
					if(!plugin.getEssentialsManager().getWarps().contains(name.replace(name.charAt(0), name.toUpperCase().charAt(0)))) {
						plugin.getEssentialsManager().getWarps().add(name.replace(name.charAt(0), name.toUpperCase().charAt(0)));
					}
				}

				if(!plugin.getEssentialsManager().getWarps().isEmpty()) {
					player.sendMessage(Color.translate("&eCurrent warps&7: &d" + plugin.getEssentialsManager().getWarps().toString().replace("[", "").replace("]", "").replace(",", "&7" + "&d")));
				} else {
					player.sendMessage(Color.translate("&cThere are no warps set yet."));

				}
			} else {
				if(plugin.getUtilities().getConfigurationSection("warps") != null) {
					if(plugin.getUtilities().getConfigurationSection("warps").contains(args[0].toLowerCase())) {
						World world = Bukkit.getWorld(plugin.getUtilities().getString("warps." + args[0].toLowerCase() + ".world"));

						int x = (int) plugin.getUtilities().getInt("warps." + args[0].toLowerCase() + ".x");
						int y = (int) plugin.getUtilities().getInt("warps." + args[0].toLowerCase() + ".y");
						int z = (int) plugin.getUtilities().getInt("warps." + args[0].toLowerCase() + ".z");

						float yaw = (float) plugin.getUtilities().getFloat("warps." + args[0].toLowerCase() + ".yaw");
						float pitch = (float) plugin.getUtilities().getFloat("warps." + args[0].toLowerCase() + ".pitch");

						Location location = new Location(world, x, y, z, yaw, pitch);

						player.teleport(location);

						player.sendMessage(Color.translate("&eYou have been warped to warp named &d" + args[0]));
					} else {
						player.sendMessage(Color.translate("&cWarp '" + args[0] + "' doesn't exsits."));
					}
				}

			}
		}
	}
}
